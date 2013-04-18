package mazestormer.infrared;

import com.vividsolutions.jts.geom.Polygon;

public interface Envelope {

	public double getDetectionRadius();

	public Polygon getPolygon();

}
