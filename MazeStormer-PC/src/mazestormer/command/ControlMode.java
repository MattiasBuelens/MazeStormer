package mazestormer.command;

import java.util.List;
import java.util.logging.Level;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.action.IAction;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public abstract class ControlMode {

	private final Player player;
	private final Commander commander;

	public ControlMode(Player player, Commander commander) {
		this.player = player;
		this.commander = commander;
	}

	/*
	 * Getters
	 */

	protected final Player getPlayer() {
		return player;
	}

	protected final IMaze getMaze() {
		return (IMaze) getPlayer().getMaze();
	}

	protected final PathFinder getPathFinder() {
		return getCommander().getDriver().getPathFinder();
	}

	public final Commander getCommander() {
		return commander;
	}

	/*
	 * Overridable
	 */

	protected void log(String message) {
		getPlayer().getLogger().log(Level.INFO, message);
	}

	protected List<Tile> createPath(Tile startTile, Tile goalTile) {
		return getPathFinder().findTilePath(startTile, goalTile);
	}

	/*
	 * Abstract
	 */

	public abstract void takeControl();

	public abstract void releaseControl();

	public abstract Tile nextTile(Tile currentTile);

	public abstract boolean isBarcodeActionEnabled();

	public abstract IAction getAction(Barcode barcode);
	
	/*
	 * Utilities
	 */
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
