package dualquest.game.quest;

import com.google.common.collect.Lists;
import dualquest.game.logic.WorldManager;
import dualquest.game.player.PlayerHandler;
import dualquest.util.TaskManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestCheckpoint extends SpottedQuest {

	private final int toPass = 12;
	private int passed = 0;
	private Spot activeSpot;
	private BossBar completeBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_12);

	@Override
	public String getName() {
		return "Пробежка";
	}

	@Override
	public List<String> getDescriptionLines() {
		List<String> list = new ArrayList<>();
		list.add(ChatColor.GOLD + "На поверхности карты друг за другом появляется " + ChatColor.AQUA + toPass + ChatColor.GOLD + " чекпоинтов. " + ChatColor.BOLD
				+ "Нужно успеть побывать на всех!");
		list.add(
				ChatColor.DARK_GREEN + "Координаты первого чекпоинта: " + ChatColor.DARK_AQUA + activeSpot.getLocation().getBlockX() + " " + activeSpot.getLocation()
						.getBlockY() + " " + activeSpot.getLocation().getBlockZ());
		list.add(ChatColor.YELLOW + "При прохождении одного чекпоинта будет активироваться следующий");
		return list;
	}

	@Override
	public void onActivate() {
		for(int i = 0; i < toPass; i++) {
			Spot spot = new Spot(this, WorldManager.getRandomSurfaceLocation());
			if(i == 0) {
				activeSpot = spot;
			} else {
				spot.setActive(false);
			}
			spots.add(spot);
		}
		List<Spot> sortedSpots = Lists.newArrayList(spots);
		for(int i = 0; i < toPass - 1; i++) {
			Spot currentSpot = sortedSpots.get(i);
			double maxDist = 0;
			Spot furthestSpot = null;
			int index = -1;
			for(int j = i + 1; j < toPass; j++) {
				Spot comparingSpot = sortedSpots.get(j);
				double dist = currentSpot.getLocation().distance(comparingSpot.getLocation());
				if(dist > maxDist) {
					furthestSpot = comparingSpot;
					maxDist = dist;
					index = j;
				}
			}
			sortedSpots.set(index, sortedSpots.get(i + 1));
			sortedSpots.set(i + 1, furthestSpot);
		}
		spots = sortedSpots;
		for(int i = 0; i < toPass; i++) {
			Spot spot = spots.get(i);
			spot.setLabel(ChatColor.GOLD + "Чекпоинт " + ChatColor.DARK_GRAY + "#" + ChatColor.AQUA + (i + 1));
		}
		completeBar.setVisible(true);
		completeBar.setTitle(ChatColor.YELLOW + "Чекпоинт: " + ChatColor.DARK_AQUA + activeSpot.getLocation().getBlockX() + " " + activeSpot.getLocation()
				.getBlockY() + " " + activeSpot.getLocation().getBlockZ());
	}

	@Override
	public void onSpotEnter(Spot spot, Player player) {
		if(spot == activeSpot) {
			if(passed < toPass - 1) {
				Firework firework = (Firework) spot.getLocation().getWorld().spawnEntity(spot.getLocation(), EntityType.FIREWORK);
				FireworkMeta meta = firework.getFireworkMeta();
				meta.setPower(2);
				meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).build());
				firework.setFireworkMeta(meta);
				spot.getLocation().getWorld().playSound(spot.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2F, 1F);
				spot.setActive(false);
				activeSpot = spots.get(++passed);
				activeSpot.setActive(true);
				completeBar.setTitle(ChatColor.YELLOW + "Чекпоинт: " + ChatColor.DARK_AQUA + activeSpot.getLocation().getBlockX() + " " + activeSpot.getLocation()
						.getBlockY() + " " + activeSpot.getLocation().getBlockZ());
			} else {
				complete();
			}
		}
	}

	@Override
	public int getDuration() {
		int time = 30;
		for(int i = 0; i < toPass - 1; i++) {
			Spot spot = spots.get(i);
			Spot nextSpot = spots.get(i + 1);
			time += (int) (spot.getLocation().distance(nextSpot.getLocation()) / 8);
		}
		return time / 60;
	}

	@Override
	public void update() {
		super.update();
		if(TaskManager.isSecUpdated()) {
			completeBar.setColor(passed < (toPass / 3) ? BarColor.RED : (passed < (toPass / 1.5) ? BarColor.YELLOW : BarColor.GREEN));
			completeBar.setProgress(passed / (double) toPass);
			completeBar.removeAll();
			for(Player player : PlayerHandler.getWhoCanSeeQuest()) {
				completeBar.addPlayer(player);
			}
		}
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		passed = 0;
		completeBar.setVisible(false);
		completeBar.removeAll();
	}

}
