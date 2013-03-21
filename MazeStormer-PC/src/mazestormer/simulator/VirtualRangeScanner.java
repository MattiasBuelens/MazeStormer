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

	private static final float maxDistance = 255f;

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

	public static final float getMaxDistance() {
		return maxDistance;
	}

	@Override
	public RangeReadings getRangeValues() {
		RangeReadings r = new RangeReadings(angles.length);
		for (int i = 0; i < angles.length; i++) {
			r.set(i, new RangeReading(angles[i], generateRange(angles[i])));
		}
		return r;
	}

	private float generateRange(float angle) {
		// Get relative robot pose
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());

		// Create ray from robot to maximum reachable point
		Point p1 = pose.getLocation();
		Point p2 = p1.pointAt(getMaxDistance() - 1f, pose.getHeading() + angle);
		Line ray = new Line(p1.x, p1.y, p2.x, p2.y);

		// Initialize with infinite best distance
		float bestDistance = Float.POSITIVE_INFINITY;

		for (Line line : getMaze().getEdgeLines()) {
			// Point p = line.intersectsAt(ray); deze methode suckt monkeyballs
			Point p = intersectionOf(ray, line);
			if (p != null) {
				// If intersecting, get distance
				float distance = (float) p.distance(p1);
				// If the range line intersects more than one map line,
				// take the shortest distance
				if (Float.isInfinite(bestDistance) || distance < bestDistance)
					bestDistance = distance;
			}
		}
		return (Float.isInfinite(bestDistance) ? -1 : bestDistance);
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

	/*
	 * Replaces LeJOS Line.intersectsAt(Line)
	 * 
	 * Source: http://stackoverflow.com/questions/563198
	 */
	private static Point intersectionOf(Line l1, Line l2) {
		Point p = l1.getP1();
		Point r = l1.getP2().subtract(p);
		Point q = l2.getP1();
		Point s = l2.getP2().subtract(q);

		float divisor = crossProduct(r, s);
		if (divisor == 0)
			return null;
		Point qminp = q.subtract(p);

		float t = crossProduct(qminp, s) / divisor;
		float u = crossProduct(qminp, r) / divisor;

		if (t >= 0 && t <= 1 && u >= 0 && u <= 1)
			return p.add(r.multiply(t));

		// No intersection
		return null;
	}

	private static float crossProduct(Point p1, Point p2) {
		return (float) (p1.getX() * p2.getY() - p1.getY() * p2.getX());
	}

}
