package dualquest.game.logic;

import java.util.stream.Stream;

public enum GameState {

	STOPPED, VOTING, PREPARING, GAME, DEATHMATCH, ENDING;

	public void set() {
		setState(this);
	}

	public void set(int timeToNextPhase) {
		setState(this, timeToNextPhase);
	}

	public boolean isRunning() {
		return currentState == this;
	}

	private static GameState currentState = STOPPED;

	public static GameState getState() {
		return currentState;
	}

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

	public static void setState(GameState newState, int timeToNextPhase) {
		currentState = newState;
		GameProcess.setTimeToNextPhase(timeToNextPhase);
	}

	public static boolean isPreGame() {
		return isState(VOTING, PREPARING);
	}

}
