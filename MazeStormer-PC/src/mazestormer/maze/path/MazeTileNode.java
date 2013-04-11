package mazestormer.maze.path;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.path.Node;
import mazestormer.util.LongPoint;

public class MazeTileNode extends Node<Long> {

	private final IMaze maze;

	protected MazeTileNode(IMaze maze, LongPoint tilePosition) throws IllegalArgumentException {
		super(tilePosition);
		this.maze = maze;
	}

	public IMaze getMaze() {
		return maze;
	}

	public Tile getTile() {
		return getMaze().getTileAt(getPosition());
	}

	@Override
	public LongPoint getPosition() {
		return (LongPoint) super.getPosition();
	}

	@Override
	public MazeTileNode getPrevious() {
		return (MazeTileNode) super.getPrevious();
	}

	@Override
	public Long getF() {
		return getG() + getH();
	}

	@Override
	public void resetG() {
		setG(0l);
	}

	@Override
	public Long getG() {
		Long g = super.getG();
		return g == null ? 0l : g;
	}

	@Override
	public Long getH() {
		Long h = super.getH();
		return h == null ? 0l : h;
	}

	@Override
	public void calculateH(Node<Long> target) throws IllegalArgumentException {
		setH((long) manhattanDistance(getPosition(), target.getPosition()));
	}

	@Override
	public Collection<? extends Node<Long>> getNeighbors() {
		List<MazeTileNode> neighbors = new ArrayList<MazeTileNode>();
		// Create neighbors in direction of open sides
		for (Orientation direction : getTile().getOpenSides()) {
			// Get neighbor tile if it exists
			Tile neighborTile = getMaze().getNeighbor(getTile(), direction);
			if (neighborTile == null)
				continue;
			// Create a neighbor node
			MazeTileNode neighborNode = createNeighbor(neighborTile);
			neighbors.add(neighborNode);
		}
		return neighbors;
	}

	protected MazeTileNode createNeighbor(Tile neighborTile) {
		// Create the node
		MazeTileNode neighborNode = new MazeTileNode(getMaze(), neighborTile.getPosition());
		// Set the previous node
		neighborNode.setPrevious(this);
		// Calculate the G-score from the previous node to this node
		neighborNode.setG(neighborNode.calculateG());
		return neighborNode;
	}

	private long calculateG() {
		MazeTileNode previous = getPrevious();
		// Add one step to the previous node's cost
		long g = previous.getG().longValue() + 1;
		return g;
	}

	@Override
	public double getKey() {
		return getF().doubleValue();
	}

	@Override
	public int compareTo(Node<Long> node) {
		return getF().compareTo(node.getF());
	}

	public static double manhattanDistance(Point2D start, Point2D end) {
		return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY());
	}

}
