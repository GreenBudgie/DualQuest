package dualquest.command;

import dualquest.game.player.DQPlayer;
import dualquest.util.Broadcaster;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTest implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		DQPlayer dq = DQPlayer.fromPlayer(p);
		Broadcaster.info(dq.getTeam());
		return true;
	}
}
