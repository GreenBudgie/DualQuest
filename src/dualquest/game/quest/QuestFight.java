package dualquest.game.quest;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.util.NumericalCases;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class QuestFight extends Quest {

	private final NumericalCases cases = new NumericalCases("игрока", "игрока", "игроков");
	private int killCount;

	@Override
	public String getName() {
		return "Битва";
	}

	@Override
	public List<String> getDescriptionLines() {
		List<String> desc = new ArrayList<>();
		desc.add(ChatColor.GOLD + "Убить " + ChatColor.AQUA + killCount + ChatColor.GOLD + cases.byNumber(killCount) + " команды " + PlayerTeam.ATTACKERS.getCases()
				.getGenitive());
		return desc;
	}

	@Override
	public void onEnable() {
		killCount = (int) Math.ceil(PlayerHandler.getPlayerList().selector().valid().team(PlayerTeam.ATTACKERS).count() / 1.5);
	}

	@Override
	public void onComplete() {

	}

	@Override
	public boolean isCompleted() {
		return killCount <= 0;
	}

	@Override
	public void update() {

	}

	@Override
	public int getDuration() {
		return 0;
	}
}
