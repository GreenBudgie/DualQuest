package dualquest.game.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.istack.internal.NotNull;
import dualquest.game.logic.DualQuest;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerList implements Iterable<DQPlayer> {

	private List<DQPlayer> players;

	public PlayerList(Collection<DQPlayer> players) {
		this.players = Lists.newArrayList(players);
	}

	public List<DQPlayer> getDQPlayers() {
		return selector().select();
	}

	public List<Player> getPlayers() {
		return selector().online().selectPlayers();
	}

	public void remove(DQPlayer p) {
		players.remove(p);
	}

	public void remove(Player p) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(p);
		if(dqPlayer != null) {
			players.remove(dqPlayer);
		}
	}

	public static PlayerList empty() {
		return new PlayerList(new ArrayList<>());
	}

	public boolean isEmpty() {
		return players.isEmpty();
	}

	public Selector selector() {
		return new Selector(players);
	}

	public int size() {
		return players.size();
	}

	public Stream<DQPlayer> stream() {
		return players.stream();
	}

	@Override
	public Iterator<DQPlayer> iterator() {
		return players.iterator();
	}

	@Override
	public void forEach(Consumer<? super DQPlayer> action) {
		players.forEach(action);
	}

	@Override
	public Spliterator<DQPlayer> spliterator() {
		return players.spliterator();
	}

	public static class Selector {

		private List<DQPlayer> selected;

		private Selector(List<DQPlayer> input) {
			selected = Lists.newArrayList(input);
		}

		public Selector online() {
			selected.removeIf(player -> !player.isOnServer());
			return this;
		}

		public Selector offline() {
			selected.removeIf(DQPlayer::isOnServer);
			return this;
		}

		public Selector team(PlayerTeam team) {
			selected.removeIf(player -> player.getTeam() != team);
			return this;
		}

		public List<DQPlayer> select() {
			return selected;
		}

		public List<Player> selectPlayers() {
			return selected.stream().map(DQPlayer::getPlayer).collect(Collectors.toList());
		}

	}

}
