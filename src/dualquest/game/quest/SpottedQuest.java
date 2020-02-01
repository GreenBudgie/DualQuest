package dualquest.game.quest;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SpottedQuest {

	public List<Spot> spots = new ArrayList<>();

	public void onSpotEnter(Spot spot, Player player) {
	}

	public void onSpotLeave(Spot spot, Player player) {
	}

	public void onCatch(Spot spot, Player player) {
	}

	public void unregisterSpots() {
		spots.forEach(Spot::unregister);
	}

}
