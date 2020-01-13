package dualquest.command;

import com.google.common.collect.Lists;
import dualquest.game.logic.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandDualQuest implements CommandExecutor, TabCompleter {

	private static List<String> getMatchingStrings(String[] args, String... possibilities) {
		return getMatchingStrings(args, Arrays.asList(possibilities));
	}

	private static List<String> getMatchingStrings(String[] inputArgs, List<String> possibleCompletions) {
		String arg = inputArgs[inputArgs.length - 1];
		List<String> list = Lists.newArrayList();
		if(!possibleCompletions.isEmpty()) {
			for(String completion : possibleCompletions) {
				if(completion.startsWith(arg)) {
					list.add(completion);
				}
			}
		}
		return list;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp() || !(sender instanceof Player)) return true;
		Player player = (Player) sender;
		if(args.length >= 1 && args[0].equalsIgnoreCase("start") && !GameState.isPlaying()) {
			Set<GameStartManager.Option> options = new HashSet<>();
			for(GameStartManager.Option option : GameStartManager.Option.values()) {
				for(String param : args) {
					if(option.name().equalsIgnoreCase(param)) {
						options.add(option);
						break;
					}
				}
			}
			GameStartManager.startGame(options.toArray(new GameStartManager.Option[0]));
		}
		if(args.length == 1 && args[0].equalsIgnoreCase("end") && GameState.isPlaying()) {
			GameProcess.endGame();
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("tp") && GameState.isPlaying()) {
			if(args[1].equalsIgnoreCase("questers_spawn")) {
				player.teleport(WorldManager.getQuestersSpawn());
			}
			if(args[1].equalsIgnoreCase("attackers_spawn")) {
				player.teleport(WorldManager.getAttackersSpawn());
			}
			if(args[1].equalsIgnoreCase("world_spawn")) {
				player.teleport(WorldManager.getSpawnLocation());
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return getMatchingStrings(args, "start", "end", "tp");
		}
		if(args.length > 1 && args[0].equalsIgnoreCase("start")) {
			return getMatchingStrings(args,
					Arrays.stream(GameStartManager.Option.values()).filter(opt -> !ArrayUtils.contains(args, opt.name().toLowerCase())).map(opt -> opt.name().toLowerCase())
							.collect(Collectors.toList()));
		}
		if(args.length == 2 && args[0].equalsIgnoreCase("tp")) {
			return getMatchingStrings(args, "questers_spawn", "world_spawn", "attackers_spawn");
		}
		return null;
	}

}
