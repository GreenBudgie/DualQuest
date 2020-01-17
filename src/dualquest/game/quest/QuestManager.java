package dualquest.game.quest;

import com.google.common.collect.Lists;
import dualquest.util.MathUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestManager {

	public static final QuestSurvival SURVIVAL = new QuestSurvival();
	public static final QuestFight FIGHT = new QuestFight();

	private static List<Quest> quests;
	private static Set<Quest> previouslyActivated = new HashSet<>();

	private static Quest currentQuest;

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
		Quest quest = MathUtils.choose(quests);
	}

	public static void update() {
		if(currentQuest != null) {
			currentQuest.update();
		}
	}

}
