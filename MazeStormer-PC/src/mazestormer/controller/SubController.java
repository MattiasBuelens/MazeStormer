package mazestormer.controller;

import mazestormer.util.AbstractEventSource;

public abstract class SubController extends AbstractEventSource {

	private final MainController mainController;

	public SubController(MainController mainController) {
		this.mainController = mainController;
		mainController.register(this);
	}

	public MainController getMainController() {
		return mainController;
	}

}
