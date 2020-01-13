package dualquest.game.logic;

import com.google.common.collect.Lists;
import dualquest.game.player.DQPlayer;
import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerList;
import dualquest.game.player.PlayerTeam;
import dualquest.util.ParticleUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles every event that happen in a pre-game, including starting the game itself
 */
public class GameStartManager implements Listener {

	private static Set<Location> playerSpawns = new HashSet<>();
	private static int votesQuesters, votesAttackers;
	private static boolean mapAllowed;
	private static BossBar voteBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);

	public static void startGame(Option... options) {
		if(GameState.isPlaying()) throw new IllegalStateException("Cannot start the game at the current moment! It's already running");
		if(!WorldManager.hasWorld()) throw new IllegalStateException("Cannot start without the generated world!");

		PlayerHandler.initPlayerList(Bukkit.getOnlinePlayers());
		dividePlayersByTeams(ArrayUtils.contains(options, Option.ONLY_QUESTERS), ArrayUtils.contains(options, Option.ONLY_ATTACKERS));

		PlayerList playerList = PlayerHandler.getPlayerList();
		List<Location> questersSpawns = calculateSpawnLocationsFor(playerList.selector().team(PlayerTeam.QUESTERS).selectPlayers(), WorldManager.getQuestersSpawn());
		List<Location> attackersSpawns = calculateSpawnLocationsFor(playerList.selector().team(PlayerTeam.ATTACKERS).selectPlayers(), WorldManager.getAttackersSpawn());
		playerSpawns.clear();
		playerSpawns.addAll(questersSpawns);
		playerSpawns.addAll(attackersSpawns);
		createGlassPlatforms();
		teleportPlayers(playerList.selector().team(PlayerTeam.QUESTERS).selectPlayers(), questersSpawns);
		teleportPlayers(playerList.selector().team(PlayerTeam.ATTACKERS).selectPlayers(), attackersSpawns);

		for(Player player : PlayerHandler.getInGamePlayers()) {
			ScoreboardHandler.createGameScoreboard(player);
		}

		votesQuesters = 0;
		votesAttackers = 0;
		mapAllowed = false;

		GameState.VOTING.set(20);
	}

	private static List<Location> calculateSpawnLocationsFor(List<Player> players, Location pivot) {
		double radius = players.size() / 1.5;
		double radsPerPlayer = 2 * Math.PI / players.size();
		int spawnHeight = pivot.getWorld().getHighestBlockYAt(pivot) + 16;
		List<Location> spawnLocations = new ArrayList<>();
		for(int i = 0; i < players.size(); i++) {
			int x = (int) Math.round(pivot.getX() + Math.cos(radsPerPlayer * i) * radius);
			int z = (int) Math.round(pivot.getZ() + Math.sin(radsPerPlayer * i) * radius);

			double xLength = pivot.getX() - x;
			double zLength = pivot.getZ() - z;
			double yaw = -Math.atan2(xLength, zLength);

			Location finalLocation = new Location(pivot.getWorld(), x, 1, z, (float) Math.toDegrees(yaw), 0);
			spawnLocations.add(finalLocation);
			int topY = pivot.getWorld().getHighestBlockYAt(finalLocation) + 4;
			if(topY >= spawnHeight) spawnHeight = topY;
		}
		for(Location l : spawnLocations) {
			l.setY(spawnHeight);
		}
		return spawnLocations;
	}

	private static void dividePlayersByTeams(boolean onlyQuesters, boolean onlyAttackers) {
		PlayerList playerList = PlayerHandler.getPlayerList();
		if(onlyQuesters) {
			playerList.forEach(player -> player.setTeam(PlayerTeam.QUESTERS));
		} else if(onlyAttackers) {
			playerList.forEach(player -> player.setTeam(PlayerTeam.ATTACKERS));
		} else {
			int questersCount = (int) Math.ceil(playerList.size() / 2.0);
			List<DQPlayer> shuffled = Lists.newArrayList(playerList.getDQPlayers());
			Collections.shuffle(shuffled);
			for(int i = 0; i < questersCount; i++) {
				shuffled.get(i).setTeam(PlayerTeam.QUESTERS);
			}
			for(int i = questersCount; i < playerList.size(); i++) {
				shuffled.get(i).setTeam(PlayerTeam.ATTACKERS);
			}
		}
	}

	private static void teleportPlayers(List<Player> players, List<Location> locations) {
		for(int i = 0; i < players.size(); i++) {
			players.get(i).teleport(locations.get(i).clone().add(0.5, 0.5, 0.5));
		}
	}

	private static void createGlassPlatforms() {
		for(Location spawn : playerSpawns) {
			Block glass = spawn.clone().subtract(0, 1, 0).getBlock();
			glass.setType(Material.GLASS);
		}
	}

	public static void removeGlassPlatforms() {
		for(Location spawn : playerSpawns) {
			spawn.clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
		}
	}

	private static void endVoting() {
		if(mapAllowed) {

		} else {
			removeGlassPlatforms();
			for(Player player : PlayerHandler.getPlayerList().getPlayers()) {
				player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "����� �����!", ChatColor.RED + "����������� ������������� ������", 5, 50, 10);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1F, 0.8F);
				player.setGameMode(GameMode.SPECTATOR);
			}
			GameState.ENDING.set();
		}
	}

	private static void vote(Player player, boolean vote) {
		ParticleUtils.createParticlesAround(player, Particle.REDSTONE, null, 20);
		Block block = player.getWorld().getHighestBlockAt(player.getLocation());
		if(block.getType() == Material.GLASS) {
			block.setType(vote ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS);
			if(vote) {
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.5F);
			} else {
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
			}
		}
		if(vote) {
			if(PlayerTeam.QUESTERS.contains(player)) {
				votesQuesters++;
			}
			if(PlayerTeam.ATTACKERS.contains(player)) {
				votesAttackers++;
			}
		}
		int questersCount = PlayerHandler.getPlayerList().selector().team(PlayerTeam.QUESTERS).count();
		int attackersCount = PlayerHandler.getPlayerList().selector().team(PlayerTeam.ATTACKERS).count();
		boolean questersPassed = votesQuesters >= questersCount / 2.0;
		boolean attackersPassed = votesAttackers >= attackersCount / 2.0;
		if(questersPassed && attackersPassed) {
			mapAllowed = true;
		}
	}

	@EventHandler
	public void preventMoving(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(GameState.isPreGame() && PlayerHandler.isPlaying(player) && e.getTo() != null) {
			if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void preventBreaking(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if(GameState.isPreGame() && PlayerHandler.isPlaying(player)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void preventPlacing(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if(GameState.isPreGame() && PlayerHandler.isPlaying(player)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void interaction(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(GameState.isPreGame() && PlayerHandler.isPlaying(player)) {
			e.setCancelled(true);
			//Voting
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				ItemStack item = e.getItem();
				if(item != null) {
					Material type = item.getType();
					if(type == Material.LIME_DYE) {
						vote(player, true);
					}
					if(type == Material.RED_DYE) {
						vote(player, false);
					}
				}
			}
		}
	}

	public enum Option {

		FAST,
		ONLY_ATTACKERS,
		ONLY_QUESTERS

	}

}
