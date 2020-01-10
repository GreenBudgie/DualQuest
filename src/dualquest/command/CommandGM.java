package dualquest.command;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGM implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		boolean allGood = true;
		if(args.length >= 1) {
			try {
				int mode = Integer.parseInt(args[0]);
				if(mode < 0 || mode > 3) {
					sender.sendMessage(ChatColor.RED + "Такого гейм мода не существует: " + args[0]);
					allGood = false;
				} else {
					p.setGameMode(GameMode.getByValue(mode));
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Такого гейм мода не существует: " + args[0]);
				allGood = false;
			}
		} else {
			p.setGameMode(p.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE);
		}
		if(allGood) {
			String gm = "ERROR";
			switch(p.getGameMode()) {
			case CREATIVE:
				gm = ChatColor.AQUA + "Creative";
				break;
			case SURVIVAL:
				gm = ChatColor.GREEN + "Survival";
				break;
			case ADVENTURE:
				gm = ChatColor.DARK_GREEN + "Adventure";
				break;
			case SPECTATOR:
				gm = ChatColor.DARK_AQUA + "Spectator";
				break;
			}
			sender.sendMessage(ChatColor.YELLOW + "Установлен " + gm);
		}
		return true;
	}
}
