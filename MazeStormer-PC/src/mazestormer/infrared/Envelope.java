package mazestormer.infrared;

import com.vividsolutions.jts.geom.Geometry;

public interface Envelope {

	public double getDetectionRadius();

	public Geometry getGeometry();

}
