package mazestormer.controller;

public class StateController extends SubController implements IStateController {

	public StateController(MainController mainController) {
		super(mainController);
	}

	public float getXPosition() {
		return Math.round(getMainController().getPose().getX() * 10) / 10;
	}

	public float getYPosition() {
		return Math.round(getMainController().getPose().getY() * 10) / 10;
	}

	public float getHeading() {
		return Math.round(getMainController().getPose().getHeading() * 10) / 10;
	}

}
