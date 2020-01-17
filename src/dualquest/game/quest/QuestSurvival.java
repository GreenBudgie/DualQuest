package dualquest.game.quest;

import com.google.common.collect.Lists;
import jdk.nashorn.internal.AssertsEnabled;
import org.bukkit.ChatColor;

import java.util.List;

public class QuestSurvival extends Quest {

	@Override
	public String getName() {
		return "���������";
	}

	@Override
	public List<String> getDescriptionLines() {
		return Lists.newArrayList(ChatColor.GOLD + "�������� � ������� " + ChatColor.AQUA + getDuration() + ChatColor.GOLD + " ����� � ����������� ����������� ��������");
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onComplete() {

	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	@Override
	public int getDuration() {
		return 10;
	}

	@Override
	public void onTimeEnd() {
		complete();
	}
}
