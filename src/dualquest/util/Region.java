package dualquest.util;

import com.google.common.collect.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a region between two {@link Location locations}. Every method in this class is represented by integer (block) coordinates
 */
public class Region implements ConfigurationSerializable {

	private Location start, end;
	private int x1, y1, z1, x2, y2, z2;

	/**
	 * Creates a new region using start and end locations. Worlds in these locations can be null
	 * @param start Start location
	 * @param end End Location
	 */
	public Region(Location start, Location end) {
		if(start == null || end == null) throw new IllegalArgumentException("Cannot create a region: start and end locations cannot be null");
		this.start = start;
		this.end = end;
		setCoords();
	}

	/**
	 * Creates new region from the given bounding box. If points at the BoundingBox are represented by double values the region will not be 100% accurate
	 * @param box Bounding box to use
	 */
	public Region(BoundingBox box) {
		this.start = new Location(null, box.getMinX(), box.getMinY(), box.getMinZ());
		this.end = new Location(null, box.getMaxX(), box.getMaxY(), box.getMaxZ());
		setCoords();
	}

	/**
	 * Deserealizes a region from the given config. If config is invalid it returns null
	 * @param args Config
	 * @return Deserealized region, or null
	 */
	@Nullable
	public static Region deserialize(Map<String, Object> args) {
		Location start, end;

		try {
			if(args.containsKey("start")) {
				String str = String.valueOf(args.get("start"));
				String[] pos = str.split(" ");
				if(pos.length == 3) {
					start = new Location(null, Integer.valueOf(pos[0]), Integer.valueOf(pos[1]), Integer.valueOf(pos[2]));
				} else return null;
			} else return null;
			if(args.containsKey("end")) {
				String str = String.valueOf(args.get("end"));
				String[] pos = str.split(" ");
				if(pos.length == 3) {
					end = new Location(null, Integer.valueOf(pos[0]), Integer.valueOf(pos[1]), Integer.valueOf(pos[2]));
				} else return null;
			} else return null;
			Region region = new Region(start, end);
			if(args.containsKey("world")) {
				region.setWorld(Bukkit.getWorld(String.valueOf(args.get("world"))));
			}
			return region;
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Gets the world of the region
	 * @return World of the region, or null
	 */
	@Nullable
	public World getWorld() {
		if(hasWorld()) {
			return start.getWorld();
		} else return null;
	}

	/**
	 * Sets the world for both locations
	 * @param world New world
	 */
	public void setWorld(World world) {
		start.setWorld(world);
		end.setWorld(world);
	}

	/**
	 * Gets the random location inside of a region
	 * @return Random location inside of a region
	 */
	public Location getRandomInsideLocation() {
		return new Location(getWorld(), MathUtils.randomRangeDouble(x1, x2 + 1), MathUtils.randomRangeDouble(y1, y2 + 1),
				MathUtils.randomRangeDouble(z1, z2 + 1));
	}

	/**
	 * Gets the random location of a block (integer) inside of a region
	 * @return Random block location inside of a region
	 */
	public Location getRandomInsideBlockLocation() {
		return new Location(getWorld(), MathUtils.randomRange(x1, x2), MathUtils.randomRange(y1, y2), MathUtils.randomRange(z1, z2));
	}

	/**
	 * Gets the start (min) X location
	 * @return Start X location
	 */
	public int getX1() {
		return x1;
	}

	/**
	 * Sets the start (min) X location
	 * @param x1 Start X location
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}

	/**
	 * Gets the start (min) Y location
	 * @return Start Y location
	 */
	public int getY1() {
		return y1;
	}

	/**
	 * Sets the start (min) Y location
	 * @param y1 Start Y location
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}

	/**
	 * Gets the start (min) Z location
	 * @return Start Z location
	 */
	public int getZ1() {
		return z1;
	}

	/**
	 * Sets the start (min) Z location
	 * @param z1 Start Z location
	 */
	public void setZ1(int z1) {
		this.z1 = z1;
	}

	/**
	 * Gets the end (max) X location
	 * @return End X location
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * Sets the end (max) X location
	 * @param x2 End X location
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}

	/**
	 * Gets the end (max) Y location
	 * @return End Y location
	 */
	public int getY2() {
		return y2;
	}

	/**
	 * Sets the end (max) Y location
	 * @param y2 End Y location
	 */
	public void setY2(int y2) {
		this.y2 = y2;
	}

	/**
	 * Gets the end (max) Z location
	 * @return End Z location
	 */
	public int getZ2() {
		return z2;
	}

	/**
	 * Sets the end (max) Z location
	 * @param z2 End Z location
	 */
	public void setZ2(int z2) {
		this.z2 = z2;
	}

	/**
	 * Gets the start location
	 * @return Start location
	 */
	public Location getStartLocation() {
		return start;
	}

	/**
	 * Sets the start location
	 * @param start New start location
	 */
	public void setStartLocation(Location start) {
		this.start = start;
		setCoords();
	}

	/**
	 * Gets the end location
	 * @return End location
	 */
	public Location getEndLocation() {
		return end;
	}

	/**
	 * Sets the end location
	 * @param end New end location
	 */
	public void setEndLocation(Location end) {
		this.end = end;
		setCoords();
	}

	/**
	 * Checks if this region has a stored world
	 * @return Whether this region has stored world
	 */
	public boolean hasWorld() {
		return start.getWorld() != null && end.getWorld() != null && (start.getWorld() == end.getWorld());
	}

	/**
	 * Validates the worlds in this locations. If the worlds are different or the are null it throws an exception
	 */
	public void validateWorlds() {
		if(start.getWorld() == null || end.getWorld() == null || (start.getWorld() != end.getWorld()))
			throw new IllegalStateException("Worlds must be present in locations to use this method!");
	}

	public BoundingBox toBoundingBox() {
		return BoundingBox.of(start, end.clone().add(1, 1, 1));
	}

	public boolean intersects(BoundingBox box) {
		return toBoundingBox().overlaps(box);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("start", String.valueOf(x1) + String.valueOf(y1) + String.valueOf(z1));
		result.put("end", String.valueOf(x1) + " " + String.valueOf(y1) + " " + String.valueOf(z1));
		if(hasWorld()) result.put("world", start.getWorld().getName());
		return result;
	}

	/**
	 * Writes the coordinates to variables to maintain region easier
	 */
	void setCoords() {
		x1 = Math.min(start.getBlockX(), end.getBlockX());
		y1 = Math.min(start.getBlockY(), end.getBlockY());
		z1 = Math.min(start.getBlockZ(), end.getBlockZ());
		x2 = Math.max(start.getBlockX(), end.getBlockX());
		y2 = Math.max(start.getBlockY(), end.getBlockY());
		z2 = Math.max(start.getBlockZ(), end.getBlockZ());
		start.setX(x1);
		start.setY(y1);
		start.setZ(z1);
		end.setX(x2);
		end.setY(y2);
		end.setZ(z2);
	}

	/**
	 * Gets the faces of the region
	 * @return A multimap containing linked locations representing the start and the end locations of the face rectangle
	 */
	public ListMultimap<Location, Location> getFaces() {
		ListMultimap<Location, Location> map = ArrayListMultimap.create();
		Location l = start.clone();

		map.put(l, l.clone().add(getXSideLength(), getHeight(), 0));
		map.put(l, l.clone().add(0, getHeight(), getZSideLength()));
		map.put(l, l.clone().add(getXSideLength(), 0, getZSideLength()));
		Location end = l.clone().add(getXSideLength(), getHeight(), getZSideLength());
		map.put(l.clone().add(getXSideLength(), 0, 0), end);
		map.put(l.clone().add(0, getHeight(), 0), end);
		map.put(l.clone().add(0, 0, getZSideLength()), end);

		return map;
	}

	/**
	 * Gets the edges of the region
	 * @return A multimap containing linked locations representing line segments of each edge
	 */
	public ListMultimap<Location, Location> getEdges() {
		ListMultimap<Location, Location> map = ArrayListMultimap.create();
		Location l = start.clone();

		map.put(l.clone(), l.clone().add(0, getHeight(), 0));
		map.put(l.clone(), l.clone().add(getXSideLength(), 0, 0));

		l.add(getXSideLength(), 0, 0);

		map.put(l.clone(), l.clone().add(0, getHeight(), 0));
		map.put(l.clone(), l.clone().add(0, 0, getZSideLength()));

		l.add(0, 0, getZSideLength());

		map.put(l.clone(), l.clone().add(0, getHeight(), 0));
		map.put(l.clone(), l.clone().add(-getXSideLength(), 0, 0));

		l.add(-getXSideLength(), 0, 0);

		map.put(l.clone(), l.clone().add(0, getHeight(), 0));
		map.put(l.clone(), l.clone().add(0, 0, -getZSideLength()));

		l.add(0, getHeight(), -getZSideLength());

		map.put(l.clone(), l.clone().add(getXSideLength(), 0, 0));
		l.add(getXSideLength(), 0, 0);

		map.put(l.clone(), l.clone().add(0, 0, getZSideLength()));
		l.add(0, 0, getZSideLength());

		map.put(l.clone(), l.clone().add(-getXSideLength(), 0, 0));
		l.add(-getXSideLength(), 0, 0);

		map.put(l.clone(), l.clone().add(0, 0, -getZSideLength()));

		return map;
	}

	/**
	 * Gets the length of the X-Axis side, in other words the count of blocks at this side
	 * @return Length of the X-Axis side
	 */
	public int getXSideLength() {
		return Math.abs(x2 - x1) + 1;
	}

	/**
	 * Gets the length of the Z-Axis side, in other words the count of blocks at this side
	 * @return Length of the Z-Axis side
	 */
	public int getZSideLength() {
		return Math.abs(z2 - z1) + 1;
	}

	/**
	 * Gets the height of the region, or length of the Y-Axis side, in other words the count of blocks at this side
	 * @return Height of the region
	 */
	public int getHeight() {
		return Math.abs(y2 - y1) + 1;
	}

	/**
	 * Checks if the region represents a parallelepiped
	 * @return Whether the region is a 3D shape (parallelepiped)
	 */
	public boolean is3D() {
		return x1 != x2 && y1 != y2 && z1 != z2;
	}

	/**
	 * Checks if the region is flat by one of the axes
	 * @return Whether the region is a 2D shape (rectangle)
	 */
	public boolean isFlat() {
		return (x1 == x2 && y1 != y2 && z1 != z2) || (x1 != x2 && y1 == y2 && z1 != z2) || (x1 != x2 && y1 != y2 && z1 == z2);
	}

	/**
	 * Checks if the region represents a line
	 * @return Whether the region is a 1D shape (line)
	 */
	public boolean isLine() {
		return (x1 == x2 && y1 == y2 && z1 != z2) || (x1 != x2 && y1 == y2 && z1 == z2) || (x1 == x2 && y1 != y2 && z1 == z2);
	}

	/**
	 * Checks if the region represents a single point (start location coordinates are equal to end location coordinates)
	 * @return Whether the region is a point
	 */
	public boolean isPoint() {
		return x1 == x2 && y1 == y2 && z1 == z2;
	}

	/**
	 * Gets the amount of blocks inside or on the edges of a region
	 * @return Amount of blocks
	 */
	public int getBlockCount() {
		return getXSideLength() * getHeight() * getZSideLength();
	}

	/**
	 * Gets the list of blocks that are inside or on the edges of a region. This method uses {@link #validateWorlds() world validation}
	 * @return List of blocks
	 */
	public Set<Block> getBlocksInside() {
		validateWorlds();
		Set<Block> blocks = new HashSet<>();
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				for(int z = z1; z <= z2; z++) {
					blocks.add(new Location(start.getWorld(), x, y, z).getBlock());
				}
			}
		}
		return blocks;
	}

	/**
	 * Gets the list of entities that are inside or on the edges of a region. This method uses {@link #validateWorlds() world validation}
	 * @return List of entities
	 */
	public Set<Entity> getEntitiesInside() {
		validateWorlds();
		Set<Entity> entities = new HashSet<>();
		for(Entity ent : start.getWorld().getEntities()) {
			if(isInside(ent.getLocation())) entities.add(ent);
		}
		return entities;
	}

	/**
	 * Checks if the given entity is inside the region. This method does not check worlds!
	 * @param ent The Entity
	 * @return Whether the given entity is inside of the region
	 */
	public boolean isInside(Entity ent) {
		return isInside(ent.getLocation());
	}

	/**
	 * Checks if the given location is inside the region. This method does not check worlds!
	 * @param l The location
	 * @return Whether the given location is inside of the region
	 */
	public boolean isInside(Location l) {
		double x = l.getBlockX();
		double y = l.getBlockY();
		double z = l.getBlockZ();
		boolean xInside = x >= x1 && x <= x2;
		boolean yInside = y >= y1 && y <= y2;
		boolean zInside = z >= z1 && z <= z2;
		return xInside && yInside && zInside;
	}

}