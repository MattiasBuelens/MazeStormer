package mazestormer.controller;

import java.util.logging.Logger;

import mazestormer.player.Game;
import mazestormer.player.Player;
import mazestormer.simulator.VirtualRobot;
import peno.htttp.Callback;

public class GameSetUpController extends SubController implements IGameSetUpController {

	private Game game;

	public GameSetUpController(MainController mainController) {
		super(mainController);
	}

	private Logger getLogger() {
		return getMainController().getPlayer().getLogger();
	}

	@Override
	public String getPlayerID() {
		return getMainController().getPlayer().getPlayerID();
	}

	@Override
	public void setPlayerID(String playerID) {
		Player player = getMainController().getPlayer();
		player.setPlayerID(playerID);
		postEvent(new PlayerEvent(PlayerEvent.EventType.PLAYER_RENAMED, player));
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
					getLogger().warning("Error when joining: " + t.getMessage());
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
					getLogger().warning("Error when leaving: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			getLogger().warning("Error when leaving: " + e.getMessage());
		}
	}

	@Override
	public void startGame() {
		try {
			if (game == null) {
				throw new Exception("Not connected.");
			}
			game.setReady(true, new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					onStart();
				}

				@Override
				public void onFailure(Throwable t) {
					getLogger().warning("Error when starting: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			getLogger().warning("Error when starting: " + e.getMessage());
		}
	}

	private boolean isReady() {
		// TODO cheating still possible
		if (getMainController().getPlayer().getRobot() == null) {
			return false;
		}

		/*
		 * TODO Do we really care about not having a maze configured? In the
		 * worst case scenario, the virtual robot will simply travel forever in
		 * an empty space.
		 */
		if (getMainController().getPlayer().getRobot() instanceof VirtualRobot
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
		game.terminate();
		game = null;

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
