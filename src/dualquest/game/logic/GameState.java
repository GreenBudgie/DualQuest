package dualquest.game.logic;

import java.util.stream.Stream;

public enum GameState {

	STOPPED, VOTING, PREPARING, GAME, ENDING;

	private static GameState currentState = STOPPED;

	public static boolean isState(GameState gameState) {
		return currentState == gameState;
	}

	public static boolean isState(GameState... gameStates) {
		return Stream.of(gameStates).anyMatch(state -> state == currentState);
	}

	public static boolean isPlaying() {
		return !isState(STOPPED);
	}

	public static void setState(GameState newState) {
		currentState = newState;
	}

}
