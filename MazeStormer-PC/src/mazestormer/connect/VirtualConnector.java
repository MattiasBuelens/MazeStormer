package mazestormer.connect;

import static com.google.common.base.Preconditions.*;
import mazestormer.maze.Maze;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.simulator.VirtualRobot;

public class VirtualConnector implements Connector {

	/*
	 * Default virtual speeds
	 */
	private static final double travelSpeed = 40d; // cm/sec
	private static final double rotateSpeed = 180d; // degrees/sec

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
		if (isConnected())
			return;

		robot = createRobot(context.getSourceMaze());
	}

	private Robot createRobot(Maze loadedMaze) {
		Robot robot = new VirtualRobot(loadedMaze);
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
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
