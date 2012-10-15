package mazestormer.controller;

import mazestormer.util.AbstractEventPublisher;

public abstract class SubController extends AbstractEventPublisher {

	private final MainController mainController;

	public SubController(MainController mainController) {
		this.mainController = mainController;
	}

	protected MainController getMainController() {
		return mainController;
	}

}
