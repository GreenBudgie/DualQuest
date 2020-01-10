package dualquest.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandPing implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(args.length == 0) {
			p.sendMessage(ChatColor.YELLOW + "Твой пинг: " + ChatColor.GREEN + getPing(p));
		}
		if(args.length >= 1) {
			Player p2 = Bukkit.getPlayer(args[0]);
			if(p2 != null) {
				p.sendMessage(
						ChatColor.YELLOW + "Пинг " + ChatColor.GOLD + p.getName() + ": " + ChatColor.GREEN + getPing(p2));
			} else {
				p.sendMessage(ChatColor.RED + "Нет такого игрока");
			}
		}
		return true;
	}

	public int getPing(Player p) {
		int ping = p == null ? -1 : ((CraftPlayer) p).getHandle().ping;
		return ping;
	}

}
