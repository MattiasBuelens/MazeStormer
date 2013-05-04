package mazestormer.command.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.action.AbstractSeesawAction;
import mazestormer.barcode.action.BarcodeMapping;
import mazestormer.barcode.action.IAction;
import mazestormer.barcode.action.NoAction;
import mazestormer.barcode.action.ObjectFoundAction;
import mazestormer.command.AbstractExploreControlMode;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;
import mazestormer.util.LongPoint;

import com.google.common.base.Predicate;

public class ExploreIslandControlMode extends AbstractExploreControlMode {

	/*
	 * Attributes
	 */

	private final BarcodeMapping exploreBarcodeMapping = new ExploreIslandBarcodeMapping();

	/*
	 * Constructor
	 */

	public ExploreIslandControlMode(Player player, GameRunner gameRunner) {
		super(player, gameRunner);
	}

	/*
	 * Getters
	 */

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	/*
	 * Driver support
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return exploreBarcodeMapping.getAction(barcode);
	}

	@Override
	public List<Tile> createPath(Tile startTile, Tile goalTile) {
		return getPathFinder().findTilePath(startTile, goalTile, new Predicate<Tile>() {
			@Override
			public boolean apply(Tile tile) {
				// Only allow internal seesaws in paths
				return !tile.isSeesaw() || isInternal(tile.getSeesaw());
			}
		});
	}

	// TODO Resolve duplication in AbstractSeesawAction
	private boolean isInternal(Seesaw seesaw) {
		Tile lowTile = getMaze().getBarcodeTile(seesaw.getLowestBarcode());
		Tile highTile = getMaze().getBarcodeTile(seesaw.getHighestBarcode());
		if (lowTile == null || highTile == null) {
			return false;
		}
		return !getPathFinder().findTilePathWithoutSeesaws(lowTile, highTile).isEmpty();
	}

	/*
	 * Object found
	 */
	public void setObjectTile() {
		Tile currentTile = getCommander().getDriver().getCurrentTile();
		Tile nextTile = getCommander().getDriver().getGoalTile();
		Orientation orientation = currentTile.orientationTo(nextTile);

		// Make next tile a dead end
		getMaze().setTileShape(nextTile.getPosition(), new TileShape(TileType.DEAD_END, orientation));

		// Mark as explored
		getMaze().setExplored(nextTile.getPosition());
	}

	public void objectFound(int teamNumber) {
		// Report object found
		getGameRunner().getGame().objectFound();
		// Join team
		getGameRunner().getGame().joinTeam(teamNumber);
		// TODO Start working together
	}

	private static int getObjectNumber(Barcode objectBarcode) {
		return objectBarcode.getValue() % 4;
	}

	private static int getTeamNumber(Barcode objectBarcode) {
		return objectBarcode.getValue() / 4;
	}

	private class ObjectAction extends ObjectFoundAction {

		private Barcode barcode;

		private ObjectAction(Barcode barcode) {
			this.barcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// Store in maze
			setObjectTile();

			// Check if own object
			if (getObjectNumber(barcode) == getGameRunner().getObjectNumber()) {
				// Found own object
				int teamNumber = getTeamNumber(barcode);
				log("Own object found, join team #" + teamNumber);
				objectFound(teamNumber);
				// Pick up own object
				Future<?> future = super.performAction(player);
				future.addFutureListener(new AfterObjectFoundListener());
			} else {
				// Not our object
				log("Not our object");
				// Skip dead end tile
				skipToNextTile(true);
			}
			return null;
		}
	}

	private class AfterObjectFoundListener implements FutureListener<Object> {
		@Override
		public void futureResolved(Future<? extends Object> future, Object result) {
			// Skip dead end tile
			skipToNextTile(true);
		}

		@Override
		public void futureCancelled(Future<? extends Object> future) {
		}
	}

	/*
	 * Seesaws
	 */

	public void setSeesawWalls() {
		IMaze maze = getMaze();

		// Get seesaw barcodes
		Tile currentTile = getCommander().getDriver().getCurrentTile();
		Barcode seesawBarcode = currentTile.getBarcode();
		Barcode otherBarcode = Seesaw.getOtherBarcode(seesawBarcode);

		// Exit if seesaw already placed
		if (getMaze().getSeesawTile(seesawBarcode) != null)
			return;

		// When still unexplored, the next tile is the unexplored seesaw tile
		Tile nextTile = getCommander().getDriver().getGoalTile();
		Orientation orientation = currentTile.orientationTo(nextTile);
		TileShape tileShape = new TileShape(TileType.STRAIGHT, orientation);

		log("Seesaw on next tiles, set seesaw and barcode");

		// Seesaw
		LongPoint nextTilePosition = nextTile.getPosition();
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, seesawBarcode);
		maze.setExplored(nextTilePosition);

		// Other seesaw
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

	private class SeesawAction extends AbstractSeesawAction {

		private final Barcode seesawBarcode;

		protected SeesawAction(Barcode seesawBarcode) {
			super(ExploreIslandControlMode.this.getPlayer(), ExploreIslandControlMode.this.getCommander().getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// Place seesaw
			setSeesawWalls();

			// Only cross if the seesaw is internal
			Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
			if (isInternal(seesaw)) {
				// Check if actually trying to cross
				if (meantToCrossSeesaw()) {
					// Check if seesaw is open
					if (canDriveOverSeesaw()) {
						// Drive over seesaw
						log("Drive over internal seesaw");
						seesaw.setOpen(seesawBarcode);
						return driveOverSeesaw();
					} else {
						// Drive around
						log("Go around internal seesaw");
						seesaw.setClosed(seesawBarcode);
						// Go around seesaw
						List<Tile> pathAroundSeesaw = getPathWithoutSeesaws();
						return redirect(pathAroundSeesaw);
					}
				} else {
					// Not trying to cross
					log("Not trying to cross seesaw, skip to next tile");
				}
			} else {
				// Non-internal seesaw
				log("Non-internal seesaw, skip to next tile");
			}

			// Skip seesaw tile
			skipToNextTile(true);
			return null;
		}

		private boolean meantToCrossSeesaw() {
			/*
			 * If the current (seesaw barcode) tile is equal to the start tile
			 * of the drivers new cycle, the driver was simply exploring it. It
			 * didn't have the intention to cross the seesaw.
			 */
			return !(getDriver().getCurrentTile().equals(getDriver().getStartTile()));
		}
	}

	private class ExploreIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(0), new ObjectAction(new Barcode(0)));
				put(new Barcode(1), new ObjectAction(new Barcode(1)));
				put(new Barcode(2), new ObjectAction(new Barcode(2)));
				put(new Barcode(3), new ObjectAction(new Barcode(3)));
				put(new Barcode(4), new ObjectAction(new Barcode(4)));
				put(new Barcode(5), new ObjectAction(new Barcode(5)));
				put(new Barcode(6), new ObjectAction(new Barcode(6)));
				put(new Barcode(7), new ObjectAction(new Barcode(7)));
				put(new Barcode(11), new SeesawAction(new Barcode(11)));
				put(new Barcode(13), new SeesawAction(new Barcode(13)));
				put(new Barcode(15), new SeesawAction(new Barcode(15)));
				put(new Barcode(17), new SeesawAction(new Barcode(17)));
				put(new Barcode(19), new SeesawAction(new Barcode(19)));
				put(new Barcode(21), new SeesawAction(new Barcode(21)));
			}
		};

		@Override
		public IAction getAction(Barcode barcode) {
			if (barcodeMapping.containsKey(barcode)) {
				return barcodeMapping.get(barcode);
			}
			return new NoAction();
		}

	}

}
