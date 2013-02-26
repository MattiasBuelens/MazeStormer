package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IPathFindingController extends EventSource {
	
	public void startStepAction(long goalX, long goalY);
	
	public void startAction(long goalX, long goalY);
	
	public void startAction(long goalX, long goalY, boolean singleStep, boolean reposition);

	public void stopAction();
	
	public long getCurrentTileX();
	
	public long getCurrentTileY();
	
	public long getTileMinX();
	
	public long getTileMinY();
	
	public long getTileMaxX();
	
	public long getTileMaxY();
	
	public void addSourceMaze();
}
