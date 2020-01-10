package dualquest.game;

import de.slikey.effectlib.EffectManager;
import dualquest.lobby.LobbyEntertainmentHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DualQuest implements Listener {

	public static EffectManager effectManager;

	private DualQuest() {
	}

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new DualQuest(), Plugin.INSTANCE);
		Bukkit.getPluginManager().registerEvents(new LobbyEntertainmentHandler(), Plugin.INSTANCE);
		LobbyEntertainmentHandler.init();
		WorldManager.init();
	}

	public static void startGame() {

	}

	public static void endGame() {

	}

	public static boolean isInLobby(Player p) {
		return WorldManager.getLobby().getPlayers().contains(p);
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
