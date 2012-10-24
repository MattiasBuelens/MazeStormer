package mazestormer;

import java.util.Scanner;

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
		
		Scanner scan = new Scanner(System.in);
		
		double slowSpeed,fastSpeed;
		slowSpeed = 20;
		fastSpeed = 50;
		
		double rotateAngle = 180.0;
		
		light.setLow(388);
		light.setHigh(428);
		
		robot.setTravelSpeed(5);
		robot.setRotateSpeed(fastSpeed);
		robot.travel(80, true);
		int value;
		//System.out.println("White: " + light.readNormalizedValue());
		
		System.out.println("Current: " + light.readValue());
		double angle2;
		while (true) {
			value = light.readValue();
			if(value > blackWhiteThreshold){
				System.out.println("Found white: " + value);
				robot.stop();
				//angle1 = robot.getMovement().getAngleTurned();
				System.out.println(light.readValue());
				//robot.travel(6,false);
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
				//angle1 = robot.getMovement().getAngleTurned();
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
				angle2 = robot.getMovement().getAngleTurned();
				System.out.println(light.readValue());
				//robot.rotate(-10, false);
				break;
			}
			
		}
		
		angle2 = Math.abs(angle2) + rotateAngle;
		
		System.out.println("Angle: " + angle2);
		
		boolean groter = false;
		
		if(angle2 > 180){
			groter = true;
			angle2 = angle2 - 360;
		}
		
		double extra  = 0 ;
		if(groter){
			extra = 3;
			System.out.println("Extra!");
		}
		
		robot.setRotateSpeed(fastSpeed);
		robot.rotate((angle2/2.0) - extra);
		
	
		
		double dist = 7.2*Math.cos(Math.toRadians(angle2/2.0));
		robot.travel(dist);
		System.out.println(dist);
		
	}
}