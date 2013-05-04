package mazestormer.maze.path;

import mazestormer.maze.IMaze;
import mazestormer.maze.Tile;

import com.google.common.base.Predicate;

public class FindCorridorAStar extends MazeAStar {

	public FindCorridorAStar(IMaze maze, Tile startTile, Predicate<Tile> tileValidator) throws IllegalArgumentException {
		super(maze, tileValidator);
		setStart(new MazeTileNode(getMaze(), startTile.getPosition()));
		setTarget(null);
	}

	@Override
	protected boolean canHaveAsTarget(MazeTileNode target) {
		// No fixed target
		return target == null;
	}

	@Override
	public boolean isTarget(MazeTileNode node) {
		// Must be valid
		if (!isValidNode(node))
			return false;
		// Must have at least two walls
		if (node.getTile().getClosedSides().size() < 2)
			return false;
		// Previous tile must have at least three openings
		MazeTileNode previous = node.getPrevious();
		if (previous == null)
			return false;
		return previous.getTile().getOpenSides().size() >= 3;
	}

}
