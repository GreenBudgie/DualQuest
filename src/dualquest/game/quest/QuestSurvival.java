package dualquest.game.quest;

import com.google.common.collect.Lists;
import dualquest.game.player.PlayerHandler;
import dualquest.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

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
	public int getDuration() {
		return 10;
	}

	@Override
	public void update() {
		super.update();
		PlayerHandler.getPlayerList().selector().aliveQuesters().selectPlayers().forEach(player -> EntityUtils.setMaxHealth(player, 10, false));
	}

	@Override
	public void onDeactivate() {
		PlayerHandler.getPlayerList().selector().aliveQuesters().selectPlayers().forEach(player -> EntityUtils.setMaxHealth(player, 20, false));
	}

	@Override
	public void onTimeEnd() {
		complete();
	}

}
