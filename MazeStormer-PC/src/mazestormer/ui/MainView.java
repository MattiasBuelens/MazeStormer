package mazestormer.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import mazestormer.connect.ConnectEvent;
import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.controller.IMainController;
import mazestormer.controller.InitializeEvent;
import mazestormer.util.EventSource;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MainView extends JFrame implements EventSource {

	private static final long serialVersionUID = 1L;

	private final IMainController controller;
	private EventBus eventBus;

	private JPanel mainPanel;
	private ViewPanel gameTabPanel;
	private ViewPanel controlPanel;
	private ViewPanel configurationPanel;
	private ViewPanel calibrationPanel;
	private ViewPanel statePanel;

	private boolean isHorizontalVisible = true;
	private boolean isVerticalVisible = true;

	public MainView(IMainController controller) {
		setTitle("MazeStormer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/res/images/MazeIcon.jpg")));

		this.controller = controller;

		initialize();
		addHideListeners();
		updateHorizontalVisible();
		updateVerticalVisible();

		if (!Beans.isDesignTime()) {
			registerController();
		}
	}

	private void registerController() {
		registerEventBus(this.controller.getEventBus());
		setControlMode(this.controller.configuration().getControlMode());
	}

	private void initialize() {
		setBounds(100, 100, 650, 500);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new MigLayout("hidemode 3", "[grow][]", "[][grow][::200px,growprio 50,grow]"));

		this.configurationPanel = new ConfigurationPanel(controller.configuration());
		this.mainPanel.add(configurationPanel, "cell 0 0,grow");

		this.calibrationPanel = new CalibrationPanel(controller.calibration());
		this.mainPanel.add(calibrationPanel, "cell 1 0,grow");

		this.gameTabPanel = new GameTabPanel(controller.gameControl());
		this.mainPanel.add(gameTabPanel, "cell 0 1 1 2,grow");

		this.statePanel = new StatePanel(controller.state());
		this.mainPanel.add(statePanel, "cell 1 2,grow");

		setContentPane(this.mainPanel);
	}

	@Override
	public EventBus getEventBus() {
		return this.eventBus;
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

	private void setControlMode(ControlMode controlMode, boolean isConnected) {
		if (!isConnected || controlMode == null) {
			setControlPanel(null);
			return;
		}

		switch (controlMode) {
		case Barcode:
			setControlPanel(new BarcodePanel(this.controller.barcodeControl()));
			break;
		case Manual:
			setControlPanel(new ManualControlPanel(this.controller.manualControl()));
			break;
		case Polygon:
			setControlPanel(new PolygonControlPanel(this.controller.polygonControl()));
			break;
		case PerpendicularOnLine:
			setControlPanel(new LineFinderPanel(this.controller.lineFinderControl()));
			break;
		case Explorer:
			setControlPanel(new ExplorerControlPanel(this.controller.explorerControl()));
			break;
		case PathFinder:
			PathFindingPanel p = new PathFindingPanel(this.controller.pathFindingControl());
			p.update();
			setControlPanel(p);
			break;
		case TeamTreasureTrek:
			setControlPanel(new GameSetUpPanel(this.controller.gameSetUpControl()));
			break;
		}
	}

	private void setControlMode(ControlMode controlMode) {
		setControlMode(controlMode, this.controller.configuration().isConnected());
	}

	private void setControlMode(boolean isConnected) {
		setControlMode(this.controller.configuration().getControlMode(), isConnected);
	}

	private void setControlPanel(ViewPanel controlPanel) {
		if (this.controlPanel != null) {
			this.mainPanel.remove(this.controlPanel);
			this.controlPanel = null;
		}
		if (controlPanel != null) {
			this.mainPanel.add(controlPanel, "cell 1 1,grow");
			this.controlPanel = controlPanel;
		}
		getContentPane().validate();
	}

	@Subscribe
	public void onInitialized(InitializeEvent e) {
		updateVisible();
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		setControlMode(e.isConnected());
		updateVisible();
	}

	@Subscribe
	public void onControlModeChanged(ControlModeChangeEvent e) {
		setControlMode(e.getControlMode());
		updateVisible();
	}

	private boolean isHorizontalVisible() {
		return isHorizontalVisible;
	}

	private boolean isVerticalVisible() {
		return isVerticalVisible && controlPanel != null;
	}

	private void updateHorizontalVisible() {
		calibrationPanel.setVisible(isHorizontalVisible() && isVerticalVisible());
		configurationPanel.setVisible(isHorizontalVisible());
		getContentPane().validate();
	}

	private void updateVerticalVisible() {
		calibrationPanel.setVisible(isHorizontalVisible() && isVerticalVisible());
		if (controlPanel != null) {
			controlPanel.setVisible(isVerticalVisible());
		}
		statePanel.setVisible(isVerticalVisible());
		getContentPane().validate();
	}

	private void updateVisible() {
		updateHorizontalVisible();
		updateVerticalVisible();
	}

	private void addHideListeners() {
		bindKeyboardAction(KeyEvent.VK_F1, new ToggleHorizontalAction());
		bindKeyboardAction(KeyEvent.VK_F2, new ToggleVerticalAction());
	}

	private void bindKeyboardAction(int key, Action action) {
		Object binding = new Object();

		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0, false), binding);
		mainPanel.getActionMap().put(binding, action);
	}

	private class ToggleHorizontalAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			isHorizontalVisible = !isHorizontalVisible;
			updateHorizontalVisible();
		}

	}

	private class ToggleVerticalAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			isVerticalVisible = !isVerticalVisible;
			updateVerticalVisible();
		}

	}

}