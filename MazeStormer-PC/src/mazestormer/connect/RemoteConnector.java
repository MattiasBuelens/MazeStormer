package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import mazestormer.remote.RemoteCommunicator;
import mazestormer.remote.RemoteRobot;
import mazestormer.robot.Robot;

public class RemoteConnector implements Connector {

	private NXTConnector connector;

	private RemoteCommunicator communicator;

	private Robot robot;

	@Override
	public Robot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return robot;
	}

	@Override
	public boolean isConnected() {
		return communicator.isListening() && robot != null;
	}

	@Override
	public void connect(ConnectionContext context) {
		if (isConnected())
			return;

		// Initialize connection
		if (!createConnection(context.getDeviceName())) {
			return;
		}

		// TODO Create communicator
		communicator = new RemoteCommunicator(connector);

		// Create robot
		robot = new RemoteRobot(communicator);
	}

	private boolean createConnection(String deviceName) {
		// Search for NXT
		NXTConnector connector = new NXTConnector();
		NXTInfo[] devices = connector.search(deviceName, null,
				NXTCommFactory.BLUETOOTH);
		if (devices.length == 0)
			return false;

		// Connect to program
		boolean isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		if (!isConnected)
			return false;

		// Set up connector
		this.connector = connector;

		return isConnected;
	}

	@Override
	public void disconnect() {
		if (!isConnected())
			return;

		robot.terminate();
		robot = null;

		try {
			if (communicator != null) {
				communicator.terminate();
			}
			if (connector != null) {
				connector.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connector = null;
			communicator = null;
		}
	}

}
