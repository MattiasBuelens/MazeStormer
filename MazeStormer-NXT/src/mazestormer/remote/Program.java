package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import mazestormer.robot.Robot;

public class Program implements Runnable {

	private Robot robot;
	private NXTCommunicator communicator;

	public static void main(String[] args) throws Exception {
		new Program().run();
		NXT.shutDown();
	}

	@Override
	public void run() {
		// Wait for Bluetooth connection from PC
		NXTConnection connection = Bluetooth.waitForConnection();
		println("Connecting");
		if (connection != null) {
			println("Connected");
			println("ESC to quit");
			// Create communicator
			communicator = new NXTCommunicator(connection);
			// Create robot
			robot = new PhysicalRobot(communicator);
			// Start communicator
			communicator.start();
		}

		// Exit when pressing escape
		Button.ESCAPE.waitForPress();
		if (communicator != null) {
			// Disconnect
			println("Disconnecting");
			robot.terminate();
			try {
				communicator.terminate();
			} catch (IOException e) {
			}
			println("Disconnected");
		}
	}

	private int cursorY = 0;

	private void println(String str) {
		LCD.drawString(padRight(str, LCD.DISPLAY_CHAR_WIDTH, ' '), 0, cursorY++);
	}

	private static String padRight(String str, int size, char padding) {
		StringBuilder padded = new StringBuilder(str);
		while (padded.length() < size) {
			padded.append(padding);
		}
		return padded.toString();
	}

}
