package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.game.quest.Quest;
import dualquest.game.quest.QuestManager;
import dualquest.lobby.sign.LobbySignManager;
import dualquest.util.Broadcaster;
import dualquest.util.TaskManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Updates all in-game processes, e.q. game states, win/lose e.t.c. For pre-game mechanics look {@link GameStartManager}
 */
public class GameProcess implements Listener {

	private static int timeToNextPhase = 0;

	public static void update() {
		String actionBar = null;
		switch(GameState.getState()) {
		case VOTING:
			if(TaskManager.isSecUpdated()) {
				actionBar = ChatColor.YELLOW + "Голосование: " + ChatColor.AQUA + TaskManager.formatTime(timeToNextPhase);
				if(timeToNextPhase <= 0) {
					GameStartManager.endVoting();
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
							player.sendMessage(ChatColor.GOLD + "Твоя основная задача - выжить. Противоположная команда будет пытаться тебя убить. После смерти ты не респавнишься, пока твоя команда не выполнит квест.");
							player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Удачи!");
						} else {
							player.sendMessage(ChatColor.GOLD + "Твоя задача - убить всю противоположную команду любыми способами. После смерти ты респавнишься на своей базе.");
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
				if(timeToNextPhase <= 0) {
					GameStartManager.endPreparing();
				}
			}
			break;
		case GAME:
			QuestManager.update();
			break;
		case ENDING:
			if(TaskManager.isSecUpdated()) {
				if(timeToNextPhase <= 0) {
					endGame();
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

	public static int getTimeToNextPhase() {
		return timeToNextPhase;
	}

	public static void setTimeToNextPhase(int timeInSeconds) {
		timeToNextPhase = timeInSeconds;
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
