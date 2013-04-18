package mazestormer.infrared;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class RectangularEnvelope implements Envelope {

	private final double detectionRadius;
	private final Polygon rectangle;

	public RectangularEnvelope(double width, double height, double detectionRadius) throws IllegalArgumentException {
		if (detectionRadius <= 0) {
			throw new IllegalArgumentException("The given radius must be positive.");
		}
		this.detectionRadius = detectionRadius;

		// Create rectangle
		GeometricShapeFactory factory = new GeometricShapeFactory();
		factory.setNumPoints(4);
		factory.setCentre(new Coordinate(0, 0));
		factory.setWidth(width);
		factory.setHeight(height);
		rectangle = factory.createRectangle();
	}

	@Override
	public double getDetectionRadius() {
		return detectionRadius;
	}

	@Override
	public Polygon getPolygon() {
		return rectangle;
	}

}
