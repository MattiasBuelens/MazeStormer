package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Predicate;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.action.AbstractSeesawAction;
import mazestormer.barcode.action.BarcodeMapping;
import mazestormer.barcode.action.IAction;
import mazestormer.barcode.action.NoAction;
import mazestormer.command.ControlMode;
import mazestormer.maze.IMaze;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.path.FindCorridorAStar;
import mazestormer.maze.path.MazeAStar;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class LeaveIslandControlMode extends ControlMode {

	// TODO: remove ID shizzle

	public static final int MINIMUM_TIMEOUT = 5;
	public static final int MAXIMUM_TIMEOUT = 15;
	
	public static int UID = 0;
	public int id;
	private List<Tile> visitedSeesawTiles = new ArrayList<>();
	private final LeaveIslandBarcodeMapping leaveBarcodeMapping = new LeaveIslandBarcodeMapping();

	private boolean droveOverSeesaw = false;
	private boolean isInCorridor = false;
	private boolean firstTile = true;
	
	/*
	 * Constructor
	 */

	public LeaveIslandControlMode(Player player, GameRunner gameRunner) {
		super(player, gameRunner);
		id = UID++;
		// make a list of inter island seesaws.
	}

	/*
	 * Getters
	 */

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	/*
	 * ControlMode Management
	 */

	@Override
	public void takeControl() {
		// TODO Auto-generated method stub
		log("Leaving island");

	}

	@Override
	public void releaseControl() {
		// TODO Auto-generated method stub

	}

	/*
	 * Driver support
	 */

	@Override
	public Tile nextTile(Tile currentTile) {

		Tile nextTile = null;

		if(droveOverSeesaw){
			droveOverSeesaw = false;
			return null;
		}
		
		boolean moreThanOneReachableSeesaws = reachableSeesawBarcodeTiles(
				currentTile).size() > 1;

		if (moreThanOneReachableSeesaws) {
			log("More than one reachable seesaw");
			
			if(firstTile && leaveBarcodeMapping.isSeesawBarcode(currentTile.getBarcode())){
				firstTile = false;
				return currentTile;
			}
			
			nextTile = getClosestSeesawBarcodeTile(currentTile,
					visitedSeesawTiles);
			if (nextTile == null) {
				log("Visited every seesaw tile, clearing list, starting over");
				visitedSeesawTiles.clear();
				nextTile = getClosestSeesawBarcodeTile(currentTile, null);
			}
			visitedSeesawTiles.add(nextTile);
		} else {
			log("One reachable seesaw");
			
			if(isInCorridor){
				isInCorridor = false;
				try {
					int timeout = getRandomTimeout() * 1000;
					log("Waiting " + timeout + " ms");
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			
			if (leaveBarcodeMapping.isSeesawBarcode(currentTile.getBarcode())) {
				log("Standing on seesaw barcode tile");
				if (visitedSeesawTiles.contains(currentTile)) {
					log("Already visited, evacuate!");
					visitedSeesawTiles.clear();
					
					nextTile = evacuate(currentTile);
					
					// TODO: EVACUATE!!!!!!
				} else {
					log("Didn't visit this tile yet, visiting current tile now");
					visitedSeesawTiles.add(currentTile);
					firstTile = false;
					return currentTile;
				}
			} else {
				log("Not standing on seesaw barcode tile");
				firstTile = false;
				return getClosestSeesawBarcodeTile(currentTile, null);
			}
		}
		log("Returning tile: " + nextTile);
		firstTile = false;
		return nextTile;

		// if(leaveBarcodeMapping.isSeesawBarcode(currentTile.getBarcode())){
		// return currentTile;
		// }
		//
		// log("LeaveIslandControlModeID: " + id);
		// // TODO: niet af!!
		// log("Requesting next tile from LeaveIslandCM");
		//
		// Tile nextTile =
		// getClosestSeesawBarcodeTile(currentTile,visitedSeesawTiles);
		//
		// log("1");
		//
		// // if(nextTile == null){
		// // log("2");
		// // visitedSeesawTiles.clear();
		// // nextTile = getClosestSeesawBarcodeTile(currentTile,null);
		// // }
		// log("3");
		// //visitedSeesawTiles.add(nextTile);
		// log("4");
		// System.out.println(nextTile);
		// if(nextTile == null){
		// nextTile = currentTile;
		// }
		// log("5");

	}

	private Tile evacuate(Tile currentTile) {
		isInCorridor = true;
		log("Lookin' for corridor");
		Tile facingTile = getCommander().getDriver().getFacingTile();
		List<Tile> pathToCorridor = findPathToCorridor(currentTile, facingTile);
		log("Found corridor, returning tile!");
		return pathToCorridor.get(pathToCorridor.size()-1);
	}

	private List<Tile> findPathToCorridor(final Tile startTile, final Tile blockedTile) {
		// Get path of tiles
		MazeAStar astar = new FindCorridorAStar(getMaze(), startTile, new Predicate<Tile>() {
			@Override
			public boolean apply(Tile tile) {
				// Ignore blocked tile
				if (tile.getPosition().equals(blockedTile.getPosition()))
					return false;
				// Ignore seesaws
				if (tile.isSeesaw())
					return false;
				return true;
			}
		});
		List<Tile> tilePath = astar.findPath();
		// Skip starting tile
		if (tilePath == null || tilePath.size() <= 1)
			return new ArrayList<Tile>();
		else
			return tilePath.subList(1, tilePath.size());
	}

	private int getRandomTimeout() {
		return MINIMUM_TIMEOUT + new Random().nextInt(MAXIMUM_TIMEOUT - MINIMUM_TIMEOUT + 1);
	}
	
	@Override
	public IAction getAction(Barcode barcode) {
		return leaveBarcodeMapping.getAction(barcode);
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	/**
	 * @return null if there is no reachable seesaw barcode tile
	 */
	private Tile getClosestSeesawBarcodeTile(Tile currentTile, List<Tile> ignore) {
		Collection<Tile> reachableSeesawBarcodeTiles = reachableSeesawBarcodeTiles(currentTile);
		Tile shortestTile = null;
		int shortestPathLength = Integer.MAX_VALUE;
		for (Tile tile : reachableSeesawBarcodeTiles) {
			if (ignore == null || !ignore.contains(tile)) {
				List<Tile> path = getPathFinder().findTilePathWithoutSeesaws(
						currentTile, tile);
				if (path.size() < shortestPathLength && path.size() > 0)
					shortestTile = tile;
			}
		}
		return shortestTile;
	}

	/**
	 * @return A collection with barcode tiles belonging to a seesaw, to which
	 *         you can go without crossing the seesaw you're currently standing
	 *         at.
	 */
	private Collection<Tile> reachableSeesawBarcodeTiles(Tile currentTile) {

		Collection<Tile> tiles = new HashSet<>();
		IMaze maze = getMaze();

		Collection<Tile> seesawBarcodeTiles = getSeesawBarcodeTiles(maze);

		for (Tile tile : seesawBarcodeTiles) {
			List<Tile> path = getPathFinder().findTilePathWithoutSeesaws(
					currentTile, tile);
			if (!path.isEmpty()) {
				log("Adding bc tile value: " + tile.getBarcode().getValue());
				tiles.add(tile);
			}
		}

		return tiles;
	}

	private Collection<Tile> getSeesawBarcodeTiles(IMaze maze) {
		Collection<Tile> barcodeTiles = maze.getBarcodeTiles();
		Collection<Tile> seesawBarcodeTiles = new HashSet<Tile>();

		Barcode currentBarcode;
		for (Tile currentTile : barcodeTiles) {
			currentBarcode = currentTile.getBarcode();

			if (leaveBarcodeMapping.isSeesawBarcode(currentBarcode)) {
				seesawBarcodeTiles.add(currentTile);
			}
		}

		return seesawBarcodeTiles;
	}

	private Barcode[] getCurrentSeesawBarcodes(Tile currentTile) {
		if (!currentTile.hasBarcode())
			return null;
		Barcode barcode = currentTile.getBarcode();

		if (!leaveBarcodeMapping.isSeesawBarcode(barcode))
			return null;
		Barcode otherBarcode = leaveBarcodeMapping
				.getOtherSeesawBarcode(barcode);
		return new Barcode[] { barcode, otherBarcode };
	}

	/*
	 * Utilities
	 */

	private class SeesawAction extends AbstractSeesawAction {

		private Barcode seesawBarcode;

		private SeesawAction(Barcode seesawBarcode) {
			super(LeaveIslandControlMode.this.getPlayer(),
					LeaveIslandControlMode.this.getCommander().getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// Cross the seesaw if open
			if (canDriveOverSeesaw()) {
				droveOverSeesaw = true;
				return driveOverSeesaw(getGameRunner().getGame());
			}

			return new NoAction().performAction(player);
		}
	}

	private class LeaveIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
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

		public boolean isSeesawBarcode(Barcode barcode) {
			return Seesaw.isSeesawBarcode(barcode);
		}

		public Barcode getOtherSeesawBarcode(Barcode barcode) {
			return Seesaw.getOtherBarcode(barcode);
		}

	}
}
