package dualquest.game;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	public static Plugin INSTANCE;

	public void onEnable() {
		INSTANCE = this;

		DualQuest.init();
	}

	public void onDisable() {
		if(GameState.isPlaying()) DualQuest.endGame();
		DualQuest.effectManager.dispose();
	}

}
