package mazestormer.maze;

import lejos.geom.Point;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.robot.Robot;

public class TileSequenceRunner implements Runnable {
	
	private Robot robot;
	private Maze maze;
	private Tile goal;
	private Tile[] tiles;
	private Navigator navigator;
	private boolean isRunning = false;

	/**
	 * Create a new tile sequence runner with given robot, maze and goal tile.
	 * 
	 * @param 	robot
	 * 			The robot who must follow a tile sequence.
	 * @param 	maze
	 * 			The maze the robot is positioned in.
	 * @param 	tiles
	 * 			The tile sequence the robot must follow.
	 */
	public TileSequenceRunner(Robot robot, Maze maze, Tile goal) {
		this.robot = robot;
		this.maze = maze;
		this.goal = goal;
		this.tiles = this.maze.getMesh(true).findTilePath(getStartTile(), this.goal);
		initializeNavigator();
	}
	
	/**
	 * Create a new tile sequence runner with given robot, maze and tile sequence.
	 * 
	 * @pre		If the tile sequence doesn't refer the null reference, the robot's
	 * 			current tile must be the first tile in the sequence.
	 * @pre		If the tile sequence doesn't refer the null reference, every two
	 * 			consecutive tiles must be located next to each other in a four way grid
	 * 			structure. (North, East, South, West)
	 * @param 	robot
	 * 			The robot who must follow a tile sequence.
	 * @param 	maze
	 * 			The maze the robot is positioned in.
	 * @param 	tiles
	 * 			The tile sequence the robot must follow.
	 */
	public TileSequenceRunner(Robot robot, Maze maze, Tile[] tiles) {
		this.robot = robot;
		this.maze = maze;
		this.tiles = tiles;
		this.goal = this.tiles[this.tiles.length-1];
		initializeNavigator();
	}
	
	private void initializeNavigator(){
		this.navigator = new Navigator(this.robot.getPilot());
		this.navigator.setPoseProvider(this.robot.getPoseProvider());
		addWayPoints();
	}
	
	public void start() {
		this.isRunning = true;
		new Thread(this).start();
	}

	public void stop() {
		if (isRunning()) {
			this.isRunning = false;
			this.robot.getPilot().stop();
		}
	}

	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	private Tile getStartTile() {
		// Get absolute robot pose
		Pose pose = this.robot.getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = this.maze.toRelative(pose.getLocation());
		Point tilePosition = this.maze.toTile(relativePosition);
		return this.maze.getTileAt(tilePosition);
	}
	
	@Override
	public void run() {
		this.navigator.followPath();
	}
	
	private void addWayPoints(){
		if(this.tiles != null)
			for(int i=0; i<this.tiles.length; i++){
				Point tilePosition = this.tiles[i].getPosition().toPoint();
				Point absolutePosition = this.maze.toAbsolute(tilePosition);
				Waypoint w = new Waypoint((absolutePosition.x+0.5)*this.maze.getTileSize(),
						(absolutePosition.y+0.5)*this.maze.getTileSize());
				this.navigator.addWaypoint(w);
			}
	}

}
