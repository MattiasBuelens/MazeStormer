package mazestormer.controller;

public class StateController extends SubController implements IStateController {

	public StateController(MainController mainController) {
		super(mainController);
	}

	public float getXPosition() {
		return getMainController().getPose().getX();
	}

	public float getYPosition() {
		return getMainController().getPose().getY();
	}

	public float getHeading() {
		return getMainController().getPose().getHeading();
	}

}
