package mazestormer.controller;

import static com.google.common.base.Preconditions.*;

import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.connect.RobotType;
import mazestormer.robot.Pilot;
import mazestormer.robot.StopEvent;

public class ConfigurationController extends SubController implements IConfigurationController {

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

	private Pilot getPilot() {
		checkState(isConnected());
		return getMainController().getRobot().getPilot();
	}

	@Override
	public boolean isConnected() {
		return getMainController().isConnected();
	}

	@Override
	public void connect(RobotType robotType, ControlMode controlMode) {
		checkState(!isConnected());

		// Set current state
		setRobotType(robotType);
		setControlMode(controlMode);

		// Connect
		getMainController().connect(robotType);
	}

	@Override
	public void disconnect() {
		checkState(isConnected());

		// Stop the robot
		stop();

		// Disconnect
		setControlMode(null);
		getMainController().disconnect();
	}

	@Override
	public void stop() {
		if (isConnected()) {
			getPilot().stop();
			postEvent(new StopEvent());
		}
	}

}
