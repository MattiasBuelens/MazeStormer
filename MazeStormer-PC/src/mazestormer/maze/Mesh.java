package mazestormer.maze;

import java.util.HashMap;
import java.util.Map;

import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

import static com.google.common.base.Preconditions.checkNotNull;

public class Mesh {

	public Mesh(Maze maze) {
		setMaze(maze);
	}

	private Maze maze;

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public Maze getMaze() {
		return this.maze;
	}

	private Map<Tile, Node> nodeMap = new HashMap<Tile, Node>();
	
	public Tile[] findTilePath(Tile startTile, Tile goalTile) {
		Path path = findNodePath(startTile, goalTile);
		if(path != null){
			Tile[] tiles = new Tile[path.size()];
			for(int i=0; i<path.size(); i++){
				Waypoint wp = path.get(i);
				tiles[i] = getMaze().getTileAt(new LongPoint((long) wp.x, (long) wp.y));
			}
			return tiles;
		}
		return new Tile[0];
	}

	private Path findNodePath(Tile startTile, Tile goalTile) {
		Node startNode = this.nodeMap.get(startTile);
		Node goalNode = this.nodeMap.get(goalTile);

		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}
	
	public void addTileAsNode(Tile tile){
		checkNotNull(tile);
		this.nodeMap.put(tile, new Node(tile.getX(), tile.getY()));
	}
	
	public void edgeUpdate(Edge edge){
		checkNotNull(edge);
		
		if (edge.getType() == EdgeType.OPEN || edge.getType() == EdgeType.WALL) {
			Object[] touchingLPs = edge.getTouching().toArray();
			Tile firstTile = getMaze().getTileAt((LongPoint) touchingLPs[0]);
			Tile secondTile = getMaze().getTileAt((LongPoint) touchingLPs[1]);
			
			if (edge.getType() == EdgeType.OPEN) {
				this.nodeMap.get(firstTile).addNeighbor(this.nodeMap.get(secondTile));
				this.nodeMap.get(secondTile).addNeighbor(this.nodeMap.get(firstTile));
			}	
			else if (edge.getType() == EdgeType.WALL) {
				this.nodeMap.get(firstTile).removeNeighbor(this.nodeMap.get(secondTile));
				this.nodeMap.get(secondTile).removeNeighbor(this.nodeMap.get(firstTile));
			}
		}
	}

	public void printNodeMap() {
		for (Tile tile : this.nodeMap.keySet()) {
			System.out.println("Tile X: " + tile.getX() + "\t" + " Y: "
					+ tile.getY() + "\t" + " | Borders: "
					+ tile.getClosedSides().size() + " | Openings: "
					+ tile.getOpenSides().size());
			Node node = this.nodeMap.get(tile);
			System.out.println("Node X: " + node.x + "\t" + " Y: " + node.y
					+ "\t" + " | Borders: " + (4 - node.getNeighbors().size())
					+ " | Openings: " + node.getNeighbors().size());
		}
	}
}
