package mazestormer.ui;

import java.awt.Color;

import mazestormer.controller.IGameSetUpController;

public class GameSetUpPanel extends ViewPanel {
	
	private static final long serialVersionUID = 1521591580799849697L;
	
	private final IGameSetUpController controller;

	public GameSetUpPanel(IGameSetUpController controller) {
		this.controller = controller;
	}
	
	
}
