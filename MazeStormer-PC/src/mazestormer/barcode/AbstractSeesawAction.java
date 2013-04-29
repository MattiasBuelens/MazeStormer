package mazestormer.barcode;

import java.util.List;

import mazestormer.command.Driver;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllablePCRobot;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public abstract class AbstractSeesawAction implements IAction {

	private final Player player;
	private final Driver driver;
	private final PathFinder pathFinder;

	protected AbstractSeesawAction(Player player, Driver driver) {
		this.player = player;
		this.driver = driver;
		this.pathFinder = new PathFinder(getMaze());
	}

	protected final Player getPlayer() {
		return player;
	}

	protected final IMaze getMaze() {
		return getPlayer().getMaze();
	}

	protected final ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	protected final Pilot getPilot() {
		return getRobot().getPilot();
	}

	protected final Driver getDriver() {
		return driver;
	}

	protected final PathFinder getPathFinder() {
		return pathFinder;
	}

	/*
	 * Checks
	 */

	protected boolean canDriveOverSeesaw() {
		boolean seesawOpen = isOpen(getRobot().getIRSensor().getAngle());
		if (seesawOpen) {
			return isOpen(getRobot().getIRSensor().getAngle());
		} else {
			return false;
		}
	}

	/*
	 * End-point actions
	 */

	/**
	 * Drive over the seesaw.
	 */
	protected final Future<?> driveOverSeesaw() {
		Future<?> future = new DriveOverSeesawAction().performAction(getPlayer());
		future.addFutureListener(new FutureListener<Object>() {
			@Override
			public void futureResolved(Future<? extends Object> future, Object result) {
				// TODO Follow updated path
			}

			@Override
			public void futureCancelled(Future<? extends Object> future) {
			}
		});
		return null;
	}

	protected final List<Tile> getPathWithoutSeesaws() {
		Tile startTile = getDriver().getCurrentTile();
		Tile goalTile = getDriver().getNextTile();
		return getPathFinder().findTilePathWithoutSeesaws(startTile, goalTile);
	}

	protected final List<Tile> getPathWithoutSeesaw(Seesaw seesaw) {
		Tile startTile = getDriver().getCurrentTile();
		Tile goalTile = getDriver().getNextTile();
		return getPathFinder().findTilePathWithoutSeesaw(startTile, goalTile, seesaw);
	}

	/**
	 * Redirect the driver to follow the given path.
	 * 
	 * @param tilePath
	 *            The new tile path.
	 */
	protected final Future<?> redirect(List<Tile> tilePath) {
		// Redirect and skip current barcode
		getDriver().followPath(tilePath, true);
		return null;
	}

	private static boolean isOpen(float angle) {
		if (Float.isNaN(angle)) {
			return true;
		}
		return (Math.abs(angle) > ControllablePCRobot.STANDARD_IR_RANGE);
	}

	protected boolean isInternal(Seesaw seesaw) {
		Tile lowTile = getMaze().getBarcodeTile(seesaw.getLowestBarcode());
		Tile highTile = getMaze().getBarcodeTile(seesaw.getHighestBarcode());
		if (lowTile == null || highTile == null) {
			return false;
		}
		return !getPathFinder().findTilePathWithoutSeesaws(lowTile, highTile).isEmpty();
	}

}
