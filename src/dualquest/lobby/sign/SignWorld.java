package dualquest.lobby.sign;

import dualquest.game.logic.GameState;
import dualquest.game.logic.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignWorld extends LobbySign {

	public SignWorld(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void updateText() {
		Sign sign = getSign();
		if(WorldManager.hasWorld()) {
			sign.setLine(1, ChatColor.DARK_GREEN + "��� ������");
			sign.setLine(2, ChatColor.DARK_BLUE + "<�������>");
		} else {
			sign.setLine(1, ChatColor.DARK_RED + "��� �� ������");
			sign.setLine(2, ChatColor.DARK_BLUE + "<�������������>");
		}
	}

	@Override
	public void onClick(Player player) {
		if(player.isOp() && !GameState.isPlaying()) {
			if(!WorldManager.hasWorld()) {
				WorldManager.makeWorld();
			} else {
				if(player.isSneaking()) {
					WorldManager.deleteWorld();
				}
			}
		}
	}

}
