package mazestormer.controller;

public class ConfigurationController extends SubController implements
		IConfigurationController {

	public ConfigurationController(MainController mainController) {
		super(mainController);
	}

	@Override
	public RobotType getRobotType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlMode getControlMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connect(RobotType robotType, ControlMode controlMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
