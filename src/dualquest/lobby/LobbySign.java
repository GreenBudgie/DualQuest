package dualquest.lobby;

import dualquest.game.WorldManager;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class LobbySign {

	private Location location;
	private SignType type;

	public LobbySign(int x, int y, int z, SignType type) {
		this.location = new Location(WorldManager.getLobby(), x, y, z);
		this.type = type;
	}

	public Sign getSign() {
		return (Sign) location.getBlock().getState();
	}

	public Location getLocation() {
		return location;
	}

	public SignType getType() {
		return type;
	}

}
