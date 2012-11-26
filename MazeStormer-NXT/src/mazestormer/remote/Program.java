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
	private boolean isConnected = false;

	public static void main(String[] args) throws Exception {
		new Program().run();
	}

	@Override
	public void run() {
		isRunning = true;
		Button.ESCAPE.addButtonListener(this);

		while (isRunning) {
			session();
		}

		NXT.shutDown();
	}

	private void session() {
		clear();
		println("ESC to quit");

		connect();
		communicator.waitComplete();
		disconnect();
	}

	private void connect() {
		// Wait for Bluetooth connection from PC
		println("Connecting");
		NXTConnection connection = Bluetooth.waitForConnection();

		if (connection == null) {
			stop();
		}

		// Connect
		isConnected = true;
		println("Connected");
		// Create communicator
		communicator = new NXTCommunicator(connection);
		// Create robot
		robot = new PhysicalRobot(communicator);
		// Start communicator
		communicator.start();
	}

	private void disconnect() {
		if (isConnected) {
			isConnected = false;
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
		}
	}

	public void stop() {
		isRunning = false;
		disconnect();
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
		LCD.clear(cursorY);
		LCD.drawString(str, 0, cursorY);
		cursorY++;
	}

	private void clear() {
		LCD.clear();
		cursorY = 0;
	}

}
