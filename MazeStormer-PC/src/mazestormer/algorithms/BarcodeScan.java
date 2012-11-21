package mazestormer.algorithms;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Pose;
import mazestormer.controller.Threshold;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class BarcodeScan {

	private static final double TRAVEL_SPEED = 10; 			// [cm/sec]
	private static final double SLOW_TRAVEL_SPEED = 2; 		// [cm/sec]
	
	private static final double START_BAR_LENGTH = 1.8; 	// [cm]
	private static final double BAR_LENGTH = 1.85; 			// [cm]
	private static final int NUMBER_OF_BARS = 6;			// without black start bars
	
	private static final int BLACK_THRESHOLD = 50;
	
	public static byte ScanBarcode(Robot robot){
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(TRAVEL_SPEED);
		light.setFloodlight(true);
		
		pilot.forward();
		
		byte result = 0;
		List<Float> distances = new ArrayList<Float>();
		while(true){
			if(light.getLightValue() < BLACK_THRESHOLD){
				pilot.stop();
				pilot.setTravelSpeed(SLOW_TRAVEL_SPEED);
				pilot.travel(-START_BAR_LENGTH/2, false);
				int oldValue = light.getLightValue();
				Pose oldPose = robot.getPoseProvider().getPose();
				boolean blackToWhite = true;
				pilot.forward();
				while(getTotalSum(distances) <= (NUMBER_OF_BARS+1)*BAR_LENGTH){
					int newValue = light.getLightValue();
					Pose newPose =  robot.getPoseProvider().getPose();
					if(isTresholdPassed(oldValue, newValue, blackToWhite)){
						distances.add(getPoseDiff(oldPose, newPose));
						oldValue = newValue;
						oldPose = newPose;
						blackToWhite = (blackToWhite == true) ? false : true;
					}
				}				
				result = convertToByte(convertToBitArray(distances));
				break;
			}
		}
		pilot.stop(); // TODO
		return result;
	}
	
	private static byte convertToByte(int[] request){
		int temp = 0;
		for(int i=request.length-1; i>0; i--)
			temp = (temp + request[i])*2;
		temp = temp + request[0];
		return ((Integer) temp).byteValue();
	}
	
	private static boolean isTresholdPassed(int one, int two, boolean blackToWhite){
		if(blackToWhite)
			return (one<Threshold.BLACK_WHITE.getThresholdValue() && two>Threshold.BLACK_WHITE.getThresholdValue());
		return (one>Threshold.WHITE_BLACK.getThresholdValue() && two<Threshold.WHITE_BLACK.getThresholdValue());
		
	}
	
	private static float getPoseDiff(Pose one, Pose two){
		float diffX = Math.abs(one.getX()-two.getX());
		float diffY = Math.abs(one.getY()-two.getY());
		if(diffX > diffY)
			return diffX;
		return diffY;
	}
	
	private static float getTotalSum(List<Float> request){
		float temp = 0;
		for(int i=0; i<request.size(); i++)
			temp = temp + request.get(i);
		return temp;
	}
	
	private static int[] convertToBitArray(List<Float> request){
		int[] values = new int[NUMBER_OF_BARS];
		int index = NUMBER_OF_BARS-1;
		for(int i=0; index>=0 && i<request.size(); i++){
			float d = request.get(i);
			int x = (i==0) ? 1 : 0;
			int a = ((Double)(Math.max(((d-START_BAR_LENGTH*x)/BAR_LENGTH),1-x))).intValue();
			for(int j=0; j<a; j++){
				if(index>=0){
					values[index] = Math.abs(i%2);
					index--;
				}
			}
		}
		return values;
	}
}
