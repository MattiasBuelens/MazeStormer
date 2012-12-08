package mazestormer.controller;

import mazestormer.util.EventSource;

public interface ICheatController extends EventSource {
	
	public void teleportTo(long goalX, long goalY);

	public long getTileMinX();
	
	public long getTileMinY();
	
	public long getTileMaxX();
	
	public long getTileMaxY();
}
