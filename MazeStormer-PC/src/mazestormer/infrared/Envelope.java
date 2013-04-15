package mazestormer.infrared;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public interface Envelope {

	public Point2D[] getClosestPoints(Pose pose, Point2D target);

	public Point2D[] getDiscretization(Pose pose, int depth)
			throws IllegalArgumentException;

	public Point2D[] getCombination(Pose pose, Point2D target, int depth)
			throws IllegalArgumentException;
}
