package dualquest.game.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public enum PlayerTeam {

	QUESTERS, ATTACKERS;

	public boolean contains(Player player) {
		return getTeam(player) == this;
	}

	public void both(Consumer<PlayerTeam> action) {
		action.accept(QUESTERS);
		action.accept(ATTACKERS);
	}

	@Nullable
	public static PlayerTeam getTeam(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		return dqPlayer == null ? null : dqPlayer.getTeam();
	}

}
