package mazestormer.command.game;

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
import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.game.Game;
import mazestormer.game.DefaultGameListener;
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

public class GameRunner extends Commander {

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
	 * Current control mode
	 */
	ControlMode currentControlmode;
	
	/*
	 * GameListener
	 */

	private final PositionReporter positionReporter = new PositionReporter();
	private final TileReporter tileReporter = new TileReporter();
	private final ScheduledExecutorService positionExecutor = Executors.newSingleThreadScheduledExecutor(factory);

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("GameRunner-%d").build();

	/*
	 * Constructor
	 */
	
	public GameRunner(Player player, Game game) {
		super(player);

		// Game
		this.game = game;
		game.addGameListener(new GameListener());

		// Modes
		setMode(new ExploreIslandControlMode(player,this));
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
	
	private void setObjectNumber(int nb) {
		this.objectNumber = nb;
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

	public void setSeesawWalls() {
		log("Seesaw on next tiles, set seesaw and barcode");

		IMaze maze = getMaze();

		Tile currentTile = getDriver().getCurrentTile();
		Tile nextTile = getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);
		TileShape tileShape = new TileShape(TileType.STRAIGHT, orientation);

		Barcode seesawBarcode = currentTile.getBarcode();
		Barcode otherBarcode = Seesaw.getOtherBarcode(seesawBarcode);

		// Seesaw
		LongPoint nextTilePosition = nextTile.getPosition();
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, seesawBarcode);
		maze.setExplored(nextTilePosition);

		// Seesaw
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);

		// Other seesaw barcode
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setBarcode(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);
	}
	
	public void setObjectTile() {
		Tile currentTile = getDriver().getCurrentTile();
		Tile nextTile = getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);

		// Make next tile a dead end
		getMaze().setTileShape(nextTile.getPosition(),
				new TileShape(TileType.DEAD_END, orientation));

		// Mark as explored
		getMaze().setExplored(nextTile.getPosition());

		// Remove both tiles from the queue

	}

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
	
	private class GameListener extends DefaultGameListener {

		@Override
		public void onGameLeft() {
			// Stop
			onGameStopped();
		}

		@Override
		public void onGameRolled(int playerNumber, int objectNumber) {
			// Store object number
			setObjectNumber(objectNumber);
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
		public void onPartnerConnected(Player partner) {
			// Send own maze
			game.sendOwnTiles();
		}
	}

	/*
	 * ControlMode management
	 */
	
	@Override
	public ControlMode nextMode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Utilities
	 */
	
	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

}
