package dualquest.game.logic;

import dualquest.game.Plugin;
import dualquest.game.player.PlayerHandler;
import dualquest.game.quest.QuestManager;
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

	private static int endingTimer;

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new DualQuest(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new PlayerHandler(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new GameStartManager(), Plugin.INSTANCE);
		WorldManager.init();
		TaskManager.init();
		LobbyParkourHandler.init();
		LobbyEntertainmentHandler.init();
		LobbySignManager.init();
		QuestManager.init();
		ScoreboardHandler.createLobbyScoreboard();
	}

	public static String getLogo() {
		return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Dual" + ChatColor.RESET + ChatColor.GREEN + "Quest" + ChatColor.RESET;
	}

	public static void update() {
		if(GameState.isPlaying()) {
			GameProcess.update();
		}
	}

}
