package dualquest.game;

import de.slikey.effectlib.EffectManager;
import dualquest.command.CommandDualQuest;
import dualquest.command.CommandGM;
import dualquest.command.CommandPing;
import dualquest.command.CommandTest;
import dualquest.game.logic.DualQuest;
import dualquest.game.logic.GameState;
import dualquest.util.ParticleUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	public static Plugin INSTANCE;

	public void onEnable() {
		INSTANCE = this;

		getCommand("gm").setExecutor(new CommandGM());
		getCommand("ping").setExecutor(new CommandPing());
		getCommand("test").setExecutor(new CommandTest());
		getCommand("dualquest").setExecutor(new CommandDualQuest());

		DualQuest.init();
		ParticleUtils.effectManager = new EffectManager(this);
	}

	public void onDisable() {
		if(GameState.isPlaying()) DualQuest.endGame();
		ParticleUtils.effectManager.dispose();
	}

}
