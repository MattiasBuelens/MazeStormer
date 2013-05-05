package mazestormer.game;

import mazestormer.player.Player;

public interface GameListener {

	public void onGameJoined();

	public void onGameLeft();

	public void onGameRolled(int playerNumber, int objectNumber);

	public void onGameStarted();

	public void onGamePaused();

	public void onGameStopped();

	public void onGameWon(int teamNumber);

	// ->
	public void onPlayerReady(String playerID, boolean isReady);

	//
	public void onObjectFound(String playerID);

	public void onPartnerConnected(Player partner);

	public void onPartnerDisconnected(Player partner);

	public void onMazesMerged();

}
