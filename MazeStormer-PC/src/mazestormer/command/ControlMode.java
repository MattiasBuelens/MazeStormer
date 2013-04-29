package mazestormer.command;

import java.util.logging.Level;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.IAction;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public abstract class ControlMode {

	private final Player player;
	private final PathFinder pathFinder;
	private final Commander commander;

	public ControlMode(Player player, Commander commander) {
		this.player = player;
		this.pathFinder = new PathFinder(getMaze());
		this.commander = commander;
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

	/*
	 * Algemeen geldige methodes
	 */

	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

	/*
	 * Abstracte methodes
	 */

	public abstract void takeControl();

	public abstract void releaseControl();

	public abstract Tile nextTile(Tile currentTile);

	public abstract boolean isBarcodeActionEnabled();

	public abstract IAction getAction(Barcode barcode);

}
