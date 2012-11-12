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

	private NXTComm comm;
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

		boolean isConnected = createConnection(context.getDeviceName());
		if (!isConnected)
			return;

		robot = new PhysicalRobot();
	}

	// private boolean createConnection(String deviceName) {
	// // Search for NXT by name and connect over LCP
	// NXTConnector conn = new NXTConnector();
	// boolean isConnected = conn.connectTo(deviceName, null,
	// NXTCommFactory.ALL_PROTOCOLS, NXTComm.LCP);
	// if (!isConnected)
	// return false;
	//
	// // Set up command connector
	// comm = conn.getNXTComm();
	// command = new NXTCommand(comm);
	// NXTCommandConnector.setNXTCommand(command);
	// return true;
	// }

	private boolean createConnection(String deviceName) {
		boolean isConnected = false;

		// Search for NXT
		NXTConnector connector = new NXTConnector();
		NXTInfo[] devices = connector.search(deviceName, null,
				NXTCommFactory.BLUETOOTH);
		if (devices.length == 0)
			return false;

		// Connect to LeJOS firmware
		isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		if (!isConnected)
			return false;

		// Start program
		NXTComm comm = connector.getNXTComm();
		NXTCommand command = new NXTCommand(comm);
		try {
			command.startProgram("Program.nxj");
			connector.close();
		} catch (IOException e) {
			return false;
		}

		// Connect to program
		connector = new NXTConnector();
		isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		if (!isConnected)
			return false;

		// Set up command connector
		this.comm = connector.getNXTComm();
		this.command = new NXTCommand(comm);
		NXTCommandConnector.setNXTCommand(command);

		return isConnected;
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
			if (comm != null) {
				comm.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			command = null;
			comm = null;
		}
	}

}
