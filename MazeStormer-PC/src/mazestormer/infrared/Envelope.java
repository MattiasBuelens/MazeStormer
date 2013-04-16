package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public interface Envelope {
	
	public double getRadius();

	public Point2D[] getDiscretization(Pose pose, int depth)
			throws IllegalArgumentException;
}
