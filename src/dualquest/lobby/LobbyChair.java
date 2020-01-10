package dualquest.lobby;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LobbyChair {

	private Location location;
	private Arrow sittingOn;
	private Player sittingPlayer;

	public LobbyChair(Player p, Location l) {
		this.sittingPlayer = p;
		this.location = l;
		Arrow arrow = (Arrow) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARROW);
		arrow.setSilent(true);
		arrow.teleport(l);
		arrow.addPassenger(p);
		sittingOn = arrow;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Arrow getSittingOn() {
		return sittingOn;
	}

	public void setSittingOn(Arrow sittingOn) {
		this.sittingOn = sittingOn;
	}

	public Player getSittingPlayer() {
		return sittingPlayer;
	}

	public void setSittingPlayer(Player sittingPlayer) {
		this.sittingPlayer = sittingPlayer;
	}

}
