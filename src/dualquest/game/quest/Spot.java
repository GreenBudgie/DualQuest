package dualquest.game.quest;

import de.slikey.effectlib.effect.WarpEffect;
import dualquest.game.Plugin;
import dualquest.game.logic.DualQuest;
import dualquest.game.player.PlayerHandler;
import dualquest.util.EntityUtils;
import dualquest.util.ParticleUtils;
import dualquest.util.TaskManager;
import dualquest.util.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a location on the map with some properties
 */
public class Spot implements Listener {

	private Location location;
	private SpottedQuest owner;
	private int size = 3;
	private double iter = size;
	private boolean checkPlayers = true, catchPlayer = false, preventInteractions = false, active = true;
	private Player catched = null;
	private ArmorStand label = null;

	public Spot(SpottedQuest owner, Location location) {
		this.location = location;
		this.owner = owner;
		Bukkit.getPluginManager().registerEvents(this, Plugin.INSTANCE);
	}

	public void setLabel(String label) {
		this.label = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, (int) (size / 2.0), 0), EntityType.ARMOR_STAND);
		this.label.setCustomNameVisible(true);
		this.label.setCustomName(label);
		this.label.setMarker(true);
		this.label.setInvulnerable(true);
		this.label.setGravity(false);
	}

	public boolean hasLabel() {
		return label != null;
	}

	public void removeLabel() {
		if(hasLabel()) {
			label.remove();
			label = null;
		}
	}

	public SpottedQuest getQuest() {
		return owner;
	}

	public Location getLocation() {
		return location;
	}

	public boolean doCheckPlayers() {
		return checkPlayers;
	}

	public void setCheckPlayers(boolean checkPlayers) {
		this.checkPlayers = checkPlayers;
	}

	public boolean doCatchPlayer() {
		return catchPlayer;
	}

	public void setCatchPlayer(boolean catchPlayer) {
		this.catchPlayer = catchPlayer;
		setCheckPlayers(true);
	}

	public boolean doPreventInteractions() {
		return preventInteractions;
	}

	public void setPreventInteractions(boolean preventInteractions) {
		this.preventInteractions = preventInteractions;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		this.iter = size;
	}

	public boolean isInside(Location l) {
		return Math.abs(l.getY() - location.getY()) <= size && WorldUtils.distanceFlat(location, l) <= size;
	}

	public List<Player> getPlayersInside() {
		return location.getWorld().getPlayers().stream().filter(p -> isInside(p.getLocation())).collect(Collectors.toList());
	}

	public boolean hasPlayersInside() {
		return !getPlayersInside().isEmpty();
	}

	public Player getCenteredPlayer() {
		if(!hasPlayersInside()) return null;
		return WorldUtils.getNearestEntity(location, Player.class);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void unregister() {
		HandlerList.unregisterAll(this);
	}

	public void update() {
		if(active) {
			if(TaskManager.ticksPassed(10)) {
				WarpEffect ef = new WarpEffect(ParticleUtils.effectManager);
				ef.setLocation(location.clone().add(0, iter + 0.5, 0));
				ef.rings = 1;
				ef.iterations = 1;
				ef.particles = size * (size + 2);
				ef.radius = size;
				ef.particle = hasPlayersInside() && checkPlayers ? Particle.FLAME : Particle.CLOUD;
				ef.start();
				if(iter < 0) iter = size;
				else iter -= 0.5;
			}
			if(catched != null && !isInside(catched.getLocation())) {
				returnPlayer(catched);
			}
		}
	}

	@EventHandler
	public void noExplode(BlockExplodeEvent e) {
		if(preventInteractions && active) {
			e.blockList().removeIf(block -> block.getWorld() == location.getWorld() && block.getLocation().distance(location) <= size);
		}
	}

	@EventHandler
	public void noExplode(EntityExplodeEvent e) {
		if(preventInteractions && active) {
			e.blockList().removeIf(block -> block.getWorld() == location.getWorld() && block.getLocation().distance(location) <= size);
		}
	}

	@EventHandler
	public void noBlockBreak(BlockBreakEvent e) {
		if(preventInteractions && active) {
			Location l = e.getBlock().getLocation();
			if(l.getWorld() == location.getWorld() && l.distance(location) <= size) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noBlockPlace(BlockPlaceEvent e) {
		if(preventInteractions && active) {
			Location l = e.getBlock().getLocation();
			if(l.getWorld() == location.getWorld() && l.distance(location) <= size) {
				e.setCancelled(true);
			}
		}
	}

	public boolean hasCatchedPlayer() {
		return catched != null;
	}

	public Player getCatchedPlayer() {
		return catched;
	}

	private void returnPlayer(Player p) {
		EntityUtils.teleport(p, location, true, true);
		p.playSound(p.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0.5F, 0.5F);
	}

	@EventHandler
	public void checkMoves(PlayerMoveEvent e) {
		if(active && checkPlayers) {
			Player p = e.getPlayer();
			if(PlayerHandler.isPlaying(p) && p.getWorld() == location.getWorld()) {
				List<Player> inside = getPlayersInside();
				if(inside.contains(p)) {
					if(!isInside(e.getTo())) {
						if(!catchPlayer || (hasCatchedPlayer() && p != catched)) {
							owner.onSpotLeave(this, p);
						}
					}
				} else {
					if(isInside(e.getTo())) {
						if(catchPlayer && !hasCatchedPlayer()) {
							catched = p;
							owner.onCatch(this, p);
						}
						owner.onSpotEnter(this, p);
					}
				}
			}
		}
	}

}
