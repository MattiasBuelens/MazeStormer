package mazestormer.controller;

import java.io.IOException;

import com.rabbitmq.client.Connection;

import lejos.robotics.navigation.Pose;
import mazestormer.game.GameRunner;
import mazestormer.player.Game;
import mazestormer.player.GameListener;
import mazestormer.player.Player;
import mazestormer.rabbitmq.ConnectionMode;
import mazestormer.simulator.VirtualRobot;
import peno.htttp.Callback;

public class GameSetUpController extends SubController implements
		IGameSetUpController {

	private Connection connection;
	private Game game;
	private GameRunner runner;
	private final IGameController gameController;

	public GameSetUpController(MainController mainController,
			IGameController gameController) {
		super(mainController);
		this.gameController = gameController;
	}

	private IGameController getGameController() {
		return this.gameController;
	}

	private void logToAll(String message) {
		getGameController().logToAll(message);
	}

	private void logTo(String playerID, String message) {
		getGameController().logTo(playerID, message);
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

	private void createGame(ConnectionMode connectionMode, String gameID)
			throws IOException {
		final Player localPlayer = getMainController().getPlayer();

		connection = connectionMode.newConnection();

		game = new Game(connection, gameID, localPlayer);
		game.addGameListener(gl);

		runner = new GameRunner(localPlayer, game) {
			@Override
			protected void log(String message) {
				logTo(localPlayer.getPlayerID(), message);
			}
		};
	}

	@Override
	public void joinGame(ConnectionMode connectionMode, String gameID) {
		if (!isReady()) {
			onNotReady();
			return;
		}

		try {
			// Create game
			createGame(connectionMode, gameID);
			// Join game
			game.join(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
				}

				@Override
				public void onFailure(Throwable t) {
					logToAll("Error when joining: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			logToAll("Error when joining: " + e.getMessage());
		}
	}

	@Override
	public void leaveGame() {
		try {
			if (game == null) {
				throw new Exception("Not connected.");
			}
			// Leave game
			game.leave(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
				}

				@Override
				public void onFailure(Throwable t) {
					logToAll("Error when leaving: " + t.getMessage());
				}
			});
			// Close connection
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			logToAll("Error when leaving: " + e.getMessage());
		}
	}

	@Override
	public void startGame() {
		if (game == null) {
			logToAll("Error when readying: not connected.");
			return;
		}

		try {
			game.setReady(true, new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					logToAll("Ready");
				}

				@Override
				public void onFailure(Throwable t) {
					logToAll("Error when readying: " + t.getMessage());
				}
			});
		} catch (Exception e) {
			logToAll("Error when readying: " + e.getMessage());
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

	private void onNotReady() {
		logToAll("Error when joining: not ready to join.");
		postState(GameSetUpEvent.EventType.NOT_READY);
	}

	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}

	private GameListener gl = new GameListener() {

		@Override
		public void onGameJoined() {
			// Add all non-local players
			for (String playerID : game.getPlayers()) {
				if (!getGameController().isPersonalPlayer(playerID)) {
					getGameController().addPlayer(playerID);
				}
			}
			// Log
			logToAll("Joined");
			postState(GameSetUpEvent.EventType.JOINED);
		}

		@Override
		public void onGameLeft() {
			// Remove all non-local players
			getGameController().removeOtherPlayers();
			// Log
			logToAll("Left");
			postState(GameSetUpEvent.EventType.LEFT);
		}

		@Override
		public void onGameStarted(int playerNumber) {
			logToAll("Game started, player number: " + playerNumber);
		}

		@Override
		public void onGamePaused() {
			logToAll("Game paused");
		}

		@Override
		public void onGameStopped() {
			logToAll("Game stopped");
		}

		@Override
		public void onPlayerJoined(String playerID) {
			getGameController().addPlayer(playerID);
			logToAll("Player " + playerID + " joined");
		}

		@Override
		public void onPlayerLeft(String playerID) {
			getGameController().removePlayer(playerID);
			logToAll("Player " + playerID + " left");
		}

		@Override
		public void onObjectFound(String playerID) {
			logTo(playerID, "Player " + playerID + " found their object");
		}

		@Override
		public void onPositionUpdate(String playerID, Pose pose) {
			((Player) getGameController().getPlayer(playerID)).getRobot()
					.getPoseProvider().setPose(pose);
		}

	};
}