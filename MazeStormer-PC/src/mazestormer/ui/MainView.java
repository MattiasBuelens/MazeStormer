package mazestormer.ui;

import javax.swing.JFrame;

import mazestormer.controller.MainController;
import mazestormer.util.EventPublisher;

import com.google.common.eventbus.EventBus;

public class MainView extends JFrame implements EventPublisher {

	private MainController controller;

	private EventBus eventBus;
	private ViewPanel controlPanel;
	private ParametersPanel parametersPanel;

	public MainView(MainController controller) {
		this.controller = controller;

		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		controlPanel = new ManualControlPanel();
		controlPanel.registerEventBus(eventBus);
		add(controlPanel);

		parametersPanel = new ParametersPanel();
		parametersPanel.registerEventBus(eventBus);
		add(controlPanel);
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
