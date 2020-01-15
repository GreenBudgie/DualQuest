package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
import dualquest.lobby.sign.LobbySignManager;
import dualquest.util.Broadcaster;
import dualquest.util.TaskManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Updates all in-game processes, e.q. game states, win/lose e.t.c. For pre-game mechanics look {@link GameStartManager}
 */
public class GameProcess implements Listener {

	private static int timeToNextPhase = 0;

	public static void update() {
		String actionBar = null;
		switch(GameState.getState()) {
		case VOTING:
			actionBar = ChatColor.YELLOW + "Голосование: " + ChatColor.AQUA + TaskManager.formatTime(timeToNextPhase);
			if(timeToNextPhase <= 0) {
				GameStartManager.endVoting();
			}
			break;
		case PREPARING:
			break;
		case GAME:
			break;
		case ENDING:
			if(timeToNextPhase <= 0) {
				endGame();
			}
			break;
		}
		if(actionBar != null) {
			Broadcaster.inWorld(WorldManager.getGameWorld()).toActionBar(actionBar);
		}
		if(timeToNextPhase > 0) {
			timeToNextPhase--;
		}
	}

	public static void setTimeToNextPhase(int timeInSeconds) {
		timeToNextPhase = timeInSeconds;
	}

	public static int getTimeToNextPhase() {
		return timeToNextPhase;
	}

	public static void endGame() {
		if(!GameState.isPlaying()) throw new IllegalStateException("Cannot end the game at the current moment! It isn't running");
		if(GameState.isPreGame()) {
			GameStartManager.removeGlassPlatforms();
			GameStartManager.hideBar();
		}
		for(Player player : PlayerHandler.getInGamePlayers()) {
			PlayerHandler.reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
			player.setGameMode(GameMode.SURVIVAL);
		}
		PlayerHandler.getSpectators().clear();
		WorldManager.deleteWorld();
		GameState.STOPPED.set();
		LobbySignManager.updateSigns();
	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}

}
