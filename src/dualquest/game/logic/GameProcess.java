package dualquest.game.logic;

import dualquest.game.player.PlayerHandler;
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
		switch(GameState.getState()) {
		case VOTING:
			break;
		case PREPARING:
			break;
		case GAME:
			break;
		case ENDING:
			break;
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
		}
		for(Player player : PlayerHandler.getInGamePlayers()) {
			PlayerHandler.reset(player);
			player.teleport(WorldManager.getLobby().getSpawnLocation());
		}
		PlayerHandler.getSpectators().clear();
		WorldManager.deleteWorld();
		GameState.STOPPED.set();
	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}

}
