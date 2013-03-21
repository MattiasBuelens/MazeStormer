package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.Node;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Mesh implements MazeListener {

	private final Maze maze;

	public Mesh(Maze maze) {
		this.maze = maze;
		maze.addListener(this);
	}

	public Maze getMaze() {
		return this.maze;
	}

	private Map<Tile, Node> nodeMap = new HashMap<Tile, Node>();

	private List<Waypoint> findNodePath(Tile startTile, Tile goalTile) {
		Node startNode = this.nodeMap.get(startTile);
		Node goalNode = this.nodeMap.get(goalTile);

		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}

	public List<Tile> findTilePath(Tile startTile, Tile goalTile) {
		// Find path of way points
		List<Waypoint> path = findNodePath(startTile, goalTile);
		// Create list of tiles
		int nbWaypoints = path == null ? 0 : path.size();
		List<Tile> tiles = new ArrayList<Tile>(nbWaypoints);
		for (Waypoint wp : path) {
			Tile tile = getMaze().getTileAt(new LongPoint(wp));
			tiles.add(tile);
		}
		return tiles;
	}

	public void addTileAsNode(Tile tile) {
		checkNotNull(tile);
		this.nodeMap.put(tile, new Node(tile.getX(), tile.getY()));
	}

	public void edgeUpdate(Edge edge) {
		checkNotNull(edge);

		if (edge.getType() == EdgeType.OPEN || edge.getType() == EdgeType.WALL) {
			LongPoint[] touching = edge.getTouching().toArray(new LongPoint[0]);
			Node first = this.nodeMap.get(getMaze().getTileAt(touching[0]));
			Node second = this.nodeMap.get(getMaze().getTileAt(touching[1]));

			if (edge.getType() == EdgeType.OPEN) {
				first.addNeighbor(second);
				second.addNeighbor(first);
			} else if (edge.getType() == EdgeType.WALL) {
				first.removeNeighbor(second);
				second.removeNeighbor(first);
			}
		}
	}

	@Deprecated
	public void printNodeMap() {
		for (Tile tile : this.nodeMap.keySet()) {
			System.out.println("Tile X: " + tile.getX() + "\t" + " Y: " + tile.getY() + "\t" + " | Borders: " + tile.getClosedSides().size()
					+ " | Openings: " + tile.getOpenSides().size());
			Node node = this.nodeMap.get(tile);
			System.out.println("Node X: " + node.x + "\t" + " Y: " + node.y + "\t" + " | Borders: " + (4 - node.getNeighbors().size())
					+ " | Openings: " + node.getNeighbors().size());
		}
	}

	@Override
	public void tileAdded(Tile tile) {
		addTileAsNode(tile);
	}

	@Override
	public void tileChanged(Tile tile) {
	}

	@Override
	public void edgeChanged(Edge edge) {
		edgeUpdate(edge);
	}

	@Override
	public void mazeOriginChanged(Pose origin) {
	}

	@Override
	public void mazeCleared() {
		this.nodeMap.clear();
	}

}
