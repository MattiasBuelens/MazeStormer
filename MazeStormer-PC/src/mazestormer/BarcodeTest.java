package mazestormer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;


public class BarcodeTest {
	
	private static final double TRAVEL_SPEED = 10; // [cm/sec]
	private static final double SLOW_TRAVEL_SPEED = 2; // [cm/sec]
	private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int NUMBER_OF_BARS = 6;
	
	private static final int BLACK_WHITE_THRESHOLD = 50;
			
	public static void main(String[] args) throws IOException, InterruptedException{
		Connector connector = new ConnectionProvider().getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);
		
		Robot robot = connector.getRobot();
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(TRAVEL_SPEED);
		light.setFloodlight(true);
		light.setLow(354);
		light.setHigh(576);
		
		pilot.forward();
		
		byte result = 0;
		List<Float> distances = new ArrayList<Float>();
		while(true){
			int oldValue = light.getLightValue();
			Pose oldPose = robot.getPoseProvider().getPose();
			if(oldValue < BLACK_WHITE_THRESHOLD){
				pilot.stop();
				pilot.setTravelSpeed(SLOW_TRAVEL_SPEED);
				pilot.travel(-BAR_LENGTH/2, false);
				oldValue = light.getLightValue();
				oldPose = robot.getPoseProvider().getPose();
				pilot.forward();	
				while(getTotalSum(distances) <= (NUMBER_OF_BARS+1)*BAR_LENGTH){
					int newValue = light.getLightValue();
					Pose newPose =  robot.getPoseProvider().getPose();
					if(areOnDifferentSideOfTreshold(oldValue, newValue)){
						distances.add(getPoseDiff(oldPose, newPose));
						oldValue = newValue;
						oldPose = newPose;
					}
				}				
				result = convertToByte(convertToIntArray(distances));
				break;
			}
		}
		
		System.out.println(result);
		pilot.stop();
		connector.disconnect();
	}
	

	public static void mainn(String[] args) throws IOException, InterruptedException{
		Connector connector = new ConnectionProvider().getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);
		
		Robot robot = connector.getRobot();
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(TRAVEL_SPEED);
		light.setFloodlight(true);
		light.setLow(363);
		light.setHigh(585);
		
		pilot.forward();
		byte result = 0;		
		
		while(true){
			int value = light.getLightValue();
			if(value < BLACK_WHITE_THRESHOLD){
				pilot.stop();
				pilot.setTravelSpeed(SLOW_TRAVEL_SPEED);
				pilot.travel(BAR_LENGTH/2);
				int[] values = new int[NUMBER_OF_BARS];
				for(int i=NUMBER_OF_BARS-1; i>=0; i--){
					pilot.travel(BAR_LENGTH/2);
					values[i] = (light.getLightValue() < BLACK_WHITE_THRESHOLD) ? 0 : 1;				
					pilot.travel(BAR_LENGTH/2);
				}
				result = convertToByte(values);
				break;
			}
		}
		
		System.out.println(result);
		pilot.stop();
		connector.disconnect();
	}
	
	private static byte convertToByte(int[] request){
		int temp = 0;
		for(int i=request.length-1; i>0; i--){
			temp = (temp + request[i])*2;
		}
		temp = temp + request[0];
		return ((Integer) temp).byteValue();
	}
	
	private static boolean areOnDifferentSideOfTreshold(int one, int two){
		return (one<BLACK_WHITE_THRESHOLD && two>BLACK_WHITE_THRESHOLD) || (one>BLACK_WHITE_THRESHOLD && two<BLACK_WHITE_THRESHOLD);
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
		for(int i=0; i<request.size(); i++){
			temp = temp + request.get(i);
		}
		
		return temp;
	}
	
	private static int[] convertToIntArray(List<Float> request){
		int[] values = new int[NUMBER_OF_BARS];
		int index = NUMBER_OF_BARS-1;
		boolean finished = false;
		for(int i=0; !finished && i<request.size(); i++){
			float d = request.get(i);
			
			int x = (i==0) ? 1 : 0;
			int a = ((Double)(Math.max(((d-1.8*x)/BAR_LENGTH),1-x))).intValue();
			
			for(int j=0; j<a; j++){
				if(index<0)
					finished = true;
				else{
					values[index] = Math.abs(i%2);
					index--;
				}
			}
			

		}
		for(int i=NUMBER_OF_BARS-1; i>=0; i--){
			System.out.println(values[i]);
		}
		return values;
	}
}
