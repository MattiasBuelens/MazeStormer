package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import mazestormer.command.CommandType;
import mazestormer.command.ShutdownCommand;
import mazestormer.remote.RemoteCommunicator;
import mazestormer.remote.RemoteRobot;
import mazestormer.robot.ControllableRobot;

public class RemoteConnector implements Connector {

	private NXTConnector connector;
	private RemoteCommunicator communicator;
	private ControllableRobot robot;

	@Override
	public ControllableRobot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return robot;
	}

	@Override
	public boolean isConnected() {
		return communicator != null && communicator.isListening()
				&& robot != null;
	}

	@Override
	public void connect(ConnectionContext context) {
		if (isConnected())
			return;

		// Initialize connection
		if (!createConnection(context.getDeviceName())) {
			return;
		}

		// Create communicator
		communicator = new RemoteCommunicator(connector);

		// Create robot
		robot = new RemoteRobot(communicator);

		// Start communicating
		communicator.start();
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
			// Shut down
			if (communicator != null) {
				communicator.send(new ShutdownCommand(CommandType.SHUTDOWN));
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
