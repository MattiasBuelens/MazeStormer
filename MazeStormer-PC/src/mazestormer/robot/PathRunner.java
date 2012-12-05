package mazestormer.robot;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.robot.Robot;

public abstract class PathRunner extends Runner implements NavigationListener {

	protected final Robot robot;
	protected final Maze maze;
	protected final Navigator navigator;

	public PathRunner(Robot robot, Maze maze) {
		super(robot.getPilot());
		this.robot = robot;
		this.maze = maze;
		this.navigator = new Navigator(robot.getPilot(),
				robot.getPoseProvider());
		this.navigator.addNavigationListener(this);
	}

	protected Pose getPose() {
		return robot.getPoseProvider().getPose();
	}

	public Tile getCurrentTile() {
		// Get absolute robot pose
		Pose pose = robot.getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = maze.toRelative(pose.getLocation());
		Point tilePosition = maze.toTile(relativePosition);
		return maze.getTileAt(tilePosition);
	}

	public List<Waypoint> findPath(Tile goal) {
		// Get path of tiles
		Tile[] tiles = maze.getMesh().findTilePath(getCurrentTile(), goal);
		// Get path of way points
		// Note: loop starts at *second* tile
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		for (int i = 1, len = tiles.length; i < len; i++) {
			waypoints.add(toWaypoint(tiles[i]));
		}
		return waypoints;
	}

	public Waypoint toWaypoint(Tile tile) {
		// Get center of tile
		Point tilePosition = tile.getPosition().toPoint()
				.add(new Point(0.5f, 0.5f));
		// Get absolute position
		Point absolutePosition = maze.toAbsolute(maze.fromTile(tilePosition));
		// Create way point
		return new Waypoint(absolutePosition.getX(), absolutePosition.getY());
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		navigator.stop();
	}

	@Override
	public void atWaypoint(Waypoint waypoint, Pose pose, int sequence) {
	}

	@Override
	public void pathComplete(Waypoint waypoint, Pose pose, int sequence) {
	}

	@Override
	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
		// Navigation interrupted, cancel runner
		cancel();
	}

}
