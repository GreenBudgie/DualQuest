package dualquest.game.quest;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.util.Broadcaster;
import dualquest.util.TaskManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Quest {

	private int timeToEnd;

	public abstract String getName();

	public abstract List<String> getDescriptionLines();

	public abstract void onEnable();

	public abstract void onComplete();

	public abstract boolean isCompleted();

	public abstract int getDuration();

	public abstract void onTimeEnd();

	public void update() {
		if(TaskManager.isSecUpdated()) {
			timeToEnd--;
			if(timeToEnd <= 0) {
				onTimeEnd();
			}
		}
	}

	public void complete() {

	}

	public void activate() {
		Broadcaster.each(PlayerHandler.getPlayerList().selector().team(PlayerTeam.QUESTERS).selectPlayers()).and(PlayerHandler.getSpectators()).toChat(getAnnounceMessage())
				.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.8F).title(ChatColor.DARK_AQUA + "Новый квест", ChatColor.AQUA + getName(), 10, 40, 20);
		Broadcaster.each(PlayerHandler.getPlayerList().selector().team(PlayerTeam.ATTACKERS).selectPlayers()).
	}

	public void deactivate() {

	}

	public final int getTimeToEnd() {
		return timeToEnd;
	}

	public boolean isActive() {
		return QuestManager.getCurrentQuest() != null && QuestManager.getCurrentQuest() == this;
	}

	public List<String> getAnnounceMessage() {
		List<String> lines = new ArrayList<>();
		String name =
				ChatColor.DARK_GRAY + "----- " + ChatColor.YELLOW + ChatColor.BOLD + "Квест: " + ChatColor.RESET + ChatColor.AQUA + getName() + ChatColor.DARK_GRAY + " -----";
		lines.add(name);
		lines.add("");
		lines.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Задача:");
		lines.addAll(getDescriptionLines().stream().map(str -> ChatColor.DARK_GRAY + "- " + str).collect(Collectors.toList()));
		lines.add("");
		lines.add(ChatColor.DARK_GRAY + StringUtils.repeat("-", 20 + getName().length()));
		return lines;
	}

}
