package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.lobby.sign.LobbySignManager;
import dualquest.util.Broadcaster;
import dualquest.util.EntityUtils;
import dualquest.util.MathUtils;
import dualquest.util.TaskManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.io.File;

public class WorldManager {

	private static final int distanceBetweenTeams = 400;
	public static boolean generating;
	private static World lobby, gameWorld;
	private static Location attackersSpawn;
	private static Location spectatorsSpawn;
	private static Location questersSpawn;
	private static Location spawnLocation;
	public static boolean keepMap = true; //FIXME Remove on release

	public static void init() {
		lobby = Bukkit.getWorld("DualQuestLobby");
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.setPVP(false);
		lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false);

		if(Bukkit.getWorld("GameWorld") != null) {
			gameWorld = Bukkit.getWorld("GameWorld");
			gameWorld.setDifficulty(Difficulty.HARD);
			setupLocations();
		}
	}

	public static void makeWorld() {
		if(!hasWorld()) {
			generating = true;
			BossBar bar = Bukkit.createBossBar(ChatColor.GREEN + "<��������� ����>", BarColor.YELLOW, BarStyle.SOLID);
			bar.setProgress(1);
			PlayerHandler.getLobbyPlayers().forEach(bar::addPlayer);
			bar.setVisible(true);
			Bukkit.getOnlinePlayers()
					.forEach(player -> EntityUtils.sendActionBarInfo(player, ChatColor.GOLD + "" + ChatColor.BOLD + "������������ ���. ������ �������� �� ��� �����."));
			Broadcaster.everybody().sound(Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 0.5F);

			gameWorld = Bukkit.createWorld(new WorldCreator("GameWorld"));
			gameWorld.setDifficulty(Difficulty.HARD);
			gameWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
			gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
			gameWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
			gameWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
			gameWorld.setPVP(false);
			gameWorld.setTime(0);

			setupLocations();

			WorldBorder border = gameWorld.getWorldBorder();
			border.setSize(getRelativeWorldSize());
			border.setWarningTime(0);
			border.setWarningDistance(0);
			border.setDamageBuffer(0);
			border.setDamageAmount(0);
			border.setCenter(spawnLocation);

			bar.setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "��� ������!");
			bar.setColor(BarColor.GREEN);
			TaskManager.asyncInvokeLater(() -> {
				bar.setVisible(false);
				generating = false;
			}, 40);
			Broadcaster.everybody().sound(Sound.BLOCK_BEACON_ACTIVATE, 0.5F, 2F);
			LobbySignManager.updateSigns();
		}
	}

	private static void setupLocations() {
		double angle = MathUtils.randomRangeDouble(0, Math.PI * 2);
		int x = (int) (distanceBetweenTeams / 2 * Math.cos(angle));
		int z = (int) (distanceBetweenTeams / 2 * Math.sin(angle));
		int ax = (int) (distanceBetweenTeams / 2 * Math.cos(angle + Math.PI));
		int az = (int) (distanceBetweenTeams / 2 * Math.sin(angle + Math.PI));

		spawnLocation = gameWorld.getSpawnLocation().clone();
		spectatorsSpawn = spawnLocation.clone().add(0, 10, 0);
		questersSpawn = spawnLocation.clone().add(x, 0, z);
		questersSpawn.setY(gameWorld.getHighestBlockYAt(questersSpawn));
		attackersSpawn = spawnLocation.clone().add(ax, 0, az);
		attackersSpawn.setY(gameWorld.getHighestBlockYAt(attackersSpawn));

	}

	public static void deleteWorld() {
		if(hasWorld()) {
			deleteWorld(gameWorld);
			gameWorld = null;
		}
	}

	private static boolean deleteWorld(World world) {
		Bukkit.unloadWorld(world, false);
		return deleteWorld(world.getWorldFolder());
	}

	private static boolean deleteWorld(File path) {
		if(path.exists()) {
			File[] files = path.listFiles();
			if(files == null) return false;
			for(File file : files) {
				if(file.isDirectory()) {
					deleteWorld(file);
				} else {
					file.delete();
				}
			}
		}
		return path.delete();
	}

	public static int getRelativeWorldSize() {
		return distanceBetweenTeams + Bukkit.getOnlinePlayers().size() * 32;
	}

	public static int getRealWorldSize() {
		return (int) gameWorld.getWorldBorder().getSize();
	}

	public static Location getRandomSurfaceLocation() {
		int x = spawnLocation.getBlockX() + MathUtils.randomRange(-getRealWorldSize() / 2, getRealWorldSize() / 2);
		int z = spawnLocation.getBlockZ() + MathUtils.randomRange(-getRealWorldSize() / 2, getRealWorldSize() / 2);
		return new Location(gameWorld, x, gameWorld.getHighestBlockYAt(x, z), z);
	}

	private static boolean outside(int z) {
		return Math.abs(z - spawnLocation.getBlockZ()) > getRealWorldSize() / 2;
	}

	public static Location getSpawnLocation() {
		return spawnLocation;
	}

	public static Location getAttackersSpawn() {
		return attackersSpawn;
	}

	public static Location getSpectatorsSpawn() {
		return spectatorsSpawn;
	}

	public static Location getQuestersSpawn() {
		return questersSpawn;
	}

	public static World getLobby() {
		return lobby;
	}

	public static World getGameWorld() {
		return gameWorld;
	}

	public static Location getSpawn(PlayerTeam team) {
		return team == PlayerTeam.QUESTERS ? questersSpawn : attackersSpawn;
	}

	public static boolean hasWorld() {
		return gameWorld != null;
	}

}
