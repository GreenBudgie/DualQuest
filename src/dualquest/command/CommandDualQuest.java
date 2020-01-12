package dualquest.command;

import com.google.common.collect.Lists;
import dualquest.game.logic.DualQuest;
import dualquest.game.logic.GameStartManager;
import dualquest.game.logic.GameState;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
		if(!sender.isOp()) return true;
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
			DualQuest.endGame();
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return getMatchingStrings(args, "start", "end");
		}
		if(args.length > 1 && args[0].equalsIgnoreCase("start")) {
			return getMatchingStrings(args,
					Arrays.stream(GameStartManager.Option.values()).filter(opt -> !ArrayUtils.contains(args, opt.name().toLowerCase())).map(opt -> opt.name().toLowerCase())
							.collect(Collectors.toList()));
		}
		return null;
	}

}
