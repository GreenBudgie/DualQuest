package dualquest.game.logic;

import de.slikey.effectlib.EffectManager;
import dualquest.game.Plugin;
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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class DualQuest implements Listener {

	public static EffectManager effectManager;

	private DualQuest() {
	}

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new DualQuest(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new LobbyEntertainmentHandler(), Plugin.INSTANCE);
		WorldManager.init();
		TaskManager.init();
		LobbyParkourHandler.init();
		LobbyEntertainmentHandler.init();
		LobbySignManager.init();
	}

	public static void startGame() {

	}

	public static void endGame() {

	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void preJoin(AsyncPlayerPreLoginEvent e) {
		if(WorldManager.generating) {
			e.setKickMessage(ChatColor.GOLD + "Сейчас идет генерация мира, зайди немного позже");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		}
	}

}
