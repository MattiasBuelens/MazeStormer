package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import mazestormer.command.CommandType;
import mazestormer.command.ShutdownCommand;
import mazestormer.physical.PhysicalCommunicator;
import mazestormer.physical.PhysicalRobot;
import mazestormer.robot.ControllablePCRobot;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.world.World;

public class PhysicalConnector implements Connector {

	private NXTConnector connector;
	private PhysicalCommunicator communicator;
	private ControllablePCRobot robot;

	@Override
	public ControllablePCRobot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return robot;
	}

	@Override
	public boolean isConnected() {
		return communicator != null && communicator.isListening() && robot != null;
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
		communicator = new PhysicalCommunicator(connector);

		// Create robot
		robot = createRobot(communicator, context.getWorld());

		// Start communicating
		communicator.start();
	}

	private static ControllablePCRobot createRobot(PhysicalCommunicator communicator, World world) {
		ControllablePCRobot robot = new PhysicalRobot(communicator, world);
		// Set default speeds
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(ControllableRobot.travelSpeed);
		pilot.setRotateSpeed(ControllableRobot.rotateSpeed);
		return robot;
	}

	private boolean createConnection(String deviceName) {
		// Search for NXT
		NXTConnector connector = new NXTConnector();
		NXTInfo[] devices = connector.search(deviceName, null, NXTCommFactory.BLUETOOTH);
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
