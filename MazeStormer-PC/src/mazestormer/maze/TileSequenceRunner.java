package mazestormer.maze;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.robot.Robot;

public class TileSequenceRunner implements Runnable {
	
	private Robot robot;
	private Maze maze;
	private Tile goal;
	private Tile[] tiles;
	private int i = 1;
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
		this.goal = goal;
		this.tiles = tiles;
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
		if (this.tiles != null) {
			while (this.i<this.tiles.length) {
				runStep();
			}
		}
	}
	
	public void runStep() {
		if (this.tiles != null && this.i<this.tiles.length) {
			turnToTile(this.tiles[i-1], this.tiles[i]);
			move();
			i++;
		}
	}
	
	// TODO: current orientation of the robot
	private void turnToTile(Tile from, Tile to) {
		double angle = 0;
		if (from.getX()+1 == to.getX())
			angle = 0;
		else if (from.getX()-1 == to.getX())
			angle = 0;
		else if (from.getY()+1 == to.getY())
			angle = 0;
		else if (from.getY()-1 == to.getY())
			angle = 0;
		this.robot.getPilot().rotate(angle, false);
	}
	
	private void move() {
		this.robot.getPilot().travel(this.maze.getTileSize(), false);
	}

}
