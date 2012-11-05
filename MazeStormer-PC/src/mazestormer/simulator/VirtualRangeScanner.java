package mazestormer.simulator;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;

public class VirtualRangeScanner implements RangeScanner {

	public VirtualRangeScanner(Maze maze, PoseProvider poseProvider) {
		this.maze = maze;
		this.poseProvider = poseProvider;
	}

	private PoseProvider getPoseProvider() {
		return this.poseProvider;
	}

	private PoseProvider poseProvider;

	private Maze getMaze() {
		return this.maze;
	}

	private Maze maze;

	@Override
	public RangeReadings getRangeValues() {
		RangeReadings r = new RangeReadings(angles.length);
		for (float angle : angles) {
			r.add(new RangeReading(angle, generateRange(angle)));
		}
		return r;
	}

	private float generateRange(float angle) {
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());

		double heading = Math.toRadians(pose.getHeading() + angle);

		Line l = new Line(pose.getX(), pose.getY(), pose.getX() + 254f
				* (float) Math.cos(heading), pose.getY() + 254f
				* (float) Math.sin(heading));
		Line rl = null;

		for (Line line : getMaze().getLines().values()) {
			Point p = line.intersectsAt(l);
			if (p == null)
				continue; // Does not intersect
			Line tl = new Line(pose.getX(), pose.getY(), p.x, p.y);
			// If the range line intersects more than one map line
			// then take the shortest distance.
			if (rl == null || tl.length() < rl.length())
				rl = tl;
		}
		return (rl == null ? -1 : rl.length());
	}

	@Override
	public void setAngles(float[] angles) {
		this.angles = angles;
	}

	private float[] angles;

	@Override
	public RangeFinder getRangeFinder() {
		return null;
	}
}
