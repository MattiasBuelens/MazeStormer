package mazestormer.maze;

import java.util.HashMap;
import java.util.Map;

import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

public class Mesh {

	public static void main(String args[]) throws Exception {
		Mesh mesh = new Mesh(new Maze());
		mesh.setUpTest();
		mesh.generateNodes();
		mesh.findPath();
	}

	@Deprecated
	public void setUpTest() throws Exception {
		String mazeFilePath = Mesh.class.getResource(
				"/res/mazes/Semester1_Demo2.txt").getPath();
		CharSequence contents;
		contents = FileUtils.load(mazeFilePath);
		this.maze.clear();
		new Parser(this.maze).parse(contents);
	}

	@Deprecated
	public void findPath() {
		Tile startTile = (Tile) this.maze.getTiles().toArray()[11];
		Tile goalTile = (Tile) this.maze.getTiles().toArray()[12];

		System.out.println("Start Tile\tX: " + startTile.getX() + "\t" + " Y: "
				+ startTile.getY() + "\t" + " | Borders: "
				+ startTile.getClosedSides().size() + " | Openings: "
				+ startTile.getOpenSides().size());
		System.out.println("Goal Tile\tX: " + goalTile.getX() + "\t" + " Y: "
				+ goalTile.getY() + "\t" + " | Borders: "
				+ goalTile.getClosedSides().size() + " | Openings: "
				+ goalTile.getOpenSides().size());
		Tile[] t = findTilePath(startTile, goalTile);
		for(int i=0; i<t.length; i++){
			System.out.println(t[i].getX() + "|" + t[i].getY());;
		}
	}

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
		Tile[] tiles = new Tile[path.size()];
		for(int i=0; i<path.size(); i++){
			Waypoint wp = path.get(i);
			tiles[i] = getMaze().getTileAt(new LongPoint((long) wp.x, (long) wp.y));
		}
		return tiles;
	}

	private Path findNodePath(Tile startTile, Tile goalTile) {
		Node startNode = nodeMap.get(startTile);
		Node goalNode = nodeMap.get(goalTile);

		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}

	public void generateNodes() {
		// Create nodes
		Map<Tile, Node> nodes = new HashMap<Tile, Node>();
		for (Tile tile : getMaze().getTiles()) {
			nodes.put(tile, new Node(tile.getX(), tile.getY()));
		}
		// Link nodes
		for (Tile tile : getMaze().getTiles()) {
			Node node = nodes.get(tile);
			for (Orientation openSide : tile.getOpenSides()) {
				Tile nb = getMaze().getNeighbor(tile, openSide);
				if (nb != null) {
					node.addNeighbor(nodes.get(nb));
				}
			}
		}
		this.nodeMap = nodes;
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
