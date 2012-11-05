package mazestormer.simulator;

import lejos.robotics.LampLightDetector;

public class VirtualLightSensor implements LampLightDetector{

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
