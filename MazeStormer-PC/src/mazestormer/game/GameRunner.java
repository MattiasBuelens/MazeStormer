package mazestormer.game;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.ExplorerRunner;
import mazestormer.maze.DefaultMazeListener;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.LongPoint;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class GameRunner implements GameListener {

	/**
	 * The frequency of position updates.
	 */
	private static final long updateFrequency = 2000; // in ms

	private final Player player;
	private final Game game;
	private final ExplorerRunner explorerRunner;

	private final PositionReporter positionReporter = new PositionReporter();
	private final TileReporter tileReporter = new TileReporter();
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
	}

	protected void log(String message) {
		System.out.println(message);
	}

	private Player getPlayer() {
		return player;
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	private IMaze getMaze() {
		return getPlayer().getMaze();
	}

	public int getObjectNumber() {
		return objectNumber;
	}

	public void setObjectTile() {
		log("Object on next tile, set walls");

		// Set all unknown edges to walls
		Tile nextTile = explorerRunner.getNextTile();
		EnumSet<Orientation> unknownSides = nextTile.getUnknownSides();
		for (Orientation side : unknownSides) {
			getMaze().setEdge(nextTile.getPosition(), side, EdgeType.WALL);
		}

		// Mark as explored
		getMaze().setExplored(nextTile.getPosition());
	}

	public void setSeesawWalls() {
		log("Seesaw on next tiles, set seesaw and barcode");

		// Set all unknown edges to walls or open
		Tile currentTile = explorerRunner.getCurrentTile();
		Barcode seesawBarcode = currentTile.getBarcode();
		Tile nextTile = explorerRunner.getNextTile();

		Orientation orientation = currentTile.orientationTo(nextTile);
		IMaze maze = getMaze();

		// Seesaw
		LongPoint nextTilePosition = nextTile.getPosition();
		maze.setEdge(nextTilePosition, orientation.rotateClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation.rotateCounterClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation, EdgeType.OPEN);
		// TODO Mark as seesaw

		// Seesaw
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setEdge(nextTilePosition, orientation.rotateClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation.rotateCounterClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation, EdgeType.OPEN);
		// TODO Mark as seesaw

		// Other seesaw barcode
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setEdge(nextTilePosition, orientation.rotateClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation.rotateCounterClockwise(), EdgeType.WALL);
		maze.setEdge(nextTilePosition, orientation, EdgeType.OPEN);
		Barcode otherBarcode = TeamTreasureTrekBarcodeMapping.getOtherSeesawBarcode(seesawBarcode);
		maze.setBarcode(nextTilePosition, otherBarcode);

		// TODO Do we need to mark seesaw tiles as explored here?
	}

	public void objectFound(int teamNumber) {
		log("Own object found, join team #" + teamNumber);
		// Report object found
		game.objectFound();
		// Join team
		game.joinTeam(teamNumber);
		// TODO Start working together
	}

	public void onSeesaw(int barcode) {
		log("The seesaw is currently opened, onwards!");
		game.lockSeesaw(barcode);
	}

	public void offSeesaw() {
		game.unlockSeesaw();
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
		// Start
		explorerRunner.start();
	}

	@Override
	public void onGamePaused() {
		// Pause
		explorerRunner.pause();
		// Stop pilot
		getRobot().getPilot().stop();
		// Stop reporting
		stopReporting();
	}

	@Override
	public void onGameStopped() {
		// Stop
		explorerRunner.stop();
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
