package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import mazestormer.robot.PhysicalRobot;
import mazestormer.robot.Robot;

public class PhysicalConnector implements Connector {

	private NXTConnector connector;
	private NXTCommand command;

	private Robot robot;

	@Override
	public Robot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return robot;
	}

	@Override
	public boolean isConnected() {
		return command != null && command.isOpen() && robot != null;
	}

	@Override
	public void connect(ConnectionContext context) {
		if (isConnected())
			return;

		// Initialize connection
		if (!createConnection(context.getDeviceName())) {
			return;
		}

		// Create robot
		robot = new PhysicalRobot();
	}

	private boolean createConnection(String deviceName) {
		// Search for NXT
		NXTConnector connector = new NXTConnector();
		NXTInfo[] devices = connector.search(deviceName, null,
				NXTCommFactory.BLUETOOTH);
		if (devices.length == 0)
			return false;

		// Connect to LeJOS firmware
		boolean isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		if (!isConnected)
			return false;

		// Set up command connector
		this.connector = connector;
		this.command = new NXTCommand(connector.getNXTComm());
		NXTCommandConnector.setNXTCommand(command);
		return true;
	}

	@Override
	public void disconnect() {
		if (!isConnected())
			return;

		robot.terminate();
		robot = null;

		try {
			if (command != null) {
				command.disconnect();
			}
			if (connector != null) {
				connector.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			command = null;
			connector = null;
		}
	}

}
