package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IScanController extends EventSource {

	public void scan(int scanRange, int angleIncrement);

	public float getMaxDistance();

	public void setMaxDistance(float distance);

}
