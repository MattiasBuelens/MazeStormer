package mazestormer.maze.path;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import mazestormer.maze.IMaze;
import mazestormer.maze.Tile;
import mazestormer.path.AStar;
import mazestormer.path.util.FibonacciQueue;
import mazestormer.util.LongPoint;

public class MazeAStar extends AStar<MazeTileNode, Long> {

	private final IMaze maze;
	private final Predicate<Tile> tileValidator;

	public MazeAStar(IMaze maze, LongPoint startPosition, LongPoint targetPosition, Predicate<Tile> tileValidator)
			throws IllegalArgumentException {
		this(maze, tileValidator);
		setStart(new MazeTileNode(maze, startPosition));
		setTarget(new MazeTileNode(maze, targetPosition));
	}

	public MazeAStar(IMaze maze, LongPoint startPosition, LongPoint targetPosition) throws IllegalArgumentException {
		this(maze, null);
		setStart(new MazeTileNode(maze, startPosition));
		setTarget(new MazeTileNode(maze, targetPosition));
	}

	protected MazeAStar(IMaze maze, Predicate<Tile> tileValidator) throws IllegalArgumentException {
		this.maze = checkNotNull(maze);
		setOpenSet(new FibonacciQueue<MazeTileNode>());

		if (tileValidator == null) {
			this.tileValidator = Predicates.alwaysTrue();
		} else {
			this.tileValidator = tileValidator;
		}
	}

	public final IMaze getMaze() {
		return maze;
	}

	public List<Tile> findPath() {
		MazeTileNode lastNode = run();
		if (isTarget(lastNode)) {
			// Reconstruct path
			List<MazeTileNode> nodePath = reconstructPath(lastNode);
			// Create list of tiles
			List<Tile> path = new LinkedList<Tile>();
			for (MazeTileNode node : nodePath) {
				path.add(node.getTile());
			}
			return path;
		} else {
			// No path found
			return null;
		}
	}

	@Override
	protected boolean canHaveAsTarget(MazeTileNode target) {
		return super.canHaveAsTarget(target) && target.getMaze() == getMaze();
	}

	@Override
	public boolean isTarget(MazeTileNode node) {
		return node != null && node.equals(getTarget());
	}

	@Override
	public boolean isValidNode(MazeTileNode node) {
		return tileValidator.apply(node.getTile());
	}

}
