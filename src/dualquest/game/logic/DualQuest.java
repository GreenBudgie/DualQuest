package dualquest.game.logic;

import dualquest.game.Plugin;
import dualquest.game.player.PlayerHandler;
import dualquest.lobby.LobbyEntertainmentHandler;
import dualquest.lobby.LobbyParkourHandler;
import dualquest.lobby.sign.LobbySignManager;
import dualquest.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class DualQuest implements Listener {

	private DualQuest() {
	}

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new DualQuest(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new PlayerHandler(), Plugin.INSTANCE);
		WorldManager.init();
		TaskManager.init();
		LobbyParkourHandler.init();
		LobbyEntertainmentHandler.init();
		LobbySignManager.init();
	}

	public static String getLogo() {
		return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Dual" + ChatColor.RESET + ChatColor.GREEN + "Quest";
	}

	public static void endGame() {
		if(!GameState.isPlaying()) throw new IllegalStateException("Cannot end the game at the current moment! It isn't running");
		if(GameState.isPreGame()) {
			GameStartManager.removeGlassPlatforms();
		}
		for(Player player : PlayerHandler.getInGamePlayers()) {
			PlayerHandler.reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
		}
		PlayerHandler.getSpectators().clear();
		WorldManager.deleteWorld();
		GameState.STOPPED.set();
	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}

}
