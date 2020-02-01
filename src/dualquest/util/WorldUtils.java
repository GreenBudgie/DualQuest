package dualquest.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

	public static <T extends Entity> List<T> getEntitiesInRange(Location pivot, double range, Class<T> entityClass) {
		if(!pivot.isWorldLoaded()) throw new IllegalArgumentException("Pivot location's world is not present!");
		List<T> list = new ArrayList<>();
		pivot.getWorld().getEntities().stream().filter(entity -> entityClass.isInstance(entity) && entity.getLocation().getWorld() == pivot.getWorld()
				&& entity.getLocation().distance(pivot) <= range).forEach(entity -> list.add((T) entity));
		return list;
	}

	public static <T extends Entity> T getNearestEntity(Location pivot, Class<T> entityClass) {
		if(!pivot.isWorldLoaded()) throw new IllegalArgumentException("Pivot location's world is not present!");
		Comparator<Entity> comparatorByDistance = Comparator.comparingDouble(entity -> entity.getLocation().distance(pivot));
		Entity found = pivot.getWorld().getEntities().stream()
				.filter(entity -> entityClass.isInstance(entity) && entity.getLocation().getWorld() == pivot.getWorld()).min(comparatorByDistance).orElse(null);
		return (T) found;
	}

	public static <T extends Entity> T getFurthestEntity(Location pivot, Class<T> entityClass) {
		if(!pivot.isWorldLoaded()) throw new IllegalArgumentException("Pivot location's world is not present!");
		Comparator<Entity> comparatorByDistance = Comparator.comparingDouble(entity -> entity.getLocation().distance(pivot));
		Entity found = pivot.getWorld().getEntities().stream()
				.filter(entity -> entityClass.isInstance(entity) && entity.getLocation().getWorld() == pivot.getWorld()).max(comparatorByDistance).orElse(null);
		return (T) found;
	}

	public static double distanceFlat(Location l, Location l2) {
		if(l == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		} else if(l.getWorld() == null || l2.getWorld() == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null world");
		} else if(l.getWorld() != l2.getWorld()) {
			throw new IllegalArgumentException("Cannot measure distance between " + l2.getWorld().getName() + " and " + l.getWorld().getName());
		}
		return Math.sqrt(NumberConversions.square(l.getX() - l2.getX()) + NumberConversions.square(l.getZ() - l2.getZ()));
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
