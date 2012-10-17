package mazestormer.connect;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import mazestormer.robot.PhysicalRobot;
import mazestormer.robot.Robot;

public class PhysicalConnector implements Connector {

	private NXTComm comm;
	private NXTCommand command;

	private String deviceName;
	private Robot robot;

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
		return command != null && command.isOpen() && robot != null;
	}

	@Override
	public void connect() {
		if (isConnected())
			return;

		boolean isConnected = createConnection();
		if (!isConnected)
			return;

		robot = createRobot();
		robot.connect();
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

	private Robot createRobot() {
		return new PhysicalRobot(Robot.leftWheelDiameter,
				Robot.rightWheelDiameter, Robot.trackWidth);
	}

	@Override
	public void disconnect() {
		robot.disconnect();
		robot = null;

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

	public NXTCommand getCommand() {
		return command;
	}

}
