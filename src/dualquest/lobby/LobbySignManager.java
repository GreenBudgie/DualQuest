package dualquest.lobby;

import com.google.common.collect.Lists;
import dualquest.game.DualQuest;
import dualquest.game.Plugin;
import dualquest.game.Rating;
import dualquest.game.WorldManager;
import dualquest.util.WorldHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class LobbySignManager implements Listener {

	private static List<LobbySign> signs = new ArrayList<>();

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new LobbySignManager(), Plugin.INSTANCE);
		LobbySign start = new LobbySign(7, 11, -4, SignType.START);
		LobbySign stats = new LobbySign(6, 11, -4, SignType.STATS);
		LobbySign world = new LobbySign(8, 11, -4, SignType.WORLD);
		signs.addAll(Lists.newArrayList(start, stats, world));
		updateSigns();
	}

	public static LobbySign getSignAt(Location l) {
		for(LobbySign sign : signs) {
			if(WorldHelper.compareLocations(l, sign.getLocation())) return sign;
		}
		return null;
	}

	public static void clearSign(Sign sign) {
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
	}

	public static LobbySign getSignByType(SignType type) {
		for(LobbySign sign : signs) {
			if(sign.getType() == type) return sign;
		}
		return null;
	}

	public static void updateSigns() {
		for(LobbySign sign : signs) {
			Sign block = sign.getSign();
			clearSign(block);
			ChatColor defCol = ChatColor.DARK_BLUE;
			switch(sign.getType()) {
			case START:
				if(DualQuest.playing) {
					block.setLine(1, defCol + "Игра идет...");
					block.setLine(2, ChatColor.DARK_AQUA + "<Наблюдать>");
				} else {
					block.setLine(1, (WorldManager.hasWorlds() ? ChatColor.DARK_GREEN : ChatColor.DARK_GRAY) + "Начать игру");
				}
				break;
			case STATS:
				block.setLine(1, defCol + "Рейтинг:");
				block.setLine(2, Rating.statsEnabled ? (ChatColor.DARK_GREEN + "Включен") : (ChatColor.DARK_RED + "Отключен"));
				break;
			case WORLD:
				if(WorldManager.hasWorlds()) {
					block.setLine(1, ChatColor.DARK_GREEN + "Мир создан");
					block.setLine(2, defCol + "<Удалить>");
				} else {
					block.setLine(1, ChatColor.DARK_RED + "Мир не создан");
					block.setLine(2, defCol + "<Сгенерировать>");
				}
				break;
			}
			block.update();
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!WorldManager.generating && e.getAction() == Action.RIGHT_CLICK_BLOCK && DualQuest.isInLobby(p)) {
			Block block = e.getClickedBlock();
			LobbySign sign = getSignAt(block.getLocation());
			if(sign != null) {
				switch(sign.getType()) {
				case START:
					if(!DualQuest.playing) {
						if(p.isOp()) {
							DualQuest.startGame();
						}
					} else {
						DualQuest.joinSpectators(p);
					}
					break;
				case STATS:
					if(p.isOp() && !DualQuest.playing) {
						Rating.statsEnabled = !Rating.statsEnabled;
					}
					break;
				case WORLD:
					if(p.isOp() && !DualQuest.playing) {
						if(!WorldManager.hasWorlds()) {
							WorldManager.makeWorlds();
						} else {
							if(p.isSneaking()) {
								WorldManager.deleteWorlds();
							}
						}
					}
					break;
				}
				updateSigns();
			}
		}
	}

}
