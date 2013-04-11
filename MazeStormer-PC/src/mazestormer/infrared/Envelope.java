package mazestormer.infrared;

import java.awt.geom.Point2D;
import lejos.robotics.localization.PoseProvider;

public interface Envelope {

	public PoseProvider getPoseProvider();
	
	public Point2D[] getClosestPoints(Point2D target);
	
	public Point2D[] getDiscretization(int depth)
			throws IllegalArgumentException;
	
	public Point2D[] getCombination(Point2D target, int depth)
			throws IllegalArgumentException;
}
