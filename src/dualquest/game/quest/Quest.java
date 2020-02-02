package dualquest.game.quest;

import dualquest.game.Plugin;
import dualquest.game.player.PlayerHandler;
import dualquest.util.Broadcaster;
import dualquest.util.TaskManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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

	public void setTimeToEnd(int time) {
		this.timeToEnd = time;
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
		for(Player spectator : PlayerHandler.getSpectators()) {
			spectator.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Квест" + ChatColor.RESET + ChatColor.YELLOW + " Выполнен!", "", 20, 60, 30);
			spectator.playSound(spectator.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
		}
		for(Player quester : PlayerHandler.getPlayerList().selector().questers().selectPlayers()) {
			quester.sendTitle(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Квест" + ChatColor.RESET + ChatColor.GREEN + " Выполнен!", "", 20, 60, 30);
			quester.playSound(quester.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}
		for(Player attacker : PlayerHandler.getPlayerList().selector().attackers().selectPlayers()) {
			attacker.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Квестеры " + ChatColor.RESET + ChatColor.RED + " выполнили квест!",
					ChatColor.AQUA + getName(), 20, 60, 30);
			attacker.playSound(attacker.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		}
		PlayerHandler.resurrectQuesters();
		onComplete();
		deactivate();
	}

	public final void fail() {
		for(Player spectator : PlayerHandler.getSpectators()) {
			spectator.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Квест" + ChatColor.RESET + ChatColor.YELLOW + " не Выполнен!", "", 20, 60, 30);
			spectator.playSound(spectator.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
		}
		for(Player quester : PlayerHandler.getPlayerList().selector().questers().selectPlayers()) {
			quester.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Квест " + ChatColor.RESET + ChatColor.ITALIC + ChatColor.GOLD + "не" + ChatColor.RESET
					+ ChatColor.RED + " Выполнен!", "", 20, 60, 30);
			quester.playSound(quester.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		}
		for(Player attacker : PlayerHandler.getPlayerList().selector().attackers().selectPlayers()) {
			attacker.sendTitle(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Квестеры" + ChatColor.RESET + ChatColor.GREEN + " провалили квест!",
					ChatColor.AQUA + getName(), 20, 60, 30);
			attacker.playSound(attacker.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}
		onFail();
		deactivate();
	}

	public final void activate() {
		if(this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, Plugin.INSTANCE);
		}
		QuestManager.currentQuest = this;
		onActivate();
		timeToEnd = getDuration() * 60;
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
