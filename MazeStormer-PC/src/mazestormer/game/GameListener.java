package mazestormer.game;

import lejos.robotics.navigation.Pose;

public interface GameListener {

	public void onGameJoined();

	public void onGameLeft();

	public void onGameRolled(int playerNumber, int objectNumber);

	public void onGameStarted();

	public void onGamePaused();

	public void onGameStopped();

	//->
	public void onPlayerReady(String playerID, boolean isReady);

	//
	public void onObjectFound(String playerID);

}
