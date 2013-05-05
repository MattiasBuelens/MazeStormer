package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import mazestormer.game.DefaultGameListener;
import mazestormer.game.Game;
import mazestormer.maze.ClosestTileComparator;
import mazestormer.maze.DefaultMazeListener;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

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
		setMode(new ExploreIslandControlMode(player, this));
	}

	/*
	 * Getters
	 */

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	public int getObjectNumber() {
		return objectNumber;
	}

	private void setObjectNumber(int nb) {
		this.objectNumber = nb;
	}

	public Game getGame() {
		return game;
	}

	public IMaze getMaze() {
		return getPlayer().getMaze();
	}

	/*
	 * ControlMode management
	 */

	@Override
	public ControlMode nextMode(ControlMode currentMode) {
		if (currentMode instanceof ExploreIslandControlMode) {
			// other unexplored islands => LeaveIslandControlMode
			// no other unexplored islands => DriveToCenterControlMode
			if (otherIslands()) {
				return new LeaveIslandControlMode(getPlayer(), this);
			} else {
				return new DriveToCenterControlMode(getPlayer(), this);
			}
		} else if (currentMode instanceof LeaveIslandControlMode) {
			return new ExploreIslandControlMode(getPlayer(), this);
		} else if (currentMode instanceof DriveToCenterControlMode) {
			// should never happen
			log("Fatal error: drive to center ran out of tiles");
			return null;
		} else if (currentMode instanceof DriveToPartnerControlMode) {
			// should never happen
			log("Fatal error: drive to partner ran out of tiles");
			return null;
		} else {
			log("Fatal error: unknown control mode " + currentMode.getClass().getSimpleName());
			return null;
		}
	}

	private void driveToPartner() {
		// TODO Is it allowed to switch modes asynchronously?
		setMode(new DriveToPartnerControlMode(getPlayer(), this));
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

	public boolean otherSideUnexplored(Tile seesawBarcodeTile) {
		Barcode barcode = seesawBarcodeTile.getBarcode();
		Tile otherBarcodeTile = getMaze().getBarcodeTile(Seesaw.getOtherBarcode(barcode));
		for (Orientation orientation : Orientation.values()) {
			if (getMaze().getNeighbor(otherBarcodeTile, orientation).isSeesaw())
				return !getMaze().getNeighbor(otherBarcodeTile, orientation.rotateClockwise(2)).isExplored();
		}
		return false;
	}

	private List<Tile> getReachableSeesawBarcodeTiles(Barcode barcode) {
		List<Tile> reachableTiles = new ArrayList<>();
		final PathFinder pf = new PathFinder(getMaze());
		for (Tile tile : getMaze().getBarcodeTiles()) {
			Barcode tileBarcode = tile.getBarcode();
			if (Seesaw.isSeesawBarcode(tileBarcode) && !tileBarcode.equals(barcode)
					&& !tileBarcode.equals(Seesaw.getOtherBarcode(barcode))
					&& !pf.findTilePathWithoutSeesaws(getDriver().getCurrentTile(), tile).isEmpty()
					&& otherSideUnexplored(tile)) {
				reachableTiles.add(tile);
			}
		}
		Collections.sort(reachableTiles, new ClosestTileComparator(getDriver().getCurrentTile()) {
			@Override
			public List<?> createPath(Tile startTile, Tile goalTile) {
				return pf.findTilePathWithoutSeesaws(startTile, goalTile);
			}
		});
		reachableTiles.add(getMaze().getBarcodeTile(barcode));
		return reachableTiles;
	}

	private boolean otherIslands() {
		for (Tile tile : getMaze().getBarcodeTiles()) {
			Barcode tileBarcode = tile.getBarcode();
			if (Seesaw.isSeesawBarcode(tileBarcode) && otherSideUnexplored(tile)) {
				return true;
			}
		}
		return false;
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

		@Override
		public void onPartnerDisconnected(Player partner) {
			// TODO Stop driving to partner
			// Revert to exploring the maze
		}

		@Override
		public void onMazesMerged() {
			driveToPartner();
		}

	}

	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

}
