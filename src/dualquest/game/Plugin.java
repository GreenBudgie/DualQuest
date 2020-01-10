package dualquest.game;

import dualquest.command.CommandGM;
import dualquest.command.CommandPing;
import dualquest.command.CommandTest;
import dualquest.game.logic.DualQuest;
import dualquest.game.logic.GameState;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	public static Plugin INSTANCE;

	public void onEnable() {
		INSTANCE = this;

		getCommand("gm").setExecutor(new CommandGM());
		getCommand("ping").setExecutor(new CommandPing());
		getCommand("test").setExecutor(new CommandTest());

		DualQuest.init();
	}

	public void onDisable() {
		if(GameState.isPlaying()) DualQuest.endGame();
		DualQuest.effectManager.dispose();
	}

}
