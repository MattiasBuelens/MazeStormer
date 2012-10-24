package mazestormer;

import lejos.nxt.*;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.Pilot;


public class LineFollower {

	public static void main(String[] aArg) throws Exception {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		connector.setDeviceName("brons");
		connector.connect();
		
		LightSensor light = new LightSensor(SensorPort.S1);
		Pilot robot = connector.getPilot();
		light.setFloodlight(true);
		
		final int blackWhiteThreshold = 30;
		
		double slowSpeed,fastSpeed;
		slowSpeed = 20;
		fastSpeed = 50;
		
		double rotateAngle = 180.0;
		
		light.setLow(388);
		light.setHigh(428);
		
		robot.setTravelSpeed(5);
		robot.setRotateSpeed(fastSpeed);
		robot.forward();
		
		int value;
		
		System.out.println("Current: " + light.readValue());
		double angle;
		while (true) {
			value = light.readValue();
			if(value > blackWhiteThreshold){
				System.out.println("Found white: " + value);
				robot.stop();
				System.out.println(light.readValue());
				robot.rotate(rotateAngle, false);
				robot.setRotateSpeed(slowSpeed);
				robot.rotateLeft();
				break;
			}
			
		}
		
		while (true) {
			value = light.readValue();
			if(value > blackWhiteThreshold){
				System.out.println("Found white: " + value);
				robot.stop();
				System.out.println(light.readValue());
				robot.setRotateSpeed(fastSpeed);
				robot.rotate(-rotateAngle, false);
				robot.setRotateSpeed(slowSpeed);
				robot.rotateRight();
				break;
			}
			
		}
		
		while (true) {
			value = light.readValue();
			if(value > blackWhiteThreshold){
				System.out.println("Found white: " + value);
				robot.stop();
				angle = robot.getMovement().getAngleTurned();
				System.out.println(light.readValue());
				break;
			}
			
		}
		
		angle = Math.abs(angle) + rotateAngle;
		
		System.out.println("Angle: " + angle);
		
		boolean groter = false;
		
		if(angle > 180){
			groter = true;
			angle = angle - 360;
		}
		
		double extra  = 0 ;
		if(groter){
			extra = 3;
			System.out.println("Extra!");
		}
		
		robot.setRotateSpeed(fastSpeed);
		robot.rotate((angle/2.0) - extra);
		
		double dist = 7.2*Math.cos(Math.toRadians(angle/2.0));
		robot.travel(dist);
		System.out.println(dist);
		
	}
}