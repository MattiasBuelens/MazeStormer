package mazestormer.ui;

import java.beans.Beans;

import javax.swing.UIManager;

import mazestormer.controller.IWorldController;
import mazestormer.ui.map.MapPanel;
import net.miginfocom.swing.MigLayout;

public class WorldTabPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IWorldController controller;

	private MapPanel mapPanel;
	private LogPanel logPanel;

	public WorldTabPanel(IWorldController controller) {
		this.controller = controller;

		setLayout(new MigLayout("", "[grow]", "[grow][::200px,growprio 50,grow]"));

		mapPanel = new MapPanel(controller.map());
		mapPanel.setBorder(UIManager.getBorder("TitledBorder.border"));
		add(mapPanel, "cell 0 0,grow");

		logPanel = new LogPanel(controller.log());
		add(logPanel, "cell 0 1,grow");

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(this.controller.getEventBus());
	}

}
