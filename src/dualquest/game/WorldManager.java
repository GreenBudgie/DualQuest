package dualquest.game;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;

public class WorldManager {

	private static World lobby, gameWorld;
	public static boolean generating;

	public static void init() {
		lobby = Bukkit.getWorld("DualQuestLobby");
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.setPVP(false);
		lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false);
	}

	public static World getLobby() {
		return lobby;
	}

}
