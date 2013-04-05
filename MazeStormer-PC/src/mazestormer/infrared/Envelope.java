package mazestormer.infrared;

import java.awt.geom.Point2D;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class Envelope {
	
	private final PoseProvider poseProvider;
	private double length;
	private double width;
	
	public Envelope(PoseProvider poseProvider, double radius)
			throws NullPointerException, IllegalArgumentException {
		this(poseProvider, 2*radius, 2*radius);
	}
	
	public Envelope(PoseProvider poseProvider, double length, double width)
			throws NullPointerException, IllegalArgumentException {
		if (poseProvider == null) {
			throw new NullPointerException("The given pose provider may not refer the null reference.");
		}
		if (length == 0 || width == 0) {
			throw new IllegalArgumentException("The given length and width may not be equal to zero.");
		}
		
		this.poseProvider = poseProvider;
		setLength(length);
		setWidth(width);
	}
	
	public PoseProvider getPoseProvider() {
		return this.poseProvider;
	}
	
	public double getLength() {
		return this.length;
	}
	
	public void setLength(double length) {
		this.length = Math.abs(length);
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public void setWidth(double width) {
		this.width = Math.abs(width);
	}
	
	public Point2D.Double[] getClosestPoints(Point2D.Double target) {
		Point2D.Double[] tps = new Point2D.Double[4];
		Point2D.Double[] cps = getCornerPoints();
		
		for (int i=0; i<tps.length; i++) {
			tps[i] = getClosestPointOnSegment(cps[i], cps[(i+1)%4], target);
		}
		return tps;
	}
	
	private Point2D.Double getClosestPointOnSegment(Point2D.Double startOfSegment, Point2D.Double endOfSegment, Point2D.Double target) {
		double u = getSegmentParameter(startOfSegment, endOfSegment, target);
		if (u < 0) {
			return startOfSegment;
		}
		if (u > 1) {
			return endOfSegment;
		}
		double c_x = startOfSegment.getX() + u*(endOfSegment.getX()-startOfSegment.getX());
		double c_y = startOfSegment.getY() + u*(endOfSegment.getY()-startOfSegment.getY());
		return new Point2D.Double(c_x, c_y);
	}
	
	private double getSegmentParameter(Point2D.Double startOfSegment, Point2D.Double endOfSegment, Point2D.Double target) {
		double tx = (target.getX()-startOfSegment.getX())*(endOfSegment.getX()-startOfSegment.getX());
		double ty = (target.getY()-startOfSegment.getY())*(endOfSegment.getY()-startOfSegment.getY());
		double squared2Norm = startOfSegment.distanceSq(endOfSegment);
		return (tx+ty)/(squared2Norm);
	}
	
	private Point2D.Double[] getCornerPoints() {
		Pose pose = getPoseProvider().getPose();
		float center_x = pose.getX();
		float center_y = pose.getY();
		float angle = pose.getHeading();
		
		Point2D.Double[] cps = new Point2D.Double[4];
		// cps[i] is a neighbour of cps[i+1] and cps[i-1] with the index % 4 (e.g.: i % 4,(i+1) % 4,(i-1) % 4)
		cps[0] = rotatePoint(new Point2D.Double((center_x+getLength()/2), (center_y+getWidth()/2)), angle);
		cps[1] = rotatePoint(new Point2D.Double((center_x-getLength()/2), (center_y+getWidth()/2)), angle);
		cps[2] = rotatePoint(new Point2D.Double((center_x-getLength()/2), (center_y-getWidth()/2)), angle);
		cps[3] = rotatePoint(new Point2D.Double((center_x+getLength()/2), (center_y-getWidth()/2)), angle);
		return cps;
	}
	
	private static Point2D.Double rotatePoint(Point2D.Double point, float angle) {
		double x = point.getX();
		double y = point.getY();
		double new_x = x*Math.cos(angle) - y*Math.sin(angle);
		double new_y = x*Math.sin(angle) + y*Math.cos(angle);
		return new Point2D.Double(new_x, new_y);
	}
}
