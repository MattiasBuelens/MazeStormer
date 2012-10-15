package mazestormer.controller;

import mazestormer.util.AbstractEventSource;

public abstract class SubController extends AbstractEventSource {

	private final MainController mainController;

	public SubController(MainController mainController) {
		this.mainController = mainController;
	}

	protected MainController getMainController() {
		return mainController;
	}

}
