package mazestormer.infrared;

import java.awt.geom.Point2D;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.util.ArrayUtils;

public class RectangularEnvelope implements Envelope{
	
	private final PoseProvider poseProvider;
	private double height;
	private double width;
	
	public RectangularEnvelope(PoseProvider poseProvider, double height, double width)
			throws NullPointerException, IllegalArgumentException {
		if (poseProvider == null) {
			throw new NullPointerException("The given pose provider may not refer the null reference.");
		}
		if (height == 0 || width == 0) {
			throw new IllegalArgumentException("The given length and width may not be equal to zero.");
		}
		
		this.poseProvider = poseProvider;
		setHeight(height);
		setWidth(width);
	}
	
	@Override
	public PoseProvider getPoseProvider() {
		return this.poseProvider;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public void setHeight(double height) {
		this.height = Math.abs(height);
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public void setWidth(double width) {
		this.width = Math.abs(width);
	}
	
	@Override
	public Point2D[] getClosestPoints(Point2D target) {
		Point2D[] tps = new Point2D.Double[4];
		Point2D[] cps = getCornerPoints();
		
		for (int i=0; i<tps.length; i++) {
			tps[i] = getClosestPointOnSegment(cps[i], cps[(i+1)%4], target);
		}
		return tps;
	}
	
	private Point2D getClosestPointOnSegment(Point2D startOfSegment, Point2D endOfSegment, Point2D target) {
		double u = getSegmentParameter(startOfSegment, endOfSegment, target);
		return getPointAtSegmentParameter(startOfSegment, endOfSegment, u);
	}
	
	private double getSegmentParameter(Point2D startOfSegment, Point2D endOfSegment, Point2D target) {
		double tx = (target.getX()-startOfSegment.getX())*(endOfSegment.getX()-startOfSegment.getX());
		double ty = (target.getY()-startOfSegment.getY())*(endOfSegment.getY()-startOfSegment.getY());
		double squared2Norm = startOfSegment.distanceSq(endOfSegment);
		return (tx+ty)/(squared2Norm);
	}
	
	private Point2D[] getCornerPoints() {
		Pose pose = getPoseProvider().getPose();
		float center_x = pose.getX();
		float center_y = pose.getY();
		float angle = pose.getHeading();
		
		Point2D[] cps = new Point2D.Double[4];
		// cps[i] is a neighbour of cps[i+1] and cps[i-1] with the index % 4 (e.g.: i % 4,(i+1) % 4,(i-1) % 4)
		cps[0] = rotatePoint(new Point2D.Double((center_x+getHeight()/2), (center_y+getWidth()/2)), angle);
		cps[1] = rotatePoint(new Point2D.Double((center_x-getHeight()/2), (center_y+getWidth()/2)), angle);
		cps[2] = rotatePoint(new Point2D.Double((center_x-getHeight()/2), (center_y-getWidth()/2)), angle);
		cps[3] = rotatePoint(new Point2D.Double((center_x+getHeight()/2), (center_y-getWidth()/2)), angle);
		return cps;
	}
	
	private static Point2D rotatePoint(Point2D point, float angle) {
		double x = point.getX();
		double y = point.getY();
		double new_x = x*Math.cos(angle) - y*Math.sin(angle);
		double new_y = x*Math.sin(angle) + y*Math.cos(angle);
		return new Point2D.Double(new_x, new_y);
	}
	
	@Override
	public Point2D[] getDiscretization(int depth)
			throws IllegalArgumentException{
		if (depth <= 0) {
			throw new IllegalArgumentException("The given depth must be greater than zero.");
		}
		
		Point2D[] tps = new Point2D.Double[4*depth];
		Point2D[] cps = getCornerPoints();
		double d = (1/ (double) depth);
		
		for (int j=0; j<4; j++) {
			for (int i=0; i<depth; i++) {
				tps[(int) (j*d+i)] = getPointAtSegmentParameter(cps[j], cps[(j+1)%4], d*i);
			}
		}
		return tps;
	}
	
	private Point2D getPointAtSegmentParameter(Point2D startOfSegment, Point2D endOfSegment, double segmentParameter) {
		if (segmentParameter < 0) {
			return startOfSegment;
		}
		if (segmentParameter > 1) {
			return endOfSegment;
		}
		double c_x = startOfSegment.getX() + segmentParameter*(endOfSegment.getX()-startOfSegment.getX());
		double c_y = startOfSegment.getY() + segmentParameter*(endOfSegment.getY()-startOfSegment.getY());
		return new Point2D.Double(c_x, c_y);
	}

	@Override
	public Point2D[] getCombination(Point2D target, int depth)
			throws IllegalArgumentException {
		Point2D[] dps = getDiscretization(depth);
		Point2D[] cps = getClosestPoints(target);
		return ArrayUtils.concat(dps, cps);
	}
}
