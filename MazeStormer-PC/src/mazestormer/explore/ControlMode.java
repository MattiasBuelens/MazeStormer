package mazestormer.explore;

import java.util.logging.Level;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public abstract class ControlMode {

	private final Player player;
	private final PathFinder pathFinder;
	private final BarcodeMapping barcodeMapping;

	public ControlMode(Player player, BarcodeMapping barcodeMapping) {
		this.player = player;
		this.pathFinder = new PathFinder(getMaze());
		this.barcodeMapping = barcodeMapping;
	}

	protected Player getPlayer() {
		return player;
	}

	protected IMaze getMaze() {
		return getPlayer().getMaze();
	}

	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

	public abstract void takeControl(Driver driver);

	public abstract void releaseControl(Driver driver);

	public abstract Tile nextTile(Tile currentTile);

	public abstract boolean isBarcodeActionEnabled();

	public PathFinder getPathFinder() {
		return pathFinder;
	}

	public BarcodeMapping getBarcodeMapping() {
		return barcodeMapping;
	}

}
