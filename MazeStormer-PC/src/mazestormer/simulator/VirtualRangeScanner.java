package mazestormer.simulator;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.detect.ObservableRangeScanner;
import mazestormer.maze.IMaze;
import mazestormer.robot.RangeScannerListener;
import mazestormer.util.Future;
import mazestormer.util.GeometryUtils;
import mazestormer.util.ImmediateFuture;
import mazestormer.world.World;

public class VirtualRangeScanner implements ObservableRangeScanner {

	private final World world;
	private static final float maxDistance = 255f;

	private float[] angles;

	private final List<RangeScannerListener> listeners = new ArrayList<RangeScannerListener>();

	public VirtualRangeScanner(World world) {
		this.world = world;
	}

	private World getWorld() {
		return world;
	}

	private PoseProvider getPoseProvider() {
		return getWorld().getLocalPlayer().getRobot().getPoseProvider();
	}

	private IMaze getMaze() {
		return getWorld().getMaze();
	}

	@Override
	public void setAngles(float[] angles) {
		this.angles = angles;
	}

	@Override
	public RangeFinder getRangeFinder() {
		return null;
	}

	@Override
	public RangeReadings getRangeValues() {
		RangeReadings r = new RangeReadings(angles.length);
		for (int i = 0; i < angles.length; i++) {
			// Get and store reading
			RangeReading reading = new RangeReading(angles[i], generateRange(angles[i]));
			r.set(i, reading);
			// Trigger listeners
			fireReadingReceived(reading);
		}
		return r;
	}

	@Override
	public Future<RangeReadings> getRangeValuesAsync() {
		return new ImmediateFuture<RangeReadings>(getRangeValues());
	}

	@Override
	public void addListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	private void fireReadingReceived(RangeReading reading) {
		for (RangeScannerListener listener : listeners) {
			listener.readingReceived(reading);
		}
	}

	private float generateRange(float angle) {
		// Get relative robot pose
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());

		// Create ray from robot to maximum reachable point
		Point p1 = pose.getLocation();
		Point p2 = p1.pointAt(getMaxDistance() - 1f, pose.getHeading() + angle);
		Line ray = new Line(p1.x, p1.y, p2.x, p2.y);

		// Get edge geometry
		Geometry mazeGeom = getMaze().getEdgeGeometry();
		GeometryFactory geomFact = mazeGeom.getFactory();

		// Get intersection between ray and maze edges
		Geometry rayGeom = GeometryUtils.toGeometry(ray, geomFact);
		Geometry intersection = mazeGeom.intersection(rayGeom);

		if (intersection.isEmpty()) {
			// No intersection
			return -1f;
		} else {
			// Get distance to robot
			Geometry posGeom = GeometryUtils.toGeometry(p1, geomFact);
			return (float) posGeom.distance(intersection);
		}
	}

	public static final float getMaxDistance() {
		return maxDistance;
	}

}
