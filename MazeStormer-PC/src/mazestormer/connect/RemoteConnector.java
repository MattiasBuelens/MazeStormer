package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import lejos.util.Delay;
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
		try {
			if (!createConnection(context.getDeviceName())) {
				return;
			}

			// Create communicator
			communicator = new RemoteCommunicator(connector);

			// Create robot
			robot = new RemoteRobot(communicator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean createConnection(String deviceName) throws IOException {
		boolean isConnected = false;

		// Search for NXT by name and connect over packet
		NXTConnector connector = new NXTConnector();
		connector.setDebug(true);
		connector.addLogListener(new NXTCommLogListener() {

			@Override
			public void logEvent(Throwable throwable) {
				System.out.println(throwable.getMessage());
			}

			@Override
			public void logEvent(String message) {
				System.out.println(message);
			}
		});

		NXTInfo[] devices = connector.search(deviceName, null,
				NXTCommFactory.BLUETOOTH);
		if (devices.length == 0)
			return false;

		// Connect to LeJOS firmware
		isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		NXTComm comm = connector.getNXTComm();
		NXTCommand command = new NXTCommand(comm);

		// Start program
		command.startProgram("Program.nxj");
		connector.close();

		// Delay.msDelay(2000);

		// Connect to program
		connector = new NXTConnector();
		isConnected = connector.connectTo(devices[0], NXTComm.LCP);
		if (isConnected) {
			this.connector = connector;
		}
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
