package mazestormer.maze;

import java.util.HashMap;
import java.util.Map;

import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;

public class Mesh{
	
	public static void main(String args[]) throws Exception{
		Mesh mesh = new Mesh(new Maze());
		mesh.setUpTest();
		mesh.generateNodes();
		mesh.printNodeMap();
	}
	
	public void setUpTest() throws Exception{
		String mazeFilePath = "C:/Users/Matthias/git/MazeStormer/MazeStormer-PC/src/res/mazes/Semester1_Demo2.txt";
		CharSequence contents;
		contents = FileUtils.load(mazeFilePath);
		this.maze.clear();
		new Parser(this.maze).parse(contents);
	}
	
	private Mesh(Maze maze){
		setMaze(maze);
	}
	
	private Maze maze;
	
	public void setMaze(Maze maze){
		this.maze = maze;
	}
	
	public Maze getMaze(){
		return this.maze;
	}
	
	private Map<Tile, Node> nodeMap = new HashMap<Tile, Node>();

	public Path findPath(Tile startTile, Tile goalTile){
		Node startNode = nodeMap.get(startTile);
		Node goalNode = nodeMap.get(goalTile);
		
		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}
	
	private void generateNodes(){
		Map<Tile, Node> nodes = new HashMap<Tile, Node>();
		for(Tile tile : getMaze().getTiles())
			nodes.put(tile, new Node(tile.getX(), tile.getY()));
		for(Tile tile : getMaze().getTiles()){
			Node node = nodes.get(tile);
			for(int i=0; i<Orientation.values().length; i++)
				if(tile.hasEdgeAt(Orientation.values()[i])){
					Tile nb = getMaze().getNeighbor(tile, Orientation.values()[i]);
					if(nb != null)
						node.addNeighbor(nodes.get(nb));
				}
		}
		this.nodeMap = nodes;
	}
	
	public void printNodeMap(){
		for(Tile tile : this.nodeMap.keySet()){
			System.out.println("Tile X: " + tile.getX() + "\t" + " Y: " + tile.getY() + "\t" + " | Borders: " + tile.getEdges().size() + " | Openings: " + tile.getOpenSides().size());
			Node node = this.nodeMap.get(tile);
				System.out.println("Node X: " + node.x + "\t" + " Y: " + node.y + "\t" + " | Borders: " + node.getNeighbors().size() + " | Openings: " + (4-node.getNeighbors().size()));
		}
	}
}
