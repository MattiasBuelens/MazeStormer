package mazestormer.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.explore.Commander;
import mazestormer.explore.ControlMode;
import mazestormer.maze.DefaultMazeListener;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.LongPoint;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class GameRunner extends Commander implements GameListener {

	/**
	 * The frequency of position updates.
	 */
	private static final long updateFrequency = 2000; // in ms

	/*
	 * Data
	 */
	private int objectNumber;
	private final Game game;

	/*
	 * Control Modes
	 */
	private final FindObjectControlMode findObjectMode;

	/*
	 * Utilities
	 */
	private final PositionReporter positionReporter = new PositionReporter();
	private final TileReporter tileReporter = new TileReporter();
	private final ScheduledExecutorService positionExecutor = Executors.newSingleThreadScheduledExecutor(factory);

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GameRunner-%d").build();

	public GameRunner(Player player, Game game) {
		super(player);

		// Game
		this.game = game;
		game.addGameListener(this);

		// Modes
		findObjectMode = new FindObjectControlMode(player, this);
		setStartMode(findObjectMode);
	}

	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

	/*
	 * Getters
	 */
	
	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	private IMaze getMaze() {
		return getPlayer().getMaze();
	}

	public int getObjectNumber() {
		return objectNumber;
	}
	
	public Tile getCurrentTile() {
		return getDriver().getCurrentTile();
	}
	
	public Game getGame(){
		return game;
	}
	
	/*
	 * Utilities
	 */

	

	public void onSeesaw(int barcode) {
		log("The seesaw is currently opened, onwards!");
		game.lockSeesaw(barcode);
	}

	public void offSeesaw() {
		game.unlockSeesaw();
	}

	public boolean isRunning() {
		return getDriver().isRunning();
	}

	private void startReporting() {
		// Position
		getRobot().getPilot().addMoveListener(positionReporter);
		// Tiles
		getPlayer().getMaze().addListener(tileReporter);
	}

	private void stopReporting() {
		// Position
		getRobot().getPilot().removeMoveListener(positionReporter);
		// Tiles
		getPlayer().getMaze().addListener(tileReporter);
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
	public void onGameRolled(int playerNumber, int objectNumber) {
		// Store object number
		this.objectNumber = objectNumber;
	}

	@Override
	public void onGameStarted() {
		// Reset player pose
		// TODO Do not reset when resuming from paused game?
		getRobot().getPoseProvider().setPose(new Pose());
		// Start reporting
		startReporting();
		// Start explorer
		start();
	}

	@Override
	public void onGamePaused() {
		// Pause explorer
		getDriver().pause();
		// Stop pilot
		getRobot().getPilot().stop();
		// Stop reporting
		stopReporting();
	}

	@Override
	public void onGameStopped() {
		// Stop explorer
		stop();
		// Stop pilot
		getRobot().getPilot().stop();
		// Stop reporting
		stopReporting();
	}

	@Override
	public void onGameWon(int teamNumber) {
		// Not really needed, since it will be stopped later on
		// onGameStopped();
	}

	@Override
	public void onPlayerReady(String playerID, boolean isReady) {
	}

	@Override
	public void onObjectFound(String playerID) {
	}
	
	@Override
	public void onPartnerConnected(Player partner) {
		// Send own maze
		game.sendOwnTiles();
	}

	@Override
	public void onPartnerDisconnected(Player partner) {
	}

	private class PositionReporter implements MoveListener {

		private ScheduledFuture<?> task;

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			if (task != null)
				return;

			// Start publishing
			task = positionExecutor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					game.updatePosition(getRobot().getPoseProvider().getPose());
				}
			}, 0, updateFrequency, TimeUnit.MILLISECONDS);
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
			if (task == null)
				return;

			// Stop publishing
			task.cancel(false);
			task = null;
		}

	}

	private class TileReporter extends DefaultMazeListener {

		@Override
		public void tileExplored(Tile tile) {
			game.sendTiles(tile);
		}

	}

}
