package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public interface Envelope {
	
	public double getInternalRadius();
	
	public double getExternalRadius();

	public Point2D[] getInternalDiscretization(Pose pose, int depth)
			throws IllegalArgumentException;
	
	public Point2D[] getExternalDiscretization(Pose pose, int depth)
			throws IllegalArgumentException;
}
