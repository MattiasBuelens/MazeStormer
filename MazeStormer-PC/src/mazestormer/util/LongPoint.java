package mazestormer.util;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.math.RoundingMode;

import lejos.geom.Point;

import com.google.common.math.DoubleMath;

public class LongPoint extends Point2D implements Serializable {

	private static final long serialVersionUID = 1L;

	private long x;
	private long y;

	public LongPoint() {
	}

	public LongPoint(long x, long y) {
		setLocation(x, y);
	}

	public LongPoint(Point2D point, RoundingMode mode) {
		setLocation(point, mode);
	}

	public LongPoint(Point2D point) {
		setLocation(point);
	}

	@Override
	public double getX() {
		return (double) x;
	}

	@Override
	public double getY() {
		return (double) y;
	}

	public void setLocation(long x, long y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setLocation(double x, double y) {
		setLocation(x, y, RoundingMode.FLOOR);
	}

	public void setLocation(double x, double y, RoundingMode mode) {
		long rx = DoubleMath.roundToLong(x, mode);
		long ry = DoubleMath.roundToLong(y, mode);
		setLocation(rx, ry);
	}

	@Override
	public void setLocation(Point2D point) {
		setLocation(point.getX(), point.getY());
	}

	public void setLocation(Point2D point, RoundingMode mode) {
		setLocation(point.getX(), point.getY(), mode);
	}

	public Point toPoint() {
		return new Point((float) getX(), (float) getY());
	}

	@Override
	public String toString() {
		return "LongPoint[" + x + ", " + y + "]";
	}
	
	public static double getSquared2Norm(LongPoint p1, LongPoint p2) {
		return (Math.pow((p1.getX()-p2.getX()),2) + Math.pow((p1.getY()-p2.getY()),2));
	}
	
	public static double get2Norm(LongPoint p1, LongPoint p2) {
		return Math.sqrt(getSquared2Norm(p1, p2));
	}
}
