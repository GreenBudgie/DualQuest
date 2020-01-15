package dualquest.lobby.sign;

import dualquest.game.logic.DualQuest;
import dualquest.game.logic.GameStartManager;
import dualquest.game.logic.GameState;
import dualquest.game.logic.WorldManager;
import dualquest.game.player.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignStart extends LobbySign {

	public SignStart(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void updateText() {
		clearText();
		Sign sign = getSign();
		if(GameState.isPlaying()) {
			sign.setLine(1, ChatColor.DARK_BLUE + "Игра идет...");
			sign.setLine(2, ChatColor.DARK_AQUA + "<Наблюдать>");
		} else {
			sign.setLine(1, (WorldManager.hasWorld() ? ChatColor.DARK_GREEN : ChatColor.DARK_GRAY) + "Начать игру");
		}
		sign.update();
	}

	@Override
	public void onClick(Player player) {
		if(!GameState.isPlaying()) {
			if(player.isOp()) {
				GameStartManager.startGame();
			}
		} else {
			PlayerHandler.joinSpectators(player);
		}
	}

}
