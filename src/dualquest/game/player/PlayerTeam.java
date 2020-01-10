package dualquest.game.player;

import org.bukkit.entity.Player;

public enum PlayerTeam {

	QUESTERS, ATTACKERS;

	public static PlayerTeam getTeam(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		return dqPlayer == null ? null : dqPlayer.getTeam();
	}

}
