package dualquest.lobby;

import com.google.common.collect.Lists;
import dualquest.game.DualQuest;
import dualquest.game.Plugin;
import dualquest.game.WorldManager;
import dualquest.util.EntityUtils;
import dualquest.util.WorldUtils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyParkourHandler implements Listener {

	private static final World lobby = WorldManager.getLobby();
	public static Map<Player, LobbyParkour> passingParkours = new HashMap<>();
	public static List<LobbyParkour> parkours = new ArrayList<>();

	public static void init() {
		LobbyParkour easy = new LobbyParkour();
		easy.setName(ChatColor.GREEN + "Изи");
		easy.setStartLocation(new Location(lobby, 24, 11, -1));
		easy.setFinishLocation(new Location(lobby, 32, 20, -16));
		easy.setCheckpointLocation(new Location(lobby, 21.5, 10, 2.5, 225, 0));
		easy.setSignLocation(new Location(lobby, 23, 10, 1));

		LobbyParkour medium = new LobbyParkour();
		medium.setName(ChatColor.DARK_GREEN + "Средний");
		medium.setStartLocation(new Location(lobby, -11, 11, -2));
		medium.setFinishLocation(new Location(lobby, -16, 23, -16));
		medium.setCheckpointLocation(new Location(lobby, -6.5, 10, 2.5, 135, 0));
		medium.setSignLocation(new Location(lobby, -9, 10, 1));

		LobbyParkour hard = new LobbyParkour();
		hard.setName(ChatColor.DARK_BLUE + "Сложный");
		hard.setStartLocation(new Location(lobby, 24, 22, -19));
		hard.setFinishLocation(new Location(lobby, 16, 26, -20));
		hard.setCheckpointLocation(new Location(lobby, 24.5, 21, -15.5, 180, 0));
		hard.setSignLocation(new Location(lobby, 24, 21, -18));

		LobbyParkour insane = new LobbyParkour();
		insane.setName(ChatColor.DARK_RED + "Дикий");
		insane.setStartLocation(new Location(lobby, -10, 22, -19));
		insane.setFinishLocation(new Location(lobby, -2, 26, -20));
		insane.setCheckpointLocation(new Location(lobby, -9.5, 21, -15.5, 180, 0));
		insane.setSignLocation(new Location(lobby, -10, 21, -18));

		LobbyParkour ice = new LobbyParkour();
		ice.setName(ChatColor.AQUA + "Ледовый");
		ice.setStartLocation(new Location(lobby, -60, 11, 71));
		ice.setFinishLocation(new Location(lobby, -50, 26, 114));
		ice.setCheckpointLocation(new Location(lobby, -56.5, 10, 71.5, 90, 0));
		ice.setSignLocation(new Location(lobby, -58, 10, 70));

		LobbyParkour ladder = new LobbyParkour();
		ladder.setName(ChatColor.LIGHT_PURPLE + "Странный");
		ladder.setStartLocation(new Location(lobby, -36, 11, 109));
		ladder.setFinishLocation(new Location(lobby, -50, 26, 114));
		ladder.setCheckpointLocation(new Location(lobby, -35.5, 10, 105.5, 0, 0));
		ladder.setSignLocation(new Location(lobby, -35, 10, 106));

		parkours.addAll(Lists.newArrayList(easy, medium, hard, insane, ice, ladder));
		Bukkit.getPluginManager().registerEvents(new LobbyParkourHandler(), Plugin.INSTANCE);
	}

	private static boolean isPassing(Player p, LobbyParkour parkour) {
		return isPassing(p) && passingParkours.get(p) == parkour;
	}

	public static boolean isPassing(Player p) {
		return passingParkours.containsKey(p);
	}

	private static void startPassing(Player p, LobbyParkour parkour) {
		passingParkours.put(p, parkour);
		EntityUtils.sendActionBarInfo(p,
				ChatColor.DARK_GRAY + "[" + parkour.getFullName() + ChatColor.DARK_GRAY + "]" + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + " Начато прохождение");
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F);
	}

	private static void finishParkour(Player p, LobbyParkour parkour) {
		passingParkours.remove(p);
		EntityUtils
				.sendActionBarInfo(p, ChatColor.DARK_GRAY + "[" + parkour.getFullName() + ChatColor.DARK_GRAY + "]" + ChatColor.BOLD + ChatColor.DARK_PURPLE + " Пройдено!");
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.5F);
		Sign sign = parkour.getSign();
		sign.setLine(1, ChatColor.DARK_GREEN + "Пройдено:");
		sign.setLine(2, ChatColor.DARK_PURPLE + p.getName());
		sign.update();
	}

	public static void stopPassing(Player p) {
		passingParkours.remove(p);
	}

	@EventHandler
	public void setCheckpoint(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(DualQuest.isInLobby(p) && p.getActivePotionEffects().isEmpty()) {
			for(LobbyParkour parkour : parkours) {
				if(!isPassing(p, parkour) && WorldUtils.compareIntegerLocations(parkour.getStartLocation(), p.getLocation())) {
					startPassing(p, parkour);
				}
				if(isPassing(p, parkour) && WorldUtils.compareIntegerLocations(parkour.getFinishLocation(), p.getLocation())) {
					finishParkour(p, parkour);
				}
			}
			if(isPassing(p) && e.getTo().getY() <= 0) {
				EntityUtils.teleport(p, passingParkours.get(p).getCheckpointLocation(), false, true);
			}
		}
	}

	@EventHandler
	public void cleanup(PlayerQuitEvent e) {
		if(DualQuest.isInLobby(e.getPlayer())) passingParkours.remove(e.getPlayer());
	}

}
