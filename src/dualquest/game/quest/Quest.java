package dualquest.game.quest;

import dualquest.game.Plugin;
import dualquest.game.player.PlayerHandler;
import dualquest.util.Broadcaster;
import dualquest.util.TaskManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Quest {

	private int timeToEnd;

	public abstract String getName();

	public abstract List<String> getDescriptionLines();

	public void onActivate() {
	}

	public void onDeactivate() {
	}

	public void onComplete() {
	}

	public void onFail() {
	}

	public abstract int getDuration();

	public void onTimeEnd() {
		fail();
	}

	public void update() {
		if(TaskManager.isSecUpdated()) {
			timeToEnd--;
			if(timeToEnd <= 0) {
				onTimeEnd();
			}
		}
	}

	public final void complete() {
		onComplete();
		deactivate();
	}

	public final void fail() {
		onFail();
		deactivate();
	}

	public final void activate() {
		if(this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, Plugin.INSTANCE);
		}
		timeToEnd = getDuration() * 60;
		QuestManager.currentQuest = this;
		onActivate();
		Broadcaster.each(PlayerHandler.getPlayerList().selector().questers().selectPlayers()).and(PlayerHandler.getSpectators()).toChat(getAnnounceMessage())
				.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.8F).title(ChatColor.DARK_AQUA + "Новый квест", ChatColor.AQUA + getName(), 10, 40, 20);
		Broadcaster.each(PlayerHandler.getPlayerList().selector().attackers().selectPlayers())
				.title("", ChatColor.RED + "Квестеры получают новый квест!", 10, 40, 20).sound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.8F);
	}

	public final void deactivate() {
		onDeactivate();
		if(this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}
		QuestManager.currentQuest = null;
	}

	public final int getTimeToEnd() {
		return timeToEnd;
	}

	public final boolean isActive() {
		return QuestManager.getCurrentQuest() != null && QuestManager.getCurrentQuest() == this;
	}

	public final List<String> getAnnounceMessage() {
		List<String> lines = new ArrayList<>();
		String name =
				ChatColor.DARK_GRAY + "----- " + ChatColor.YELLOW + ChatColor.BOLD + "Квест: " + ChatColor.RESET + ChatColor.AQUA + getName() + ChatColor.DARK_GRAY
						+ " -----";
		lines.add(name);
		lines.add("");
		lines.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Задача:");
		lines.addAll(getDescriptionLines().stream().map(str -> ChatColor.DARK_GRAY + "- " + str).collect(Collectors.toList()));
		lines.add("");
		lines.add(ChatColor.DARK_GRAY + StringUtils.repeat("-", 20 + getName().length()));
		return lines;
	}

}
