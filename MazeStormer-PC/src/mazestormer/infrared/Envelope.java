package mazestormer.infrared;

import java.awt.geom.Point2D;
import lejos.robotics.localization.PoseProvider;

public interface Envelope {

	public PoseProvider getPoseProvider();
	
	public Point2D[] getClosestPoints(Point2D target);
}
