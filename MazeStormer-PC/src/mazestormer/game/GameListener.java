package mazestormer.game;

import lejos.robotics.navigation.Pose;

public interface GameListener {

	public void onGameJoined();

	public void onGameLeft();

	public void onGameRolled(int playerNumber);

	public void onGameStarted();

	public void onGamePaused();

	public void onGameStopped();

	public void onPlayerJoined(String playerID);

	public void onPlayerLeft(String playerID);
	
	public void onPlayerReady(String playerID, boolean isReady);

	public void onObjectFound(String playerID);

	public void onPositionUpdate(String playerID, Pose pose);

}
