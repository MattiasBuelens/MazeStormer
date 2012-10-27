package mazestormer.connect;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import mazestormer.robot.PhysicalPilot;
import mazestormer.robot.Pilot;

public class PhysicalConnector implements Connector {

	private NXTComm comm;
	private NXTCommand command;

	private String deviceName;
	private Pilot pilot;

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
		return command != null && command.isOpen() && pilot != null;
	}

	@Override
	public void connect() {
		if (isConnected())
			return;

		boolean isConnected = createConnection();
		if (!isConnected)
			return;

		pilot = createPilot();
	}

	private boolean createConnection() {
		// Search for NXT by name and connect over LCP
		NXTConnector conn = new NXTConnector();
		boolean isConnected = conn.connectTo(getDeviceName(), null,
				NXTCommFactory.ALL_PROTOCOLS, NXTComm.LCP);
		if (!isConnected)
			return false;

		// Set up command connector
		comm = conn.getNXTComm();
		command = new NXTCommand(comm);
		NXTCommandConnector.setNXTCommand(command);
		return true;
	}

	private Pilot createPilot() {
		return new PhysicalPilot(Pilot.leftWheelDiameter,
				Pilot.rightWheelDiameter, Pilot.trackWidth);
	}

	@Override
	public void disconnect() {
		pilot = null;

		try {
			if (command != null) {
				command.disconnect();
			}
			if (comm != null) {
				comm.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			command = null;
			comm = null;
		}
	}

}
