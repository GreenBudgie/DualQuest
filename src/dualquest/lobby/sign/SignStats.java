package dualquest.lobby.sign;

import dualquest.game.logic.GameState;
import dualquest.game.logic.Rating;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignStats extends LobbySign {

	public SignStats(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void updateText() {
		Sign sign = getSign();
		sign.setLine(1, ChatColor.DARK_BLUE + "�������:");
		sign.setLine(2, Rating.statsEnabled ? (ChatColor.DARK_GREEN + "�������") : (ChatColor.DARK_RED + "��������"));
		sign.update();
	}

	@Override
	public void onClick(Player player) {
		if(player.isOp() && !GameState.isPlaying()) {
			Rating.statsEnabled = !Rating.statsEnabled;
		}
	}

}
