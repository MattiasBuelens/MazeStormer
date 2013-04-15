package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;
import mazestormer.util.ArrayUtils;

public class CircularEnvelope implements Envelope {

	private double radius;

	public CircularEnvelope(double radius) throws IllegalArgumentException {
		if (radius == 0) {
			throw new IllegalArgumentException(
					"The given radius and width may not be equal to zero.");
		}
		setRadius(radius);
	}

	public double getRadius() {
		return this.radius;
	}

	private void setRadius(double radius) {
		this.radius = Math.abs(radius);
	}

	@Override
	public Point2D[] getClosestPoints(Pose pose, Point2D target) {
		// TODO: dummy implementation
		return (new RectangularEnvelope(2 * getRadius(), 2 * getRadius()))
				.getClosestPoints(pose, target);
	}

	@Override
	public Point2D[] getDiscretization(Pose pose, int depth)
			throws IllegalArgumentException {
		if (depth <= 0) {
			throw new IllegalArgumentException(
					"The given depth must be greater than zero.");
		}

		Point2D[] tps = new Point2D.Double[depth];
		double d = (2*Math.PI / (double) depth);

		for (int i = 0; i < depth; i++) {
			tps[i] = getPointAtCircleParameter(pose, d);
		}
		return tps;
	}
	
	private Point2D getPointAtCircleParameter(Pose pose, double u) {
		float center_x = pose.getX();
		float center_y = pose.getY();
		double c_x = center_x + radius * Math.cos(u);
		double c_y = center_y + radius * Math.sin(u);
		return new Point2D.Double(c_x, c_y);
	}

	@Override
	public Point2D[] getCombination(Pose pose, Point2D target, int depth)
			throws IllegalArgumentException {
		Point2D[] dps = getDiscretization(pose, depth);
		Point2D[] cps = getClosestPoints(pose, target);
		return ArrayUtils.concat(dps, cps);
	}

}
