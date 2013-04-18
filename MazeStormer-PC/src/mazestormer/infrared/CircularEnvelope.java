package mazestormer.infrared;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class CircularEnvelope implements Envelope {

	private final double detectionRadius;
	private final Polygon circle;

	/**
	 * Approximate circle with hexagon.
	 */
	private static final int numPoints = 6;

	public CircularEnvelope(double radius, double detectionRadius) throws IllegalArgumentException {
		if (detectionRadius <= 0) {
			throw new IllegalArgumentException("The given radius must be positive.");
		}
		this.detectionRadius = detectionRadius;

		// Create circle
		GeometricShapeFactory factory = new GeometricShapeFactory();
		factory.setNumPoints(numPoints);
		factory.setCentre(new Coordinate(0, 0));
		factory.setSize(radius);
		circle = factory.createCircle();
	}

	@Override
	public double getDetectionRadius() {
		return detectionRadius;
	}

	@Override
	public Polygon getPolygon() {
		return circle;
	}

}
