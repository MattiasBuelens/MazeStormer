package mazestormer.player;

import lejos.robotics.navigation.Pose;

public interface GameListener {

	public void onGameJoined();

	public void onGameLeft();

	public void onGameStarted(int playerNumber);

	public void onGamePaused();

	public void onGameStopped();

	public void onPlayerJoined(String playerID);

	public void onPlayerLeft(String playerID);

	public void onObjectFound(String playerID);

	public void onPositionUpdate(String playerID, Pose pose);

}
