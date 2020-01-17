package dualquest.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A class that helps to work with announcements for players Each method can send any information in different ways to specified player(s) as a single object, array or
 * collection
 */
public class Broadcaster {

	private Set<Player> players;

	public static Broadcaster everybody() {
		return new Broadcaster(Lists.newArrayList(Bukkit.getOnlinePlayers()));
	}

	public static Broadcaster inWorld(World world) {
		return new Broadcaster(world.getPlayers());
	}

	public static Broadcaster inRange(Location pivot, double range) {
		return new Broadcaster(WorldUtils.getEntitiesInRange(pivot, range, Player.class));
	}

	public static Broadcaster each(Player... players) {
		return new Broadcaster(players);
	}

	public static Broadcaster each(Collection<Player> players) {
		return new Broadcaster(players);
	}

	public Broadcaster and(Player... players) {
		this.players.addAll(Arrays.asList(players));
		return this;
	}

	public Broadcaster and(Collection<Player> players) {
		this.players.addAll(players);
		return this;
	}

	public Broadcaster except(Player... players) {
		this.players.removeAll(Arrays.asList(players));
		return this;
	}

	public Broadcaster except(Collection<Player> players) {
		this.players.removeAll(players);
		return this;
	}

	private Broadcaster(Player... players) {
		this.players = Sets.newHashSet(players);
	}

	private Broadcaster(Collection<Player> players) {
		this.players = Sets.newHashSet(players);
	}

	public Broadcaster toChat(String... messages) {
		players.forEach(player -> player.sendMessage(messages));
		return this;
	}

	public Broadcaster toChat(Collection<String> messages) {
		for(String message : messages) {
			players.forEach(player -> player.sendMessage(message));
		}
		return this;
	}

	public Broadcaster toActionBar(String message) {
		players.forEach(player -> EntityUtils.sendActionBarInfo(player, message));
		return this;
	}

	public Broadcaster title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		players.forEach(player -> player.sendTitle(title, subtitle, fadeIn, stay, fadeOut));
		return this;
	}

	public Broadcaster sound(Sound sound, float volume, float pitch) {
		players.forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
		return this;
	}

	public Broadcaster sound(Location location, Sound sound, float volume, float pitch) {
		players.forEach(player -> player.playSound(location, sound, volume, pitch));
		return this;
	}

}
