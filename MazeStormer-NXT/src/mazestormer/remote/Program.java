package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import mazestormer.robot.ControllableRobot;

public class Program implements Runnable, ButtonListener {

	private ControllableRobot robot;
	private NXTCommunicator communicator;

	private volatile boolean isRunning = false;
	private volatile boolean isConnected = false;

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
		if (isConnected) {
			communicator.waitComplete();
			disconnect();
		}
	}

	private void connect() {
		// Wait for Bluetooth connection from PC
		println("Connecting");
		NXTConnection connection = Bluetooth.waitForConnection();

		if (connection == null) {
			println("Connection failed");
			stop();
			return;
		}

		// Connected
		isConnected = true;
		println("Connected");
		Sound.beepSequenceUp();

		// Create communicator
		communicator = new NXTCommunicator(connection);
		// Create robot
		robot = new PhysicalRobot(communicator);
		// Start communicator
		communicator.start();
	}

	private void disconnect() {
		if (!isConnected)
			return;

		isConnected = false;
		println("Disconnecting");

		// Terminate robot
		if (robot != null) {
			robot.terminate();
		}

		// Terminate communicator
		if (communicator != null) {
			try {
				communicator.terminate();
			} catch (IOException e) {
			}
		}

		// Disconnected
		println("Disconnected");
		Sound.beepSequence();
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
