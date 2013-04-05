package mazestormer.infrared;

public interface IRSource extends Model {
	
	public boolean isEmitting();
	
	public Envelope getEnvelope();
}
