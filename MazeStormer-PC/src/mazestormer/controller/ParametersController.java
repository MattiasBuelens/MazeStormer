package mazestormer.controller;

import mazestormer.robot.Robot;
import mazestormer.ui.event.RobotParameterChangeRequest;

import com.google.common.eventbus.Subscribe;

public class ParametersController extends SubController implements
		IParametersController {

	public ParametersController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	@Override
	@Subscribe
	public void onParameterChangeRequest(RobotParameterChangeRequest e) {
		switch (e.getParameter()) {
		case "travelSpeed":
			getRobot().setTravelSpeed(e.getValue());
			break;
		case "rotateSpeed":
			getRobot().setRotateSpeed(e.getValue());
			break;
		}
	}
}
