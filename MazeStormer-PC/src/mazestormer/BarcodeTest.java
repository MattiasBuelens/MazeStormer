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
	
	private static final double TRAVEL_SPEED = 10; 			// [cm/sec]
	private static final double SLOW_TRAVEL_SPEED = 2; 		// [cm/sec]
	
	private static final double START_BAR_LENGTH = 1.8; 	// [cm]
	private static final double BAR_LENGTH = 1.85; 			// [cm]
	private static final int NUMBER_OF_BARS = 6;			// without black start bars
	
	private static final int BLACK_THRESHOLD = 50;
	private static final int BLACK_WHITE_THRESHOLD = 50;
	private static final int WHITE_BLACK_THRESHOLD = 50;
	private static final int LOW = 354;
	private static final int HIGH = 576;
			
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
		light.setLow(LOW);
		light.setHigh(HIGH);
		
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
		
		System.out.println(result);
		pilot.stop();
		connector.disconnect();
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
			return (one<BLACK_WHITE_THRESHOLD && two>BLACK_WHITE_THRESHOLD);
		return (one>WHITE_BLACK_THRESHOLD && two<WHITE_BLACK_THRESHOLD);
		
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
		
		String s = "";
		for(int i=NUMBER_OF_BARS-1; i>=0; i--)
			s=s+values[i];
		System.out.println(s);
		
		return values;
	}
}
