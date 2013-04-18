package mazestormer.explore;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public abstract class AbstractExploreControlMode extends ControlMode {

	private final LinkedList<Tile> queue = new LinkedList<Tile>();

	public AbstractExploreControlMode(Player player, Commander commander) {
		super(player, commander);
	}

	/*
	 * Methodes eigen voor deze controlMode
	 */
	
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
			return null;
		}

		// Sort queue
		Collections.sort(queue, new ClosestTileComparator(currentTile, getMaze()));

		// Returns the next unexplored tile.
		Tile nextTile = queue.pollFirst();
		while (nextTile.isExplored()) {
			if (queue.isEmpty())
				return null;
			nextTile = queue.pollFirst();
		}

		// Go to next tile
		return nextTile;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}
	
	/*
	 * Utilities
	 */
	
	public boolean hasUnexploredTiles() {
		while (!queue.isEmpty()) {
			if (!queue.peekFirst().isExplored())
				return true;
			else
				queue.pollFirst();
		}
		return false;
	}

	/**
	 * Add tiles to the queue if the edge in its direction is open and it is not
	 * explored yet.
	 */
	private void selectTiles(Tile tile) {
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
	 * Compares tiles based on their shortest path distance to a given reference
	 * tile.
	 */
	public static class ClosestTileComparator implements Comparator<Tile> {

		private final Tile referenceTile;
		private Maze maze;

		public ClosestTileComparator(Tile referenceTile, Maze maze) {
			this.referenceTile = referenceTile;
			this.maze = maze;
		}

		@Override
		public int compare(Tile left, Tile right) {
			int leftDistance = shortestPathLength(referenceTile, left);
			int rightDistance = shortestPathLength(referenceTile, right);
			return Integer.compare(leftDistance, rightDistance);
		}

		// TODO This won't work if there exist longer paths around a seesaw!!!
		public int shortestPathLength(Tile startTile, Tile endTile) {
			List<Tile> path = new PathFinder(maze).findTilePath(startTile, endTile);
			for (Tile tile : path) {
				if (tile.getIgnoreFlag()) {
					return Integer.MAX_VALUE;
				}
			}
			return path.size();
		}

	}

}