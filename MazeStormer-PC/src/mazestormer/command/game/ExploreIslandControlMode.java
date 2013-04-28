package mazestormer.command.game;

import java.util.HashMap;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.DriveOverSeesawAction;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.command.AbstractExploreControlMode;
import mazestormer.maze.IMaze;
import mazestormer.maze.Island;
import mazestormer.maze.Orientation;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.util.Future;
import mazestormer.util.LongPoint;

public class ExploreIslandControlMode extends AbstractExploreControlMode {

	/*
	 * Atributes
	 */

	private final BarcodeMapping exploreBarcodeMapping = new ExploreIslandBarcodeMapping();
	private Island island;

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

	public Island getIsland() {
		return island;
	}

	/*
	 * Driver support
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return exploreBarcodeMapping.getAction(barcode);
	}

	@Override
	public void takeControl() {
		// Check where we are
		Tile tile = getCommander().getDriver().getCurrentTile();
		Barcode seesawBarcode = null;
		if (tile.hasBarcode()) {
			seesawBarcode = tile.getBarcode();
		} else if (tile.isSeesaw()) {
			seesawBarcode = tile.getSeesawBarcode();
		}
		Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
		if (seesawBarcode == null || seesaw == null) {
			// No barcode or not a seesaw barcode
			// Assume continuing from previous island
			if (island == null) {
				island = new Island();
			}
		} else {
			// Valid seesaw barcode
			island = seesaw.getIsland(seesawBarcode);
			if (island == null) {
				// Previously unknown island reached
				island = new Island();
				seesaw.setIsland(seesawBarcode, island);
			}
		}
	}

	/*
	 * Object found
	 */

	public void setObjectTile() {
		Tile currentTile = getCommander().getDriver().getCurrentTile();
		Tile nextTile = getCommander().getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);

		// Make next tile a dead end
		getMaze().setTileShape(nextTile.getPosition(), new TileShape(TileType.DEAD_END, orientation));

		// Mark as explored
		getMaze().setExplored(nextTile.getPosition());
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

			// TODO: verwijder volgende tegels uit queue? Worden ze ooit
			// toegevoegd?

			// Check if own object
			if (getObjectNumberFromBarcode(barcode) == getGameRunner().getObjectNumber()) {
				// Found own object
				objectFound(getTeamNumberFromBarcode(barcode));
				// Pick up own object
				return super.performAction(player);
			} else {
				// Not our object
				// TODO Check what comes after this?
				return null;
			}
		}

		private int getObjectNumberFromBarcode(Barcode objectBarcode) {
			return (objectBarcode.getValue() % 4);
		}

		private int getTeamNumberFromBarcode(Barcode objectBarcode) {
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}

	}

	/*
	 * Seesaws
	 */

	public void setSeesawWalls() {
		log("Seesaw on next tiles, set seesaw and barcode");

		IMaze maze = getMaze();

		Tile currentTile = getCommander().getDriver().getCurrentTile();
		Tile nextTile = getCommander().getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);
		TileShape tileShape = new TileShape(TileType.STRAIGHT, orientation);

		Barcode seesawBarcode = currentTile.getBarcode();
		Barcode otherBarcode = Seesaw.getOtherBarcode(seesawBarcode);

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
	}

	private class SeesawAction implements IAction {

		private final Barcode seesawBarcode;

		private SeesawAction(Barcode barcode) {
			this.seesawBarcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// TODO Check whether we're trying to cross the seesaw

			// Place seesaw
			setSeesawWalls();
			// Set island on this side of the seesaw
			getMaze().setSeesawIsland(seesawBarcode, getIsland());
			// Only cross if the seesaw is internal
			if (getMaze().getSeesaw(seesawBarcode).isInternal()) {
				// Cross the seesaw
				return new DriveOverSeesawAction().performAction(player);
			} else {
				// TODO Check what comes after this?
				return null;
			}
		}

	}

	private class ExploreIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeTypeMapping = new HashMap<Barcode, IAction>() {
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
			if (barcodeTypeMapping.containsKey(barcode)) {
				return barcodeTypeMapping.get(barcode);
			}
			return new NoAction();
		}

	}

	public void objectFound(int teamNumber) {
		log("Own object found, join team #" + teamNumber);
		// Report object found
		getGameRunner().getGame().objectFound();
		// Join team
		getGameRunner().getGame().joinTeam(teamNumber);
		// TODO Start working together
	}

}
