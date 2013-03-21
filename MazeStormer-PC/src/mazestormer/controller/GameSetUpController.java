package mazestormer.controller;

import java.io.IOException;

import lejos.robotics.navigation.Pose;
import mazestormer.game.ConnectionMode;
import mazestormer.game.Game;
import mazestormer.game.GameListener;
import mazestormer.game.GameRunner;
import mazestormer.player.Player;
import mazestormer.simulator.VirtualRobot;
import mazestormer.world.World;
import peno.htttp.Callback;

import com.rabbitmq.client.Connection;

public class GameSetUpController extends SubController implements IGameSetUpController {

	private Connection connection;
	private Game game;
	// TODO @Matthias: Add WorldSimulator to index FFS!
	// private WorldSimulator worldSimulator;
	private GameRunner runner;

	public GameSetUpController(MainController mainController) {
		super(mainController);
	}

	private World getWorld() {
		return getMainController().getWorld();
	}

	private void logToAll(String message) {
		for (Player player : getWorld().getPlayers()) {
			logTo(player, message);
		}
	}

	private void logTo(String playerID, String message) {
		logTo(getWorld().getPlayer(playerID), message);
	}

	private void logTo(Player player, String message) {
		player.getLogger().info(message);
	}

	@Override
	public String getPlayerID() {
		return getMainController().getPlayer().getPlayerID();
	}

	@Override
	public void setPlayerID(String newPlayerID) {
		Player player = getMainController().getPlayer();
		getMainController().getWorld().renamePlayer(player, newPlayerID);
	}

	private void createGame(ConnectionMode connectionMode, String gameID) throws IOException {
		final Player localPlayer = getMainController().getPlayer();

		connection = connectionMode.newConnection();

		game = new Game(connection, gameID, localPlayer, getMainController().getWorld());
		game.addGameListener(gl);

		// TODO Uncomment when WorldSimulator is added
		// worldSimulator = new WorldSimulator(connection, gameID, localPlayer, getWorld());

		runner = new GameRunner(localPlayer, game) {
			@Override
			protected void log(String message) {
				logTo(localPlayer, message);
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
	public void setReady(final boolean isReady) {
		if (game == null) {
			logToAll("Error when readying: not connected.");
			return;
		}

		try {
			game.setReady(isReady, new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
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

	@Override
	public void pauseGame() {
		if (game == null) {
			logToAll("Error when pausing: not connected.");
			return;
		}

		try {
			game.pause();
		} catch (IllegalStateException | IOException e) {
			logToAll("Error when pausing: " + e.getMessage());
		}
	}

	@Override
	public void stopGame() {
		if (game == null) {
			logToAll("Error when stopping: not connected.");
			return;
		}

		try {
			game.stop();
		} catch (IllegalStateException | IOException e) {
			logToAll("Error when stopping: " + e.getMessage());
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
				&& getMainController().getWorld().getMaze().getNumberOfTiles() == 0) {
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

		// TODO: Move to simulator

		@Override
		public void onGameJoined() {
			// TODO Add all non-local players in simulator
			// for (String playerID : game.getPlayers()) {
			// if (!getGameController().isPersonalPlayer(playerID)) {
			// getGameController().addPlayer(playerID);
			// }
			// }
			// Log
			logToAll("Joined");
			postState(GameSetUpEvent.EventType.JOINED);
		}

		@Override
		public void onGameLeft() {
			// TODO Remove all non-local players on simulator disconnect
			// getGameController().removeOtherPlayers();
			// Log
			logToAll("Left");
			postState(GameSetUpEvent.EventType.LEFT);
		}

		@Override
		public void onGameRolled(int playerNumber) {
			logToAll("Player number rolled: " + playerNumber);
		}

		@Override
		public void onGameStarted() {
			logToAll("Game started");
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
		public void onPlayerReady(String playerID, boolean isReady) {
			logTo(playerID, isReady ? "Ready" : "Not ready");
		}

		@Override
		public void onObjectFound(String playerID) {
			logTo(playerID, "Player " + playerID + " found their object");
		}

		@Override
		public void onPositionUpdate(String playerID, Pose pose) {
			// TODO Handle position updates in simulator
			getWorld().getPlayer(playerID).getRobot().getPoseProvider().setPose(pose);
		}

	};
}