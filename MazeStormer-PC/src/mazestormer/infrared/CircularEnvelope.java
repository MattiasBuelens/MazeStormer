package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

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
		// TODO: dummy implementation
		return (new RectangularEnvelope(2 * getRadius(), 2 * getRadius()))
				.getDiscretization(pose, depth);
	}

	@Override
	public Point2D[] getCombination(Pose pose, Point2D target, int depth)
			throws IllegalArgumentException {
		// TODO: dummy implementation
		return (new RectangularEnvelope(2 * getRadius(), 2 * getRadius()))
				.getCombination(pose, target, depth);
	}

}
