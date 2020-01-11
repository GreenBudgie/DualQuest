package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
import dualquest.game.player.PlayerTeam;
import dualquest.util.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {

	private static Scoreboard lobbyScoreboard;

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
		for(Player player : PlayerHandler.getInGamePlayers()) {
			Scoreboard gameScoreboard = player.getScoreboard();
			Team lobbyTeam = gameScoreboard.getTeam("LobbyTeam");
			Team questersTeam = gameScoreboard.getTeam("QuestersTeam");
			Team attackersTeam = gameScoreboard.getTeam("AttackersTeam");
			Team spectatorsTeam = gameScoreboard.getTeam("SpectatorsTeam");
			lobbyTeam.getEntries().clear();
			questersTeam.getEntries().clear();
			attackersTeam.getEntries().clear();
			spectatorsTeam.getEntries().clear();
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

	public static void updateGameScoreboard() {

	}

	public static void createLobbyScoreboard(Player player) {
		Scoreboard lobbyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team lobbyTeam = lobbyScoreboard.registerNewTeam("LobbyTeam");
		Team gameTeam = lobbyScoreboard.registerNewTeam("GameTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		lobbyTeam.setCanSeeFriendlyInvisibles(true);
		lobbyTeam.setColor(ChatColor.GOLD);
		gameTeam.setColor(ChatColor.DARK_AQUA);
		updateLobbyTeams();
		player.setScoreboard(lobbyScoreboard);
		ScoreboardHandler.lobbyScoreboard = lobbyScoreboard;
	}

	public static void updateLobbyTeams() {
		Team lobbyTeam = lobbyScoreboard.getTeam("LobbyTeam");
		Team gameTeam = lobbyScoreboard.getTeam("GameTeam");
		lobbyTeam.getEntries().clear();
		gameTeam.getEntries().clear();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(PlayerHandler.isInLobby(player)) {
				lobbyTeam.addEntry(player.getName());
			}
			if(PlayerHandler.isInGame(player)) {
				gameTeam.addEntry(player.getName());
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
