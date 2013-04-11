package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.localization.PoseProvider;

public class CircularEnvelope implements Envelope {
	
	private final PoseProvider poseProvider;
	private double radius;
	
	public CircularEnvelope(PoseProvider poseProvider, double radius)
			throws NullPointerException, IllegalArgumentException {
		if (poseProvider == null) {
			throw new NullPointerException("The given pose provider may not refer the null reference.");
		}
		if (radius == 0) {
			throw new IllegalArgumentException("The given radius and width may not be equal to zero.");
		}
		
		this.poseProvider = poseProvider;
		setRadius(radius);
	}

	@Override
	public PoseProvider getPoseProvider() {
		return this.poseProvider;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public void setRadius(double radius) {
		this.radius = Math.abs(radius);
	}

	@Override
	public Point2D[] getClosestPoints(Point2D target) {
		// TODO: dummy implementation
		return (new RectangularEnvelope(getPoseProvider(), 2*getRadius(), 2*getRadius())).getClosestPoints(target);
	}

}
