package mazestormer.infrared;

import mazestormer.world.Model;

public interface IRSource extends Model {
	
	public boolean isEmitting();
	
	public Envelope getEnvelope();
}
