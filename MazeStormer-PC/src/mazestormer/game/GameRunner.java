package mazestormer.game;

import java.util.EnumSet;

import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.ExplorerRunner;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Orientation;
import mazestormer.player.Game;
import mazestormer.player.Player;

public class GameRunner {

	private final Game game;
	private final ExplorerRunner explorerRunner;

	public GameRunner(Player player, Game game) {
		this.explorerRunner = new ExplorerRunner(player);
		explorerRunner.setBarcodeMapping(new TeamTreasureTrekBarcodeMapping(
				this));

		this.game = game;
	}

	public int getObjectNumber() {
		return game.getObjectNumber();
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

		// Restart cycle
		explorerRunner.restartCycle();
	}

}
