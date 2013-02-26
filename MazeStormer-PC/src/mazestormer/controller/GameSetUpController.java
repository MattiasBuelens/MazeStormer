package mazestormer.controller;

import java.io.IOException;

import mazestormer.player.Game;

public class GameSetUpController extends SubController implements
		IGameSetUpController {

	private Game game;

	public GameSetUpController(MainController mainController) {
		super(mainController);
	}

	@Override
	public void createGame(String gameID) {
		try {
			this.game = new Game(gameID, getMainController().getPlayer());
			onJoin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void joinGame(String gameID) {
		// TODO Auto-generated method stub
		onJoin();
	}

	@Override
	public String[] refreshLobby() {
		// TODO Auto-generated method stub
		String[] lobby = { "one", "two" };
		return lobby;
	}

	@Override
	public void leaveGame() {
		// TODO Auto-generated method stub
		onLeave();
	}

	private boolean isReady() {
		// TODO cheating still possible
		if (getMainController().getPlayer().getRobot() == null) {
			return false;
		} else if (mazestormer.simulator.VirtualRobot.class
				.isInstance(getMainController().getPlayer().getRobot())
				&& getMainController().getSourceMaze().getNumberOfTiles() == 0) {
			return false;
		}
		return true;
	}

	private void onJoin() {
		if (isReady())
			postState(GameSetUpEvent.EventType.JOINED);
		else
			onNotReady();
	}

	private void onLeave() {
		postState(GameSetUpEvent.EventType.LEFT);
	}

	private void onDisconnect() {
		postState(GameSetUpEvent.EventType.DISCONNECTED);
	}

	private void onNotReady() {
		postState(GameSetUpEvent.EventType.NOT_READY);
	}

	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}
}
