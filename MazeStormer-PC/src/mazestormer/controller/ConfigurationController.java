package mazestormer.controller;

import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.connect.RobotType;
import mazestormer.robot.Robot;

public class ConfigurationController extends SubController implements
		IConfigurationController {

	private RobotType robotType;
	private ControlMode controlMode;

	public ConfigurationController(MainController mainController) {
		super(mainController);
	}

	@Override
	public RobotType getRobotType() {
		return robotType;
	}

	private void setRobotType(RobotType robotType) {
		this.robotType = robotType;
	}

	@Override
	public ControlMode getControlMode() {
		return controlMode;
	}

	private void setControlMode(ControlMode controlMode) {
		this.controlMode = controlMode;
		postEvent(new ControlModeChangeEvent(controlMode));
	}

	@Override
	public boolean isConnected() {
		return getMainController().isConnected();
	}

	@Override
	public void connect(RobotType robotType, ControlMode controlMode) {
		if (isConnected())
			throw new IllegalStateException("Already connected.");

		// Set current state
		setRobotType(robotType);
		setControlMode(controlMode);

		// Connect
		getMainController().connect(robotType);
	}

	@Override
	public void disconnect() {
		if (!isConnected())
			throw new IllegalStateException("Not connected.");

		// Stop the robot
		stop();

		// Disconnect
		setControlMode(null);
		getMainController().disconnect();
	}

	@Override
	public void stop() {
		if (isConnected())
			getRobot().stop();
	}

	public Robot getRobot() {
		if (!isConnected())
			throw new IllegalStateException("Not connected.");

		return getMainController().getRobot();
	}

}
