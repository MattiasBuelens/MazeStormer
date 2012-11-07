package mazestormer.controller;

import static com.google.common.base.Preconditions.*;

import com.google.common.eventbus.Subscribe;

import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.connect.RobotType;
import mazestormer.robot.Pilot;
import mazestormer.robot.StopEvent;

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

	@Override
	public void setControlMode(ControlMode controlMode) {
		if (controlMode != this.controlMode) {
			// Stop robot before changing
			stop();
			// Change control mode
			this.controlMode = controlMode;
			postEvent(new ControlModeChangeEvent(controlMode));
		}
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
	public void connect(RobotType robotType) {
		checkState(!isConnected());

		setRobotType(robotType);
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
