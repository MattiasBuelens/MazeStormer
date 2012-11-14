package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import mazestormer.robot.Robot;

public class Program implements Runnable, ButtonListener {

	private Robot robot;
	private NXTCommunicator communicator;

	private boolean isRunning = false;

	public static void main(String[] args) throws Exception {
		new Program().run();
		NXT.shutDown();
	}

	@Override
	public void run() {
		isRunning = true;
		Button.ESCAPE.addButtonListener(this);

		// Wait for Bluetooth connection from PC
		println("Connecting");
		NXTConnection connection = Bluetooth.waitForConnection();
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

		while (isRunning)
			Thread.yield();
	}

	public void stop() {
		println("Disconnecting");
		if (robot != null) {
			robot.terminate();
		}
		if (communicator != null) {
			try {
				communicator.terminate();
			} catch (IOException e) {
			}
		}
		println("Disconnected");
		isRunning = false;
	}

	@Override
	public void buttonPressed(Button b) {
		switch (b.getId()) {
		case Button.ID_ESCAPE:
			stop();
			break;
		default:
		}
	}

	@Override
	public void buttonReleased(Button b) {
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
