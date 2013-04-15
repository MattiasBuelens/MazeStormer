package mazestormer.explore;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class ExploreControlMode extends ControlMode {

	private final LinkedList<Tile> queue = new LinkedList<Tile>();

	public ExploreControlMode(Player player, BarcodeMapping mapping) {
		super(player, mapping);
	}

	@Override
	public void takeControl(Driver driver) {
		log("Exploring the maze");
	}

	@Override
	public void releaseControl(Driver driver) {
	}

	@Override
	public Tile nextTile(Tile currentTile) {
		// Create new paths to all neighbors
		selectTiles(currentTile);

		// Queue depleted
		if (queue.isEmpty()) {
			if (getBarcodeMapping() instanceof TeamTreasureTrekBarcodeMapping) {
				return getClosestSeesawBarcodeTile(currentTile);
			} else return null;
		}

		// Sort queue
		Collections.sort(queue, new ClosestTileComparator(currentTile));

		// Go to next tile
		Tile nextTile = queue.pollFirst();
		while (nextTile.isExplored())
			nextTile = queue.pollFirst();

		return nextTile;
	}

	/**
	 * @return null if there is no reachable seesaw barcode tile
	 */
	private Tile getClosestSeesawBarcodeTile(Tile currentTile) {
		Collection<Tile> reachableSeesawBarcodeTiles = reachableSeesawBarcodeTiles(currentTile);
		Tile shortestTile = null;
		int shortestPathLength = Integer.MAX_VALUE;
		for (Tile tile : reachableSeesawBarcodeTiles) {
			List<Tile> path = getPathFinder().findTilePathWithoutSeesaws(
					currentTile, tile);
			if (path.size() < shortestPathLength)
				shortestTile = tile;
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
		Barcode[] currentSeesaw = getCurrentSeesawBarcodes(currentTile);
		IMaze maze = getMaze();
		Collection<Tile> seesawBarcodeTiles = maze.getSeesawBarcodeTiles();

		for (Tile tile : seesawBarcodeTiles) {
			if (tile.getSeesawBarcode().equals(currentSeesaw[0])
					|| tile.getSeesawBarcode().equals(currentSeesaw[1])) {
				continue;
			} else {
				List<Tile> path = getPathFinder().findTilePathWithoutSeesaws(
						currentTile, tile);
				if (!path.isEmpty())
					tiles.add(tile);
			}
		}

		return tiles;
	}

	private Barcode[] getCurrentSeesawBarcodes(Tile currentTile) {
		if (!currentTile.hasBarcode())
			return null;
		Barcode barcode = currentTile.getBarcode();
		if (!TeamTreasureTrekBarcodeMapping.isSeesawBarcode(barcode))
			return null;
		Barcode otherBarcode = TeamTreasureTrekBarcodeMapping
				.getOtherSeesawBarcode(barcode);
		return new Barcode[] { barcode, otherBarcode };
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	/**
	 * Add tiles to the queue if the edge in its direction is open and it is not
	 * explored yet.
	 */
	protected void selectTiles(Tile tile) {
		for (Orientation direction : tile.getOpenSides()) {
			Tile neighborTile = getMaze().getOrCreateNeighbor(tile, direction);
			// Reject the new paths with loops
			if (!neighborTile.isExplored() && !queue.contains(neighborTile)) {
				// Add the new paths to front of queue
				queue.addFirst(neighborTile);
			}
		}
	}

	/**
	 * Compares tiles based on their Manhattan distance to a given reference
	 * tile.
	 */
	private class ClosestTileComparator implements Comparator<Tile> {

		private final Tile referenceTile;

		public ClosestTileComparator(Tile referenceTile) {
			this.referenceTile = referenceTile;
		}

		@Override
		public int compare(Tile left, Tile right) {
			int leftDistance = shortestPathLength(referenceTile, left);
			int rightDistance = shortestPathLength(referenceTile, right);
			return Integer.compare(leftDistance, rightDistance);
		}

		public int shortestPathLength(Tile startTile, Tile endTile) {
			List<Tile> path = getPathFinder().findTilePath(startTile, endTile);
			/*
			 * for (Tile tile : path) { if (tile.getIgnoreFlag()) { return
			 * Integer.MAX_VALUE; } }
			 */
			return path.size();
		}

	}

}
