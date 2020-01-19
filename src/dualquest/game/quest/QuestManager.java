package dualquest.game.quest;

import com.google.common.collect.Lists;
import dualquest.util.MathUtils;
import dualquest.util.TaskManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestManager {

	public static final QuestSurvival SURVIVAL = new QuestSurvival();
	public static final QuestFight FIGHT = new QuestFight();

	private static List<Quest> quests;
	private static Set<Quest> previouslyActivated = new HashSet<>();

	protected static Quest currentQuest;
	private static int timeToAddNext = 5;

	public static void init() {
		quests = Lists.newArrayList(SURVIVAL, FIGHT);
	}

	public static List<Quest> getQuests() {
		return quests;
	}

	public static Quest getCurrentQuest() {
		return currentQuest;
	}

	public static void activateRandomQuest() {
		List<Quest> available = Lists.newArrayList(quests);
		available.removeAll(previouslyActivated);
		Quest quest = MathUtils.choose(available);
		quest.activate();
		previouslyActivated.add(quest);
	}

	public static void cleanup() {
		currentQuest = null;
		previouslyActivated.clear();
		timeToAddNext = 5;
	}

	public static void update() {
		if(currentQuest != null) {
			currentQuest.update();
		} else {
			if(TaskManager.isSecUpdated()) {
				if(timeToAddNext > 0) {
					timeToAddNext--;
				} else {
					timeToAddNext = 5;
					activateRandomQuest();
				}
			}
		}
	}

}
