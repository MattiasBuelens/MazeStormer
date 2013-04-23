package mazestormer.explore;

import java.util.logging.Level;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public abstract class ControlMode {

	private final Player player;
	private final Driver driver;
	private final PathFinder pathFinder;
	private final Commander commander;

	public ControlMode(Player player, Commander commander) {
		this.player = player;
		this.pathFinder = new PathFinder(getMaze());
		this.commander = commander;
		this.driver = commander.getDriver();
	}
	
	/*
	 * Getters
	 */

	protected Player getPlayer() {
		return player;
	}

	protected IMaze getMaze() {
		return (IMaze) getPlayer().getMaze();
	}

	public PathFinder getPathFinder() {
		return pathFinder;
	}

	public Commander getCommander() {
		return commander;
	}
	
	public final Driver getDriver() {
		return driver;
	}

	/*
	 * Algemeen geldige methodes
	 */
	
	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}
	
	/*
	 * Abstracte methodes
	 */

	public abstract void takeControl(Driver driver);

	public abstract void releaseControl(Driver driver);

	public abstract Tile nextTile(Tile currentTile);

	public abstract boolean isBarcodeActionEnabled();
	
	public abstract BarcodeMapping getBarcodeMapping();

}
