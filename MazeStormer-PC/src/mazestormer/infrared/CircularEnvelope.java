package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public class CircularEnvelope implements Envelope {

	private double internalRadius;
	private double externalRadius;

	public CircularEnvelope(double internalRadius, double externalRadius) throws IllegalArgumentException {
		if (internalRadius == 0 || externalRadius == 0) {
			throw new IllegalArgumentException(
					"The given radius may not be equal to zero.");
		}
		setInternalRadius(internalRadius);
		setExternalRadius(externalRadius);
	}

	@Override
	public double getInternalRadius() {
		return this.internalRadius;
	}
	
	@Override
	public double getExternalRadius() {
		return this.externalRadius;
	}

	private void setInternalRadius(double internalRadius) {
		this.internalRadius = Math.abs(internalRadius);
	}
	
	private void setExternalRadius(double radius) {
		this.externalRadius = Math.abs(radius);
	}
	
	@Override
	public Point2D[] getInternalDiscretization(Pose pose, int depth)
			throws IllegalArgumentException {
		return getDiscretization(pose, depth, getInternalRadius());
	}

	@Override
	public Point2D[] getExternalDiscretization(Pose pose, int depth)
			throws IllegalArgumentException {
		return getDiscretization(pose, depth, getExternalRadius());
	}
	
	private Point2D[] getDiscretization(Pose pose, int depth, double radius)
			throws IllegalArgumentException {
		if (depth <= 0) {
			throw new IllegalArgumentException(
					"The given depth must be greater than zero.");
		}

		Point2D[] tps = new Point2D.Double[depth];
		double d = (2*Math.PI / (double) depth);

		for (int i = 0; i < depth; i++) {
			tps[i] = getPointAtCircleParameter(pose, d, radius);
		}
		return tps;
	}
	
	private Point2D getPointAtCircleParameter(Pose pose, double u, double radius) {
		float center_x = pose.getX();
		float center_y = pose.getY();
		double c_x = center_x + radius * Math.cos(u);
		double c_y = center_y + radius * Math.sin(u);
		return new Point2D.Double(c_x, c_y);
	}

}
