package mazestormer.game;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.ExplorerRunner;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

public class GameRunner implements GameListener {

	/**
	 * The frequency of position updates.
	 */
	private static final long updateFrequency = 2000; // in ms

	private final Player player;
	private final Game game;
	private final ExplorerRunner explorerRunner;

	private final PositionReporter positionReporter;
	private final PositionPublisher positionPublisher;
	private final ScheduledExecutorService positionExecutor = Executors.newSingleThreadScheduledExecutor(factory);

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GameRunner-%d").build();

	private int objectNumber;

	public GameRunner(Player player, Game game) {
		this.player = player;
		this.explorerRunner = new ExplorerRunner(player) {
			@Override
			protected void log(String message) {
				GameRunner.this.log(message);
			}
		};
		explorerRunner.setBarcodeMapping(new TeamTreasureTrekBarcodeMapping(this));

		this.game = game;
		game.addGameListener(this);

		this.positionReporter = new PositionReporter();
		this.positionPublisher = new PositionPublisher();
	}

	protected void log(String message) {
		System.out.println(message);
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	public int getObjectNumber() {
		return objectNumber;
	}

	public void setWallsOnNextTile() {
		log("Object on next tile, set walls");

		// Set all unknown edges to walls
		Tile nextTile = explorerRunner.getNextTile();
		EnumSet<Orientation> unknownSides = nextTile.getUnknownSides();
		for (Orientation side : unknownSides) {
			explorerRunner.getMaze().setEdge(nextTile.getPosition(), side, EdgeType.WALL);
		}
	}

	public void objectFound() {
		log("Report own object found");
		// Report object found
		game.objectFound();
		// Done
		stopGame();
	}

	public void afterObjectBarcode() {
		log("Object found, go to next tile");
		// Skip next tile
		explorerRunner.skipNextTile();
		// Create new path
		explorerRunner.createPath();
		// Object found action resolves after this
	}

	public boolean isRunning() {
		return explorerRunner.isRunning();
	}

	private void stopGame() {
		// Stop
		explorerRunner.stop();
		// Stop pilot
		getRobot().getPilot().stop();
		// Stop reporting
		stopPositionReport();
	}

	private void startPositionReport() {
		getRobot().getPilot().addMoveListener(positionReporter);
	}

	private void stopPositionReport() {
		getRobot().getPilot().removeMoveListener(positionReporter);
	}

	@Override
	public void onGameJoined() {
	}

	@Override
	public void onGameLeft() {
		// Stop
		onGameStopped();
	}

	@Override
	public void onGameRolled(int playerNumber) {
		// Store object number
		objectNumber = playerNumber;
	}

	@Override
	public void onGameStarted() {
		// Reset player pose
		// TODO Do not reset when resuming from paused game?
		getRobot().getPoseProvider().setPose(new Pose());
		// Start
		explorerRunner.start();
		// Start reporting
		startPositionReport();
	}

	@Override
	public void onGamePaused() {
		// Pause
		explorerRunner.pause();
		// Stop pilot
		getRobot().getPilot().stop();
		// Stop reporting
		stopPositionReport();
	}

	@Override
	public void onGameStopped() {
		stopGame();
	}

	@Override
	public void onPlayerReady(String playerID, boolean isReady) {
	}

	@Override
	public void onObjectFound(String playerID) {
	}

	@Override
	public void onPositionUpdate(String playerID, Pose pose) {
	}

	private class PositionReporter implements MoveListener {

		private ScheduledFuture<?> task;

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			if (task == null) {
				// Start publishing
				task = positionExecutor.scheduleWithFixedDelay(positionPublisher, 0, updateFrequency,
						TimeUnit.MILLISECONDS);
			}
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
			if (task != null) {
				// Stop publishing
				task.cancel(false);
				task = null;
			}
		}

	}

	private class PositionPublisher implements Runnable {

		@Override
		public void run() {
			game.updatePosition(getRobot().getPoseProvider().getPose());
		}

	}

}
