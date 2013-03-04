package mazestormer.controller;

import java.util.logging.Logger;

import lejos.robotics.navigation.Pose;
import mazestormer.player.Game;
import mazestormer.player.GameListener;
import mazestormer.player.Player;
import mazestormer.simulator.VirtualRobot;
import peno.htttp.Callback;

public class GameSetUpController extends SubController implements IGameSetUpController {

	private Game game;
	private final IGameController gameController;

	public GameSetUpController(MainController mainController, IGameController gameController) {
		super(mainController);
		this.gameController = gameController;
	}
	
	private IGameController getGameController() {
		return this.gameController;
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
			game.addGameListener(this.gl);
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
	
	private GameListener gl = new GameListener(){

		@Override
		public void onGameStarted(int playerNumber) {
			getGameController().logToAll("Game started, player number: " + playerNumber);
		}

		@Override
		public void onGamePaused() {
			getGameController().logToAll("Game paused");
		}

		@Override
		public void onGameStopped() {
			getGameController().logToAll("Game stopped");
		}

		@Override
		public void onPlayerJoined(String playerID) {
			getGameController().addPlayer(playerID);
			getGameController().logToSpecific(playerID, "Player " + playerID + " joined");
			
		}

		@Override
		public void onPlayerLeft(String playerID) {
			getGameController().removePlayer((Player) getGameController().getPlayer(playerID));
			getGameController().logToSpecific(playerID, "Player " + playerID + " left");
			
		}

		@Override
		public void onObjectFound(String playerID) {
			getGameController().logToSpecific(playerID, "Player " + playerID + " found their object");
		}

		@Override
		public void onPositionUpdate(String playerID, Pose pose) {
			((Player) getGameController().getPlayer(playerID)).getRobot().getPoseProvider().setPose(pose);
		}
	};
}