package mazestormer;

import java.io.IOException;

import lejos.nxt.Motor;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.tools.ConsoleDebugDisplay;
import lejos.pc.tools.ConsoleViewerUI;
import mazestormer.console.ConsoleConnector;
import mazestormer.console.StreamConsoleViewer;
import mazestormer.robot.PhysicalRobot;
import mazestormer.robot.Robot;

public class Program {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// Set up command connection
		Connector.connectTo(nxtName);

		// Open remote console
		ConsoleConnector console = new ConsoleConnector();
		ConsoleViewerUI viewer = new StreamConsoleViewer(System.out);
		console.addViewer(viewer);
		console.addDebugger(new ConsoleDebugDisplay(viewer));
		// console.open(nxtName, false);

		// Start NXT program
		// NXTCommandConnector.getSingletonOpen().startProgram("Program.nxj");

		// Create robot
		PhysicalRobot robot = new PhysicalRobot(Robot.leftWheelDiameter,
				Robot.rightWheelDiameter, Robot.trackWidth);
		float travelSpeed = 15; // cm/sec
		float rotateSpeed = 90; // degrees/sec
		int acceleration = (int) (travelSpeed * 0.5); // cm/sec²

		robot.setAcceleration(acceleration);
		robot.setRotateSpeed(rotateSpeed);
		robot.setTravelSpeed(travelSpeed);
		int n = 5;
		for (int i = 0; i < n; ++i) {
			robot.travel(200 / n);
		}

		// Travel in a square
		// int nSides = 5;
		// int direction = 1; // 1 = counterclockwise, -1 = clockwise
		// double sideLength = 50d;
		// for (int i = 0; i < nSides; ++i) {
		// robot.travel(sideLength);
		// robot.rotate(direction * 360d / (double) nSides);
		// }
		robot.stop();

		Connector.close();
	}

}
