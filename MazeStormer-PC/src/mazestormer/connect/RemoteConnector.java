package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
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
		return robot != null;
	}

	@Override
	public void connect(ConnectionContext context) {
		// Initialize connection
		if (!createConnection(context.getDeviceName())) {
			return;
		}

		// Create communicator
		communicator = new RemoteCommunicator(connector);

		// Create robot
		robot = new RemoteRobot(communicator);
	}

	private boolean createConnection(String deviceName) {
		// Search for NXT by name and connect over packet
		connector = new NXTConnector();
		boolean isConnected = connector.connectTo(deviceName, null,
				NXTCommFactory.BLUETOOTH, NXTComm.PACKET);
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
				// TODO Send shutdown command to robot
				// communicator.send(new ShutdownCommand());
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
