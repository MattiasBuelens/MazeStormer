package mazestormer.connect;

import static com.google.common.base.Preconditions.*;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.robot.VirtualRobot;

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
		checkState(isConnected());
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
	}

	private Robot createRobot() {
		Robot robot = new VirtualRobot();
		Pilot pilot = robot.getPilot();
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		return robot;
	}

	@Override
	public void disconnect() {
		robot = null;
	}

}
