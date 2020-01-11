package dualquest.game.logic;

import com.google.common.collect.Lists;
import dualquest.game.player.DQPlayer;
import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerList;
import dualquest.game.player.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class GameStartManager {

	private static Set<Location> playerSpawns = new HashSet<>();
	private static int votesRequired;

	public static void startGame() {
		if(GameState.isPlaying()) throw new IllegalStateException("Cannot start the game at the current moment! It's already running");
		if(!WorldManager.hasWorld()) throw new IllegalStateException("Cannot start without the generated world!");

		PlayerHandler.initPlayerList(Bukkit.getOnlinePlayers());
		dividePlayersByTeams();

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

	private static void dividePlayersByTeams() {
		PlayerList playerList = PlayerHandler.getPlayerList();
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



}
