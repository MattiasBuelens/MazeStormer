package mazestormer.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import mazestormer.controller.IMainController;
import mazestormer.util.EventSource;

import com.google.common.eventbus.EventBus;

public class MainView extends JFrame implements EventSource {

	private static final long serialVersionUID = 1L;

	private final IMainController controller;

	private EventBus eventBus;
	private ViewPanel controlPanel;
	private ViewPanel controlPanel2;
	private ViewPanel parametersPanel;

	public MainView(IMainController controller) {
		this.controller = controller;

		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		controlPanel = new ManualControlPanel(controller.manualControl());
		getContentPane().add(controlPanel, BorderLayout.WEST);

		controlPanel2 = new PolygonControlPanel(controller.polygonControl());
		getContentPane().add(controlPanel2, BorderLayout.CENTER);

		parametersPanel = new ParametersPanel(controller.parameters());
		getContentPane().add(parametersPanel, BorderLayout.EAST);
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void registerEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

}
