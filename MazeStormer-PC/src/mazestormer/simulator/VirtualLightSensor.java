package mazestormer.simulator;

import lejos.robotics.LampLightDetector;
import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.Maze;

public class VirtualLightSensor implements LampLightDetector{
	
	public VirtualLightSensor(Maze maze, PoseProvider poseProvider){
		this.maze = maze;
		this.poseProvider = poseProvider;
	}
	
	private PoseProvider getPoseProvider(){
		return this.poseProvider;
	}
	
	private PoseProvider poseProvider;
	
	private Maze getMaze(){
		return this.maze;
	}
	
	private Maze maze;

	@Override
	public int getLightValue(){
		
		
		return 0;
	}

	@Override
	public int getNormalizedLightValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHigh(){
		return 0;
	}

	@Override
	public int getLow(){
		return 0;
	}

	@Override
	public void setFloodlight(boolean floodlight){
		
	}

	@Override
	public boolean isFloodlightOn(){
		return true;
	}

	@Override
	public int getFloodlight(){
		return 0;
	}

	@Override
	public boolean setFloodlight(int color){
		return false;
	}

}
