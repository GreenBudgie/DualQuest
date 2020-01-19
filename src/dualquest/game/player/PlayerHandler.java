package dualquest.game.player;

import com.google.common.collect.Streams;
import dualquest.game.logic.ScoreboardHandler;
import dualquest.game.logic.WorldManager;
import dualquest.util.Broadcaster;
import dualquest.util.WorldUtils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles everything that can happen to players in the game
 */
public class PlayerHandler implements Listener {

	private static PlayerList playerList = PlayerList.empty();
	private static List<Player> spectators = new ArrayList<>();

	public static PlayerList getPlayerList() {
		return playerList == null ? PlayerList.empty() : playerList;
	}

	public static void initPlayerList(Collection<? extends Player> players) {
		playerList = new PlayerList(players.stream().map(DQPlayer::new).collect(Collectors.toList()));
	}

	public static void clearPlayerList() {
		playerList = PlayerList.empty();
	}

	public static List<Player> getSpectators() {
		return spectators;
	}

	public static boolean isPlaying(Player player) {
		return playerList.getPlayers().contains(player);
	}

	public static boolean isSpectator(Player player) {
		return spectators.contains(player);
	}

	public static boolean isInGame(Player player) {
		return isPlaying(player) || isSpectator(player);
	}

	public static boolean isInLobby(Player player) {
		return WorldManager.getLobby().getPlayers().contains(player);
	}

	/**
	 * Gets all players that are currently in-game, spectators and players
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static List<Player> getInGamePlayers() {
		return Streams.concat(playerList.getPlayers().stream(), spectators.stream()).collect(Collectors.toList());
	}

	public static List<Player> getLobbyPlayers() {
		return WorldManager.getLobby().getPlayers();
	}

	public static void update() {
		for(DQPlayer dqPlayer : playerList.getDQPlayers()) {
			if(dqPlayer.isValid()) {
				dqPlayer.update();
			}
		}
	}

	public static void joinSpectators(Player player) {
		reset(player);
		player.teleport(WorldManager.getSpectatorsSpawn());
		spectators.add(player);
	}

	public static void reset(Player player) {
		player.getInventory().clear();
		player.getActivePotionEffects().forEach(ef -> player.removePotionEffect(ef.getType()));
		heal(player);
		player.setFireTicks(0);
		player.setNoDamageTicks(0);
		player.setExp(0);
		player.setLevel(0);
	}

	public static void heal(Player player) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.setHealth(20);
		player.setSaturation(20);
		player.setExhaustion(20);
		player.setFoodLevel(20);
	}

	@EventHandler
	public void deadPlayerInteract(PlayerArmorStandManipulateEvent e) {
		ArmorStand stand = e.getRightClicked();
		if(stand.hasMetadata("dead_player")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void deadPlayerInteract(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof ArmorStand) {
			Player player = (Player) e.getDamager();
			ArmorStand stand = (ArmorStand) e.getEntity();
			if(stand.hasMetadata("dead_player")) {
				for(DQPlayer dqPlayer : getPlayerList()) {
					if(dqPlayer.getQuitStand() == stand) {
						dqPlayer.killAsArmorStand(DQPlayer.fromPlayer(player));
						break;
					}
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void preJoin(AsyncPlayerPreLoginEvent e) {
		if(WorldManager.generating) {
			e.setKickMessage(ChatColor.GOLD + "Сейчас идет генерация мира, зайди немного позже");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Player player = e.getEntity();
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		if(dqPlayer != null && dqPlayer.isValid()) {

		}
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		if(dqPlayer != null && dqPlayer.isValid() && e.getTo() != null) {
			boolean spectating = (dqPlayer.isTemporaryDead() && !dqPlayer.isSpectatingTeammates()) || dqPlayer.isRespawning();
			if(spectating && !WorldUtils.compareIntegerLocations(e.getFrom(), e.getTo())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void teleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		if(dqPlayer != null && (dqPlayer.isRespawning() || dqPlayer.isTemporaryDead()) && e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player player = e.getPlayer();
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		if(dqPlayer == null) {
			reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
			Broadcaster.inWorld(WorldManager.getLobby()).and(player).toChat(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " присоединился");
		} else {
			dqPlayer.rejoin(player);
		}
		ScoreboardHandler.updateScoreboardTeams();
	}

	@EventHandler
	public void leave(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player player = e.getPlayer();
		if(isSpectator(player)) {
			reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
			Broadcaster.inWorld(WorldManager.getGameWorld()).toChat(ChatColor.GRAY + player.getName() + ChatColor.DARK_GRAY + " отключился");
		}
		if(isPlaying(player)) {
			DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
			if(dqPlayer != null) dqPlayer.quit();
		}
		if(isInLobby(player)) {
			Broadcaster.inWorld(WorldManager.getLobby()).toChat(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " отключился");
		}
		ScoreboardHandler.updateScoreboardTeams();
	}

}
