package mazestormer.maze;

import java.util.Comparator;
import java.util.List;


/**
 * Compares tiles based on their shortest path distance to a given reference
 * tile.
 */
public abstract class ClosestTileComparator implements Comparator<Tile> {

	private final Tile referenceTile;

	public ClosestTileComparator(Tile referenceTile) {
		this.referenceTile = referenceTile;
	}

	@Override
	public int compare(Tile left, Tile right) {
		int leftDistance = createPath(referenceTile, left).size();
		int rightDistance = createPath(referenceTile, right).size();
		return Integer.compare(leftDistance, rightDistance);
	}

	public abstract List<?> createPath(Tile startTile, Tile goalTile);

}