package dualquest.game.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Represents an in-game player. Stays if the player left the game and can rejoin
 */
public class DQPlayer {

	private boolean valid = false; //If false, the player will be removed from the player list

	private final String playerName;
	private Player player;
	private PlayerTeam team = null;
	private boolean onServer = true;

	//Fields for attackers only
	private boolean isAlive = true;
	private int secondsToRespawn = 0;

	//Fields for questers only


	/**
	 * Registers a new DQPlayer
	 * @param p The player who represents this DQPlayer
	 */
	public DQPlayer(Player p) {
		player = p;
		playerName = p.getName();
	}

	/**
	 * Finds the DQPlayer that is bound to the given player. Returns null if the player wasn't found. This method uses <b>player nickname comparison</b>, that means that it
	 * searches the DQPlayer by comparing {@link Player#getName()} with {@link DQPlayer#getPlayerName()}
	 * @param p The player
	 * @return DQPlayer that is bound to the given player, or null
	 */
	@Nullable
	public static DQPlayer fromPlayer(Player p) {
		if(PlayerHandler.getPlayerList().isEmpty()) return null;
		return PlayerHandler.getPlayerList().getDQPlayers().stream().filter(dqp -> dqp.isOnServer() && dqp.getPlayerName().equals(p.getName())).findFirst().orElse(null);
	}

	/**
	 * Gets the team that the player in
	 * @return Player's team
	 */
	public PlayerTeam getTeam() {
		return team;
	}

	/**
	 * Sets the team that the player in
	 * @param team Player's team
	 */
	public void setTeam(PlayerTeam team) {
		this.team = team;
	}

	/**
	 * Gets the unchangeable player name bound to this DQPlayer
	 * @return The player name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Gets the player bound to this DQPlayer. If player is not on the server it will return null
	 * @return The player bound to this DQPlayer, or null
	 */
	@Nullable
	public Player getPlayer() {
		if(isOnServer()) {
			return player;
		} else {
			return null;
		}
	}

	/**
	 * Handles player rejoin
	 * @param p The player who rejoined
	 */
	public void rejoin(Player p) {
		this.player = p;
		this.onServer = true;
		//TODO
	}

	/**
	 * Handles player in-game quit
	 */
	public void quit() {
		this.player = null;
		this.onServer = false;
		//TODO
	}

	/**
	 * Handles player's death. If it is an attacker, the player will be respawned, otherwise he will join spectators
	 */
	public void death() {
		if(team == PlayerTeam.QUESTERS) {
			valid = false;
		} else {

		}
		//TODO
	}

	/**
	 * Determines if the player is currently online
	 * @return if the player is on server
	 */
	public boolean isOnServer() {
		return onServer;
	}

	@Override
	public boolean equals(Object another) {
		return another instanceof DQPlayer && ((DQPlayer) another).playerName.equals(playerName);
	}

	@Override
	public int hashCode() {
		return playerName.hashCode();
	}
}