package mazestormer.util;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class LongPoint extends Point2D implements Serializable {

	private static final long serialVersionUID = 1L;

	private long x;
	private long y;

	public LongPoint() {
	}

	public LongPoint(long x, long y) {
		setLocation(x, y);
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

	@Override
	public void setLocation(double x, double y) {
		setLocation((long) x, (long) y);
	}

	public void setLocation(long x, long y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "LongPoint[" + x + ", " + y + "]";
	}

}
