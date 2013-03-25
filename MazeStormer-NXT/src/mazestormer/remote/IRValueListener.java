package mazestormer.remote;

public interface IRValueListener {
	
	public void irValueChanged(int normalizedIRValue);
	
	public int getSensorID();
}
