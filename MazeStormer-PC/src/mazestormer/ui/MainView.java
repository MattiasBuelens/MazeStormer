package mazestormer.ui;

import java.awt.Dimension;
import java.beans.Beans;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.controller.IMainController;
import mazestormer.ui.map.MapPanel;
import mazestormer.util.EventSource;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
	
	private JPanel mainPanel;

	public MainView(IMainController controller){
		setTitle("MazeStormer");
		
		this.controller = controller;

		initialize();

		if(!Beans.isDesignTime()){
			registerController();
		}
	}

	private void registerController(){
		this.controller.register(this);
		setControlMode(this.controller.configuration().getControlMode());
	}

	private void initialize(){
		setBounds(100, 100, 650, 500);
		setMinimumSize(new Dimension(650, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(
				new MigLayout("hidemode 3", "[grow][]", "[][grow][::200px,growprio 50,grow]"));

		this.configurationPanel = new ConfigurationPanel(controller.configuration());
		this.mainPanel.add(configurationPanel, "cell 0 0,grow");

		this.parametersPanel = new ParametersPanel(controller.parameters());
		this.mainPanel.add(parametersPanel, "cell 1 0,grow");

		this.controlPanel = new ManualControlPanel(controller.manualControl());
		this.mainPanel.add(controlPanel, "cell 1 1,grow");

		this.mapPanel = new MapPanel(controller.map());
		this.mapPanel.setBorder(UIManager.getBorder("TitledBorder.border"));
		this.mainPanel.add(mapPanel, "cell 0 1,grow");

		this.logPanel = new LogPanel(controller.log());
		this.mainPanel.add(logPanel, "cell 0 2,grow");

		this.statePanel = new StatePanel(controller.state());
		this.mainPanel.add(statePanel, "cell 1 2,grow");

	    setContentPane(this.mainPanel);
	}

	@Override
	public EventBus getEventBus(){
		return this.eventBus;
	}

	@Override
	public void registerEventBus(EventBus eventBus){
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	protected void postEvent(Object event){
		if(getEventBus() != null)
			getEventBus().post(event);
	}

	private void setControlMode(ControlMode controlMode){
		if(controlMode == null){
			setControlPanel(null);
			return;
		}

		switch(controlMode){
		case Manual:
			setControlPanel(new ManualControlPanel(this.controller.manualControl()));
			break;
		case Polygon:
			setControlPanel(new PolygonControlPanel(this.controller.polygonControl()));
			break;
		}
	}

	private void setControlPanel(ViewPanel controlPanel){
		if(this.controlPanel != null){
			this.mainPanel.remove(this.controlPanel);
		}
		if(controlPanel != null){
			this.mainPanel.add(controlPanel, "cell 1 1,grow");
			this.controlPanel = controlPanel;
		}
		getContentPane().validate();
	}

	@Subscribe
	public void onControlModeChanged(ControlModeChangeEvent e){
		setControlMode(e.getControlMode());
	}
}
