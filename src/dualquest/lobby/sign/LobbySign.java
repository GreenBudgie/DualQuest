package dualquest.lobby.sign;

import dualquest.game.logic.WorldManager;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public abstract class LobbySign {

	private Location location;

	public LobbySign(int x, int y, int z) {
		this.location = new Location(WorldManager.getLobby(), x, y, z);
	}

	public Sign getSign() {
		return (Sign) location.getBlock().getState();
	}

	public void clearText() {
		Sign sign = getSign();
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
		sign.update();
	}

	public Location getLocation() {
		return location;
	}

	public abstract void updateText();

	public abstract void onClick(Player player);

}
