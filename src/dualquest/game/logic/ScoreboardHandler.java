package dualquest.game.logic;

import dualquest.game.player.DQPlayer;
import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.game.quest.Quest;
import dualquest.game.quest.QuestManager;
import dualquest.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class ScoreboardHandler {

	public static Scoreboard lobbyScoreboard;

	public static void createGameScoreboard(Player player) {
		Scoreboard gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team lobbyTeam = gameScoreboard.registerNewTeam("LobbyTeam");
		Team questersTeam = gameScoreboard.registerNewTeam("QuestersTeam");
		Team attackersTeam = gameScoreboard.registerNewTeam("AttackersTeam");
		Team spectatorsTeam = gameScoreboard.registerNewTeam("SpectatorsTeam");
		lobbyTeam.setColor(ChatColor.GOLD);
		questersTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		questersTeam.setColor(ChatColor.BLUE);
		questersTeam.setCanSeeFriendlyInvisibles(true);
		attackersTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		attackersTeam.setColor(ChatColor.RED);
		attackersTeam.setCanSeeFriendlyInvisibles(true);
		spectatorsTeam.setColor(ChatColor.GRAY);
		player.setScoreboard(gameScoreboard);
	}

	public static void updateGameTeams() {
		if(!GameState.isPlaying()) return;
		for(Player player : PlayerHandler.getInGamePlayers()) {
			Scoreboard gameScoreboard = player.getScoreboard();
			Team lobbyTeam = gameScoreboard.getTeam("LobbyTeam");
			Team questersTeam = gameScoreboard.getTeam("QuestersTeam");
			Team attackersTeam = gameScoreboard.getTeam("AttackersTeam");
			Team spectatorsTeam = gameScoreboard.getTeam("SpectatorsTeam");
			removeEntriesFromTeams(lobbyTeam, questersTeam, attackersTeam, spectatorsTeam);
			for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if(PlayerHandler.isInLobby(currentPlayer)) {
					lobbyTeam.addEntry(currentPlayer.getName());
				}
				if(PlayerHandler.isInGame(currentPlayer)) {
					if(PlayerHandler.isSpectator(currentPlayer)) {
						spectatorsTeam.addEntry(currentPlayer.getName());
					} else {
						PlayerTeam team = PlayerTeam.getTeam(currentPlayer);
						if(team != null) {
							if(team == PlayerTeam.QUESTERS) {
								questersTeam.addEntry(currentPlayer.getName());
							} else {
								attackersTeam.addEntry(currentPlayer.getName());
							}
						}
					}
				}
			}
		}
	}

	public static void updateGameScoreboard(Player player) {
		DQPlayer dqPlayer = DQPlayer.fromPlayer(player);
		boolean hasDQ = dqPlayer != null;
		Scoreboard board = player.getScoreboard();
		Objective gameInfo = board.getObjective("gameInfo");
		if(gameInfo != null) gameInfo.unregister();
		gameInfo = board.registerNewObjective("gameInfo", "dummy", DualQuest.getLogo());
		gameInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		int c = 0;
		Quest currentQuest = QuestManager.getCurrentQuest();
		if((PlayerHandler.isSpectator(player) || (hasDQ && dqPlayer.getTeam() == PlayerTeam.QUESTERS)) && currentQuest != null) {
			gameInfo.getScore(ChatColor.LIGHT_PURPLE + "Квест" + ChatColor.GRAY + ": " + ChatColor.AQUA + currentQuest.getName() + ChatColor.GRAY + " ("
					+ ChatColor.DARK_AQUA + TaskManager.formatTime(currentQuest.getTimeToEnd()) + ChatColor.GRAY + ")").setScore(c++);
		}
		if(PlayerHandler.isPlaying(player) && hasDQ) {
			for(DQPlayer teammate : PlayerHandler.getPlayerList().selector().team(dqPlayer.getTeam()).select()) {
				String prefix = ChatColor.DARK_GRAY + "- ";
				if(!teammate.isValid()) {
					prefix += ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH;
				} else if(teammate.isRespawning() || teammate.isTemporaryDead()) {
					prefix += ChatColor.GRAY;
				} else if(teammate.equals(dqPlayer)) {
					prefix += ChatColor.GOLD + "" + ChatColor.BOLD;
				} else {
					prefix += ChatColor.GOLD;
				}
				gameInfo.getScore(prefix + teammate.getPlayerName()).setScore(c++);
			}
			gameInfo.getScore(ChatColor.YELLOW + "Команда " + dqPlayer.getTeam().getCases().getGenitive() + ChatColor.GRAY + ":").setScore(c);
		}
	}

	public static void createLobbyScoreboard() {
		Scoreboard lobbyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team lobbyTeam = lobbyScoreboard.registerNewTeam("LobbyTeam");
		Team gameTeam = lobbyScoreboard.registerNewTeam("GameTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		lobbyTeam.setCanSeeFriendlyInvisibles(true);
		lobbyTeam.setColor(ChatColor.GOLD);
		gameTeam.setColor(ChatColor.DARK_AQUA);
		ScoreboardHandler.lobbyScoreboard = lobbyScoreboard;
		for(Player player : PlayerHandler.getLobbyPlayers()) {
			player.setScoreboard(lobbyScoreboard);
		}
		updateLobbyTeams();
	}

	public static void updateLobbyTeams() {
		if(lobbyScoreboard == null) return;
		Team lobbyTeam = lobbyScoreboard.getTeam("LobbyTeam");
		Team gameTeam = lobbyScoreboard.getTeam("GameTeam");
		removeEntriesFromTeams(lobbyTeam, gameTeam);
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(PlayerHandler.isInLobby(player)) {
				lobbyTeam.addEntry(player.getName());
				player.setScoreboard(lobbyScoreboard);
			}
			if(PlayerHandler.isInGame(player)) {
				gameTeam.addEntry(player.getName());
			}
		}
	}

	public static void updateLobbyTeamsLater() {
		TaskManager.invokeLater(ScoreboardHandler::updateLobbyTeams);
	}

	private static void removeEntriesFromTeams(Team... teams) {
		for(Team team : teams) {
			Set<String> entries = team.getEntries();
			for(String entry : entries) {
				team.removeEntry(entry);
			}
		}
	}

	public static void updateScoreboardTeams() {
		updateLobbyTeams();
		updateGameTeams();
	}

	public static void updateScoreboardTeamsLater() {
		TaskManager.invokeLater(() -> {
			updateLobbyTeams();
			updateGameTeams();
		});
	}

}
