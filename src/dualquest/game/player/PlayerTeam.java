package dualquest.game.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public enum PlayerTeam {

	QUESTERS, ATTACKERS;

	public boolean contains(Player player) {
		return getTeam(player) == this;
	}

	@Nullable
	public static PlayerTeam getTeam(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		return dqPlayer == null ? null : dqPlayer.getTeam();
	}

}
