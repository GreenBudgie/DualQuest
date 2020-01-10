package dualquest.util;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A class that helps to work with announcements for players Each method can send an information in different ways to specified player(s) as a single object, array or
 * collection
 */
public class Broadcaster {

	private Set<? extends Player> players;

	public static Broadcaster everybody() {
		return new Broadcaster(Bukkit.getOnlinePlayers());
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

	private Broadcaster(Player... players) {
		this.players = Sets.newHashSet(players);
	}

	private Broadcaster(Collection<? extends Player> players) {
		this.players = Sets.newHashSet(players);
	}

	public Broadcaster toChat(String... messages) {
		players.forEach(player -> player.sendMessage(messages));
		return this;
	}

	public Broadcaster toActionBar(String message) {
		players.forEach(player -> EntityUtils.sendActionBarInfo(player, message));
		return this;
	}

	public Broadcaster asTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		players.forEach(player -> player.sendTitle(title, subtitle, fadeIn, stay, fadeOut));
		return this;
	}

}
