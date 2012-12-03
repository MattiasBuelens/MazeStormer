package mazestormer;

import java.util.ArrayList;
import java.util.Stack;

import lejos.robotics.RangeScanner;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Tile;
import mazestormer.robot.Robot;

public class MazeMapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int xCo, yCo;
		xCo = 0;
		yCo = 0;
		int heading; //0 North, 1 East, 2 South, 3 West
		heading = 0; //North
		
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Remote);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);
		
		RangeScanner scanner = connector.getRobot().getRangeScanner();

		float scanRange = 180f;
		float scanIncrement = 3f;

		int scanCount = (int) (scanRange / scanIncrement) + 1;
		float[] scanAngles = new float[scanCount];
		float scanStart = -scanRange / 2f;
		for (int i = 0; i < scanCount; i++) {
			scanAngles[i] = scanStart + i * scanIncrement;
		}
		scanner.setAngles(scanAngles);
		
		RangeFeatureDetector detector = connector.getRobot().getRangeDetector();
		RangeFeature feature = detector.scan();
		
		System.out.println(feature);
		
		ArrayList maze = new ArrayList();
		
		// 1. QUEUE  <--  path only containing the root;
		Stack queue = new Stack();
		//Tile root = new Tile();
		
		// 2. WHILE        QUEUE is not empty

//		     DO    remove the first path from the QUEUE;
//		              create new paths (to all children);
//		              reject the new paths with loops;
//		              add the new paths to front of QUEUE;
//
//		3. IF  goal reached
//		             THEN success;
//		             ELSE failure; 


	}

}
