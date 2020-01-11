package dualquest.util;

import com.google.common.collect.ListMultimap;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;

public class ParticleUtils {

	public static EffectManager effectManager;

	/**
	 * Creates particles on the edges of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param density How much particles to spawn per block
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesOnRegionEdges(Region region, Particle particle, double density, @Nullable Color color) {
		region.validateWorlds();
		ListMultimap<Location, Location> edges = region.getEdges();
		for(Location start : edges.keySet()) {
			List<Location> ends = edges.get(start);
			for(Location end : ends) {
				createLine(start, end, particle, density, color);
			}
		}
	}

	/**
	 * Creates particles on the faces of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param density How much particles to spawn at each side
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesOnRegionFaces(Region region, Particle particle, int density, @Nullable Color color) {
		region.validateWorlds();
		ListMultimap<Location, Location> faces = region.getFaces();
		for(Location start : faces.keySet()) {
			List<Location> ends = faces.get(start);
			for(Location end : ends) {
				createParticlesBetween(start, end, particle, density, color);
			}
		}
	}

	/**
	 * Creates particles inside of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param amount How much particles to spawn
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesInsideRegion(Region region, Particle particle, int amount, @Nullable Color color) {
		region.validateWorlds();
		for(int i = 0; i < amount; i++) {
			createParticle(region.getRandomInsideLocation(), particle, color);
		}
	}

	/**
	 * Creates a line of particles
	 * @param from Line start location
	 * @param to Line end location
	 * @param particle A particle to create
	 * @param color Color of a particle, can be null
	 * @param density How much particles to spawn per block
	 */
	public static void createLine(Location from, Location to, Particle particle, double density, @Nullable Color color) {
		if(from.getWorld() != to.getWorld()) throw new IllegalArgumentException("Locations must have the same worlds!");
		LineEffect effect = new LineEffect(effectManager);
		effect.setLocation(from);
		effect.setTargetLocation(to);
		effect.particles = (int) Math.round(from.distance(to) * density);
		effect.particle = particle;
		effect.iterations = 1;
		if(color != null) effect.color = color;
		effect.start();
	}

	/**
	 * Creates a particles between two locations
	 * @param from Surface start location (first corner)
	 * @param to Surface end location (second corner)
	 * @param particle A particle to create
	 * @param color Color of a particle, can be null
	 * @param density How much particles to spawn per block
	 */
	public static void createParticlesBetween(Location from, Location to, Particle particle, double density, @Nullable Color color) {
		if(from.getWorld() != to.getWorld()) throw new IllegalArgumentException("Locations must have the same worlds!");
		double x1 = Math.min(from.getX(), to.getX());
		double y1 = Math.min(from.getY(), to.getY());
		double z1 = Math.min(from.getZ(), to.getZ());
		double x2 = Math.max(from.getX(), to.getX());
		double y2 = Math.max(from.getY(), to.getY());
		double z2 = Math.max(from.getZ(), to.getZ());
		int sizeX = (int) Math.ceil(x2 - x1) + 1;
		int sizeY = (int) Math.ceil(y2 - y1) + 1;
		int sizeZ = (int) Math.ceil(z2 - z1) + 1;
		int count = (int) (sizeX * sizeY * sizeZ * density);
		for(int i = 0; i < count; i++) {
			Location loc = new Location(from.getWorld(), MathUtils.randomRangeDouble(x1, x2), MathUtils.randomRangeDouble(y1, y2), MathUtils.randomRangeDouble(z1, z2));
			createParticle(loc, particle, color);
		}
	}

	/**
	 * Creates a single particle at the given location
	 * @param location The location
	 * @param particle A particle to create
	 * @param color Color of a particle, can be null
	 */
	public static void createParticle(Location location, Particle particle, @Nullable Color color) {
		ParticleEffectPoint effect = new ParticleEffectPoint();
		effect.setLocation(location);
		effect.particle = particle;
		effect.iterations = 1;
		if(color != null) effect.color = color;
		effect.start();
	}

	public static void createParticlesAround(Entity ent, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			double h = ent.getHeight();
			double w = ent.getWidth() / 1.5;
			createParticle(ent.getLocation().clone().add(MathUtils.randomRangeDouble(-w, w), MathUtils.randomRangeDouble(0, h), MathUtils.randomRangeDouble(-w, w)), effect, color);
		}
	}

	public static void createParticlesInsideSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			double u = Math.random();
			double v = Math.random();
			double theta = u * 2.0 * Math.PI;
			double phi = Math.acos(2.0 * v - 1.0);
			double sinTheta = Math.sin(theta);
			double cosTheta = Math.cos(theta);
			double sinPhi = Math.sin(phi);
			double cosPhi = Math.cos(phi);
			double r = Math.random() * radius;
			double x = r * sinPhi * cosTheta;
			double y = r * sinPhi * sinTheta;
			double z = r * cosPhi;
			createParticle(l.clone().add(x, y, z), effect, color);
		}
	}

	public static void createParticlesOutlineSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			double u = Math.random();
			double v = Math.random();
			double theta = 2 * Math.PI * u;
			double phi = Math.acos(2 * v - 1);
			double x = radius * Math.sin(phi) * Math.cos(theta);
			double y = radius * Math.sin(phi) * Math.sin(theta);
			double z = radius * Math.cos(phi);
			createParticle(l.clone().add(x, y, z), effect, color);
		}
	}

	public static void createParticlesInRange(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			ef.setLocation(
					l.clone().add(MathUtils.randomRangeDouble(-radius, radius), MathUtils.randomRangeDouble(-radius, radius), MathUtils.randomRangeDouble(-radius, radius)));
			ef.start();
		}
	}

	public static void createParticlesInside(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			createParticle(b.getLocation().clone().add(MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1)), effect, color);
		}
	}

	public static void createParticlesOutline(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			createParticle(getOutlineLocation(b.getLocation().clone().add(0.5, 0.5, 0.5), 0.6), effect, color);
		}
	}

	private static Location getOutlineLocation(Location l, double r) {
		double x = 0, y = 0, z = 0;
		int rand = MathUtils.randomRange(1, 3);
		switch(rand) {
		case 1:
			x = MathUtils.choose(r, -r);
			break;
		case 2:
			y = MathUtils.choose(r, -r);
			break;
		case 3:
			z = MathUtils.choose(r, -r);
			break;
		}
		if(x != 0) {
			y = MathUtils.randomRangeDouble(-r, r);
			z = MathUtils.randomRangeDouble(-r, r);
		}
		if(y != 0) {
			x = MathUtils.randomRangeDouble(-r, r);
			z = MathUtils.randomRangeDouble(-r, r);
		}
		if(z != 0) {
			x = MathUtils.randomRangeDouble(-r, r);
			y = MathUtils.randomRangeDouble(-r, r);
		}
		return l.clone().add(x, y, z);
	}

	private static class ParticleEffectPoint extends Effect {

		public Particle particle = Particle.REDSTONE;
		public int amount = 1;

		public ParticleEffectPoint() {
			super(ParticleUtils.effectManager);
			type = EffectType.INSTANT;
			visibleRange = 128F;
		}

		@Override
		public void onRun() {
			this.display(particle, this.getLocation(), color, 0, amount);
		}

	}

}
