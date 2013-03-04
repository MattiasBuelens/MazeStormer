package mazestormer.game;

import java.util.EnumSet;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.ExplorerRunner;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Orientation;
import mazestormer.player.Game;
import mazestormer.player.GameListener;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

public class GameRunner implements GameListener {

	private final Player player;
	private final Game game;
	private final ExplorerRunner explorerRunner;

	private int objectNumber;

	public GameRunner(Player player, Game game) {
		this.player = player;
		this.explorerRunner = new ExplorerRunner(player);
		explorerRunner.setBarcodeMapping(new TeamTreasureTrekBarcodeMapping(
				this));

		this.game = game;
		game.addGameListener(this);
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	public int getObjectNumber() {
		return objectNumber;
	}

	public void setWallsOnNextTile() {
		// Set all unknown edges to walls
		EnumSet<Orientation> unknownSides = explorerRunner.getNextTile()
				.getUnknownSides();
		for (Orientation or : unknownSides) {
			explorerRunner.getNextTile().setEdge(or, EdgeType.WALL);
		}
	}

	public void objectFound() {
		// Report object found
		game.objectFound();
	}

	public void afterObjectBarcode() {
		// Remove next tile from queue
		explorerRunner.pollTile();

		// Create new path
		explorerRunner.createPath();

		// TODO Let barcode action resolve its future
	}

	@Override
	public void onGameJoined() {
	}

	@Override
	public void onGameLeft() {
	}

	@Override
	public void onGameStarted(int playerNumber) {
		objectNumber = playerNumber;
		explorerRunner.start();
	}

	@Override
	public void onGamePaused() {
		explorerRunner.pause();
		getRobot().getPilot().stop();
	}

	@Override
	public void onGameStopped() {
		explorerRunner.stop();
		getRobot().getPilot().stop();
	}

	@Override
	public void onPlayerJoined(String playerID) {
	}

	@Override
	public void onPlayerLeft(String playerID) {
	}

	@Override
	public void onObjectFound(String playerID) {
	}

	@Override
	public void onPositionUpdate(String playerID, Pose pose) {
	}

}
