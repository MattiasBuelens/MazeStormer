package mazestormer.connect;

import mazestormer.robot.Robot;
import mazestormer.robot.SimulatedRobot;

public class VirtualConnector implements Connector {

	/*
	 * Default virtual speeds
	 */
	private static final double travelSpeed = 20d; // cm/sec
	private static final double rotateSpeed = 90d; // degrees/sec

	private Robot robot;
	private String deviceName; // not used

	@Override
	public String getDeviceName() {
		return deviceName;
	}

	@Override
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public Robot getRobot() throws IllegalStateException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to robot.");
		return robot;
	}

	@Override
	public boolean isConnected() {
		return robot != null;
	}

	@Override
	public void connect() {
		if (isConnected())
			return;

		robot = createRobot();
		robot.connect();
	}

	private Robot createRobot() {
		Robot robot = new SimulatedRobot(Robot.leftWheelDiameter,
				Robot.rightWheelDiameter, Robot.trackWidth);
		robot.setTravelSpeed(travelSpeed);
		robot.setRotateSpeed(rotateSpeed);
		return robot;
	}

	@Override
	public void disconnect() {
		robot.disconnect();
		robot = null;
	}

}
