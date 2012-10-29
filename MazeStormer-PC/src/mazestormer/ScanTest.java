package mazestormer;

import java.io.IOException;
import lejos.nxt.*;
import lejos.nxt.remote.RemoteMotor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RotatingRangeScanner;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.CachedRemoteMotor;

public class ScanTest {
	
	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	
	public static void main(String[] args) throws IOException,
	InterruptedException {
		Connector connector = new ConnectionProvider().getConnector(RobotType.Physical);
		connector.setDeviceName(nxtName);
		connector.connect();

		RangeFinder rf = new UltrasonicSensor(SensorPort.S2);
		RemoteMotor rm = CachedRemoteMotor.get(2);
		RotatingRangeScanner rrs = new RotatingRangeScanner(rm, rf);

		float[] angles = {-90f, -75f, -60f, -45f, -30f, -15f, 0, 15f, 30f, 45f, 60f, 75f, 90f};
		rrs.setAngles(angles);

		System.out.println(rrs.getRangeValues());
		
	}

}
