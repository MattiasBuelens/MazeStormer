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

	public TileSequenceRunner(Robot robot, Maze maze, Tile goal) {
		this.robot = robot;
		this.maze = maze;
		this.goal = goal;
		
		this.tiles = this.maze.getMesh(true).findTilePath(getStartTile(), this.goal);
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
