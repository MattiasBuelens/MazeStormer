package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.simulator.VirtualRobot;
import mazestormer.world.World;

public class VirtualConnector implements Connector {

	private ControllableRobot robot;

	@Override
	public ControllableRobot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return robot;
	}

	@Override
	public boolean isConnected() {
		return robot != null;
	}

	@Override
	public void connect(ConnectionContext context) {
		if (isConnected())
			return;

		robot = createRobot(context.getWorld());
	}

	private static ControllableRobot createRobot(World world) {
		ControllableRobot robot = new VirtualRobot(world);
		// Set default speeds
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(ControllableRobot.travelSpeed);
		pilot.setRotateSpeed(ControllableRobot.rotateSpeed);
		return robot;
	}

	@Override
	public void disconnect() {
		if (!isConnected())
			return;

		robot.terminate();
		robot = null;
	}

}
