package mazestormer.connect;

import static com.google.common.base.Preconditions.*;
import mazestormer.robot.Pilot;
import mazestormer.robot.SimulatedPilot;

public class VirtualConnector implements Connector {

	/*
	 * Default virtual speeds
	 */
	private static final double travelSpeed = 20d; // cm/sec
	private static final double rotateSpeed = 90d; // degrees/sec

	private Pilot pilot;
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
	public Pilot getPilot() throws IllegalStateException {
		checkState(isConnected());
		return pilot;
	}

	@Override
	public boolean isConnected() {
		return pilot != null;
	}

	@Override
	public void connect() {
		if (isConnected())
			return;

		pilot = createPilot();
	}

	private Pilot createPilot() {
		Pilot pilot = new SimulatedPilot(Pilot.trackWidth);
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		return pilot;
	}

	@Override
	public void disconnect() {
		pilot = null;
	}

}
