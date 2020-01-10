package dualquest.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldUtils {

	/**
	 * Compares two locations by their integer coordinates. This method does not check worlds!
	 * @param l1 First location to compare
	 * @param l2 Second location to compare
	 * @return Whether two locations are equal to each other by integer (block) coordinates
	 */
	public static boolean compareIntegerLocations(Location l1, Location l2) {
		return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
	}

	/**
	 * Converts string to a location. Format: "x y z" or "world x y z"
	 * @param str Input string
	 * @return Converted location
	 */
	public static Location getLocationFromString(String str) {
		Location loc;
		try {
			String[] pos = str.split(" ");
			if(pos.length == 3) {
				loc = new Location(null, Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]));
			} else if(pos.length == 4) {
				loc = new Location(Bukkit.getWorld(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]), Double.parseDouble(pos[3]));
			} else throw new IllegalArgumentException("Invalid string: cannot convert to location");
		} catch(Exception e) {
			throw new IllegalArgumentException("Invalid string: cannot convert to location");
		}
		return loc;
	}

}
