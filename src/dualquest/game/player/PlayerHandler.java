package dualquest.game.player;

import com.google.common.collect.Streams;
import dualquest.game.logic.DualQuest;
import dualquest.game.logic.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles everything that can happen to players in the game
 */
public class PlayerHandler implements Listener {

	private static PlayerList playerList;
	private static List<Player> spectators = new ArrayList<>();

	public static PlayerList getPlayerList() {
		return playerList;
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

	public static void joinSpectators(Player player) {
		spectators.add(player);
	}

}
