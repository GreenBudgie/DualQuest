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
		return selector().selectPlayers();
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
		private boolean includeInvalid = false;

		private Selector(List<DQPlayer> input) {
			selected = Lists.newArrayList(input);
		}

		public Selector includeInvalid() {
			includeInvalid = true;
			return this;
		}

		public int count() {
			return selected.size();
		}

		public Selector spectatingQuesters() {
			selected.removeIf(player -> player.getTeam() != PlayerTeam.QUESTERS || !player.isTemporaryDead());
			return this;
		}

		public Selector aliveQuesters() {
			selected.removeIf(player -> player.getTeam() != PlayerTeam.QUESTERS || player.isTemporaryDead());
			return this;
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

		public Selector questers() {
			selected.removeIf(player -> player.getTeam() != PlayerTeam.QUESTERS);
			return this;
		}

		public Selector attackers() {
			selected.removeIf(player -> player.getTeam() != PlayerTeam.ATTACKERS);
			return this;
		}

		public List<DQPlayer> select() {
			if(!includeInvalid) {
				selected.removeIf(player -> !player.isValid());
			}
			return selected;
		}

		public List<Player> selectPlayers() {
			if(!includeInvalid) {
				selected.removeIf(player -> !player.isValid());
			}
			return selected.stream().map(DQPlayer::getPlayer).collect(Collectors.toList());
		}

	}

}
