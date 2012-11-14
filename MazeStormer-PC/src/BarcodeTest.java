import java.io.IOException;

import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;


public class BarcodeTest {
	
	private static final double TRAVEL_SPEED = 5; // [cm/sec]
	private static final double SLOW_TRAVEL_SPEED = 1; // [cm/sec]
	private static final double BAR_LENGTH = 1.8; // [cm]
	private static final int NUMBER_OF_BARS = 6;
	
	private static final int BLACK_WHITE_THRESHOLD = 30;
	

	public static void main(String[] args) throws IOException, InterruptedException{
		Connector connector = new ConnectionProvider().getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);
		
		Robot robot = connector.getRobot();
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		pilot.setRotateSpeed(TRAVEL_SPEED);
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
}
