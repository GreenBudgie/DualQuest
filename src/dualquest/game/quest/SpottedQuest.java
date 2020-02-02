package dualquest.game.quest;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SpottedQuest extends Quest {

	public List<Spot> spots = new ArrayList<>();

	public List<Spot> getSpots() {
		return spots;
	}

	public void onSpotEnter(Spot spot, Player player) {
	}

	public void onSpotLeave(Spot spot, Player player) {
	}

	@Override
	public void update() {
		super.update();
		for(Spot spot : spots) {
			spot.update();
		}
	}

	@Override
	public void onDeactivate() {
		unregisterSpots();
	}

	public void unregisterSpots() {
		spots.forEach(Spot::unregister);
	}

}
