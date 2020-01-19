package dualquest.game.quest;

import dualquest.game.player.DQPlayer;
import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.util.NumericalCases;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class QuestFight extends Quest implements Listener {

	private final NumericalCases cases = new NumericalCases("игрока", "игрока", "игроков");
	private int killCount;

	@Override
	public String getName() {
		return "Битва";
	}

	@Override
	public List<String> getDescriptionLines() {
		List<String> desc = new ArrayList<>();
		desc.add(ChatColor.GOLD + "Убить " + ChatColor.AQUA + killCount + ChatColor.GOLD + " " + cases.byNumber(killCount) + " команды " + PlayerTeam.ATTACKERS.getCases()
				.getGenitive());
		return desc;
	}

	@Override
	public int getDuration() {
		return 10;
	}

	@Override
	public void onActivate() {
		killCount = (int) Math.ceil(PlayerHandler.getPlayerList().selector().attackers().count() / 1.5);
	}

	@EventHandler
	public void kill(PlayerDeathEvent e) {
		Player player = e.getEntity();
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		if(dqPlayer != null && dqPlayer.getTeam() == PlayerTeam.ATTACKERS) {
			killCount--;
			if(killCount <= 0) {
				complete();
			}
		}
	}

}
