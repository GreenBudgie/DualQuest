package dualquest.lobby;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class LobbyParkour {

	private Location startLocation, checkpointLocation, finishLocation, signLocation;
	private String name;

	public Location getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	public Location getCheckpointLocation() {
		return checkpointLocation;
	}

	public void setCheckpointLocation(Location checkpointLocation) {
		this.checkpointLocation = checkpointLocation;
	}

	public Location getFinishLocation() {
		return finishLocation;
	}

	public void setFinishLocation(Location finishLocation) {
		this.finishLocation = finishLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sign getSign() {
		return (Sign) signLocation.getBlock().getState();
	}

	public void setSignLocation(Location signLocation) {
		this.signLocation = signLocation;
		Sign sign = getSign();
		sign.setLine(0, getFullName());
		sign.update();
	}

	public String getFullName() {
		return this.name + ChatColor.DARK_AQUA + " Паркур";
	}

}
