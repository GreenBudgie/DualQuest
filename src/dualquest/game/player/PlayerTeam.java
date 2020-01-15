package dualquest.game.player;

import dualquest.util.Cases;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public enum PlayerTeam {

	QUESTERS(new Cases(ChatColor.BLUE, "��������", "���������", "���������", "���������", "����������", "���������")),
	ATTACKERS(new Cases(ChatColor.DARK_RED, "���������", "���������", "���������", "���������", "����������", "���������"));

	private Cases nameCases;

	PlayerTeam(Cases nameCases) {
		this.nameCases = nameCases;
	}

	@Nullable
	public static PlayerTeam getTeam(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		return dqPlayer == null ? null : dqPlayer.getTeam();
	}

	public Cases getCases() {
		return nameCases;
	}

	public String getName() {
		return nameCases.getNominative();
	}

	public boolean contains(Player player) {
		return getTeam(player) == this;
	}

	public void both(Consumer<PlayerTeam> action) {
		action.accept(QUESTERS);
		action.accept(ATTACKERS);
	}

}
