package dualquest.game.player;

import dualquest.util.Cases;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public enum PlayerTeam {

	QUESTERS(ChatColor.BLUE, new Cases(ChatColor.BLUE, "Квестеры", "Квестеров", "Квестерам", "Квестеров", "Квестерами", "Квестерах")),
	ATTACKERS(ChatColor.DARK_RED, new Cases(ChatColor.DARK_RED, "Атакующие", "Атакующих", "Атакующим", "Атакующих", "Атакующими", "Атакующих"));

	private ChatColor color;
	private Cases nameCases;

	PlayerTeam(ChatColor color, Cases nameCases) {
		this.color = color;
		this.nameCases = nameCases;
	}

	@Nullable
	public static PlayerTeam getTeam(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		return dqPlayer == null ? null : dqPlayer.getTeam();
	}

	public ChatColor getColor() {
		return color;
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
