package mazestormer.controller;

import java.util.logging.Logger;

import mazestormer.player.Game;
import peno.htttp.Callback;

public class GameSetUpController extends SubController implements
		IGameSetUpController {

	private Game game;

	public GameSetUpController(MainController mainController) {
		super(mainController);
	}

	private Logger getLogger() {
		return getMainController().getPlayer().getLogger();
	}

	@Override
	public void joinGame(String gameID) {
		if (!isReady()) {
			onNotReady();
			return;
		}

		try {
			game = new Game(gameID, getMainController().getPlayer());
			game.join(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					onJoin();
				}

				@Override
				public void onFailure(Throwable t) {
					getLogger()
							.warning("Error when joining: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			getLogger().warning("Error when joining: " + e.getMessage());
		}
	}

	@Override
	public void leaveGame() {
		try {
			if (game == null) {
				throw new Exception("Not connected.");
			}
			game.leave(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					onLeave();
				}

				@Override
				public void onFailure(Throwable t) {
					getLogger()
							.warning("Error when leaving: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			getLogger().warning("Error when leaving: " + e.getMessage());
		}
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		onStart();
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
		getLogger().info("Joined");
		postState(GameSetUpEvent.EventType.JOINED);
	}

	private void onStart() {
		// TODO
	}

	private void onLeave() {
		getLogger().info("Left");
		postState(GameSetUpEvent.EventType.LEFT);
	}

	private void onDisconnect() {
		getLogger().info("Disconnected");
		postState(GameSetUpEvent.EventType.DISCONNECTED);
	}

	private void onNotReady() {
		getLogger().info("Not ready");
		postState(GameSetUpEvent.EventType.NOT_READY);
	}

	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}

}
