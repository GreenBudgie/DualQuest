package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.game.quest.QuestManager;
import dualquest.lobby.sign.LobbySignManager;
import dualquest.util.Broadcaster;
import dualquest.util.NumericalCases;
import dualquest.util.TaskManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.annotation.Nullable;

/**
 * Updates all in-game processes, e.q. game states, win/lose e.t.c. For pre-game mechanics look {@link GameStartManager}
 */
public class GameProcess implements Listener {

	private static int timeToNextPhase = 0;
	public static boolean skipState = false; //Debug field

	public static void update() {
		String actionBar = null;
		switch(GameState.getState()) {
		case VOTING:
			if(TaskManager.isSecUpdated()) {
				actionBar = ChatColor.YELLOW + "Голосование: " + ChatColor.AQUA + TaskManager.formatTime(timeToNextPhase);
				if(timeToNextPhase <= 0 || skipState) {
					GameStartManager.endVoting();
					skipState = false;
				}
			}
			break;
		case PREPARING:
			if(TaskManager.isSecUpdated()) {
				if(timeToNextPhase == 20) {
					Broadcaster.inWorld(WorldManager.getGameWorld()).title(DualQuest.getLogo(), null, 30, 70, 20).sound(Sound.BLOCK_BEACON_POWER_SELECT, 0.4F, 0.8F);
				}
				if(timeToNextPhase == 14) {
					for(Player player : PlayerHandler.getPlayerList().getPlayers()) {
						player.sendTitle(ChatColor.LIGHT_PURPLE + "Команда " + PlayerTeam.getTeam(player).getCases().getGenitive(), null, 8, 80, 30);
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 1F);

						player.sendMessage(StringUtils.repeat(ChatColor.DARK_GRAY + "-", 10) + " " + DualQuest.getLogo() + " " + StringUtils
								.repeat(ChatColor.DARK_GRAY + "-", 10));
						player.sendMessage("");
						if(PlayerTeam.getTeam(player) == PlayerTeam.QUESTERS) {
							player.sendMessage(ChatColor.GOLD
									+ "Твоя основная задача - выжить. Противоположная команда будет пытаться тебя убить. После смерти ты не респавнишься, пока твоя команда не выполнит квест.");
							player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Удачи!");
						} else {
							player.sendMessage(ChatColor.GOLD
									+ "Твоя задача - убить всю противоположную команду любыми способами. После смерти ты респавнишься на своей базе.");
							player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Удачи!");
						}
						player.sendMessage("");
						player.sendMessage(StringUtils.repeat(ChatColor.DARK_GRAY + "-", 30));
					}
				}
				if(timeToNextPhase <= 3 && timeToNextPhase > 0) {
					Broadcaster.inWorld(WorldManager.getGameWorld()).title(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + timeToNextPhase, "", 0, 100, 0)
							.sound(Sound.BLOCK_COMPARATOR_CLICK, 0.5F, timeToNextPhase == 3 ? 1.2F : (timeToNextPhase == 2 ? 1.1F : 1F));
				}
				actionBar = ChatColor.YELLOW + "Подготовка: " + ChatColor.AQUA + TaskManager.formatTime(timeToNextPhase);
				if(timeToNextPhase <= 0 || skipState) {
					GameStartManager.endPreparing();
					skipState = false;
				}
			}
			break;
		case GAME:
			QuestManager.update();
			if(skipState) {
				initDeathmatch();
				skipState = false;
			}
			break;
		case DEATHMATCH:
			if(skipState) {
				win(null);
				GameState.ENDING.set();
			}
			break;
		case ENDING:
			if(TaskManager.isSecUpdated()) {
				if(timeToNextPhase <= 0 || skipState) {
					endGame();
					skipState = false;
				}
			}
			break;
		}
		if(TaskManager.isSecUpdated()) {
			if(actionBar != null) {
				Broadcaster.inWorld(WorldManager.getGameWorld()).toActionBar(actionBar);
			}
			for(Player player : PlayerHandler.getInGamePlayers()) {
				ScoreboardHandler.updateGameScoreboard(player);
			}
			if(timeToNextPhase > 0) {
				timeToNextPhase--;
			}
		}
		PlayerHandler.update();
	}

	public static void initDeathmatch() {
		PlayerHandler.moveDeadQuestersToSpectators();

		WorldBorder border = WorldManager.getGameWorld().getWorldBorder();
		int time = (int) (border.getSize() / 4);
		border.setSize(35 + PlayerHandler.getPlayerList().getDQPlayers().size() * 6, time);
		border.setWarningTime(15);
		border.setWarningDistance(2);
		border.setDamageBuffer(1);
		border.setDamageAmount(0.3);

		int minutes = time / 60;
		int seconds = time % 60;
		String minuteInfo = new NumericalCases("минуты", "минут", "минут").byNumber(minutes);
		String secondInfo = new NumericalCases("секунды", "секунд", "секунд").byNumber(seconds);
		for(Player player : PlayerHandler.getInGamePlayers()) {
			player.sendTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "Дезматч!",
					ChatColor.YELLOW + "Сбор на " + ChatColor.AQUA + WorldManager.getSpawnLocation().getBlockX() + " " + WorldManager.getSpawnLocation().getBlockZ()
							+ ChatColor.YELLOW + " в течение " + ChatColor.DARK_AQUA + minutes + " " + minuteInfo + " " + seconds + " " + secondInfo, 20, 60, 30);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 1F);
		}
		GameState.DEATHMATCH.set(time + 5 * 60);
	}

	public static int getTimeToNextPhase() {
		return timeToNextPhase;
	}

	public static void setTimeToNextPhase(int timeInSeconds) {
		timeToNextPhase = timeInSeconds;
	}

	/**
	 * Forces the given team to win the game. The opposite team loses it
	 * @param team The winner team, or null if tied
	 */
	public static void win(@Nullable PlayerTeam team) {
		QuestManager.cleanup();
		for(Player player : PlayerHandler.getInGamePlayers()) {
			PlayerHandler.reset(player);
			player.setGameMode(GameMode.SPECTATOR);
		}
		if(team == null) {
			for(Player player : PlayerHandler.getInGamePlayers()) {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.9F);
				player.sendTitle(ChatColor.YELLOW + "Ничья!", "", 5, 60, 20);
			}
		} else {
			for(Player winner : PlayerHandler.getPlayerList().selector().team(team).selectPlayers()) {
				winner.playSound(winner.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
				winner.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Победа!", "", 5, 60, 20);
				Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
				FireworkMeta meta = firework.getFireworkMeta();
				meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).withFlicker().build());
				firework.setFireworkMeta(meta);
			}
			for(Player loser : PlayerHandler.getPlayerList().selector().team(team.opposite()).selectPlayers()) {
				loser.playSound(loser.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 0.5F);
				loser.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Проигрыш", "", 5, 60, 20);
			}
		}
		GameState.ENDING.set(10);
	}

	public static void endGame() {
		if(!GameState.isPlaying()) throw new IllegalStateException("Cannot end the game at the current moment! It isn't running");
		if(GameState.isPreGame()) {
			GameStartManager.removeGlassPlatforms();
			GameStartManager.hideBar();
		}
		for(Player player : PlayerHandler.getInGamePlayers()) {
			PlayerHandler.reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
			player.setGameMode(GameMode.SURVIVAL);
			player.setScoreboard(ScoreboardHandler.lobbyScoreboard);
		}
		PlayerHandler.clearPlayerList();
		ScoreboardHandler.updateScoreboardTeamsLater();
		GameStartManager.removeInfoStands();
		PlayerHandler.getSpectators().clear();
		if(!WorldManager.keepMap) {
			WorldManager.deleteWorld();
		}
		QuestManager.cleanup();
		GameState.STOPPED.set();
		LobbySignManager.updateSigns();
	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}

}
