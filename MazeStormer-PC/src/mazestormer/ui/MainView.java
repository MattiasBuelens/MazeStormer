package mazestormer.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import mazestormer.controller.IMainController;
import mazestormer.ui.map.MapPanel;
import mazestormer.util.EventSource;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.EventBus;

public class MainView extends JFrame implements EventSource {

	private static final long serialVersionUID = 1L;

	private final IMainController controller;

	private EventBus eventBus;
	private ViewPanel mapPanel;
	private ViewPanel controlPanel;
	private ViewPanel parametersPanel;
	private JPanel configurationPanel;
	private JPanel logPanel;
	private JPanel statePanel;

	public MainView(IMainController controller) {
		setTitle("MazeStormer");
		this.controller = controller;

		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 650, 500);
		this.setMinimumSize(new Dimension(650, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(
				new MigLayout("", "[grow][300px:n]", "[][grow][grow]"));

		configurationPanel = new ConfigurationPanel(controller.configuration());
		getContentPane().add(configurationPanel, "cell 0 0,grow");

		parametersPanel = new ParametersPanel(controller.parameters());
		getContentPane().add(parametersPanel, "cell 1 0,grow");

		mapPanel = new MapPanel(controller.map());
		mapPanel.setBorder(UIManager.getBorder("TitledBorder.border"));
		getContentPane().add(mapPanel, "cell 0 1,grow");

		controlPanel = new ManualControlPanel(controller.manualControl());
		getContentPane().add(controlPanel, "cell 1 1,grow");

		logPanel = new LogPanel();
		getContentPane().add(logPanel, "cell 0 2,grow");

		statePanel = new StatePanel();
		getContentPane().add(statePanel, "cell 1 2,grow");
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

	protected void postEvent(Object event) {
		if (getEventBus() != null)
			getEventBus().post(event);
	}

}
