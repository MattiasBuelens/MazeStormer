package mazestormer.connect;

import static com.google.common.base.Preconditions.checkState;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.simulator.VirtualRobot;

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

		robot = createRobot(context.getSourceMaze());
	}

	private static ControllableRobot createRobot(Maze loadedMaze) {
		ControllableRobot robot = new VirtualRobot(loadedMaze);
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
