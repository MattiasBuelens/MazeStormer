package mazestormer.ui;
import java.beans.Beans;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;

import mazestormer.controller.GameEvent;
import mazestormer.controller.IGameController;
import mazestormer.controller.IPlayerController;
import mazestormer.ui.map.MapPanel;

public class GameTabPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;
	
	private final IGameController controller;
	
	private JTabbedPane tabbedPane;
	
	public GameTabPanel(IGameController controller) {
		this.controller = controller;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(this.tabbedPane);
		
		setBorder(new TitledBorder(null, "Players",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		createTabs();
		
		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(this.controller.getEventBus());
	}
	
	private void createTabs() {
		for(IPlayerController pc : this.controller.getPlayerControllers()) {
			JPanel temp = new JPanel();
			
			temp.setLayout(new MigLayout("", "[grow]", "[grow][::200px,growprio 50,grow]"));
		
			MapPanel mapPanel = new MapPanel(pc.map(), pc.getPlayerID());
			mapPanel.setBorder(UIManager.getBorder("TitledBorder.border"));
			LogPanel logPanel = new LogPanel(pc.log());
			
			temp.add(mapPanel, "cell 0 0,grow");
			temp.add(logPanel, "cell 0 2,grow");
			this.tabbedPane.addTab(pc.getPlayerID(), temp);
		}
		validate();
	}
	
	@Subscribe
	public void onGameEvent(GameEvent e) {
		switch(e.getEventType()) {
		case PLAYER_ADDED :
			createTabs();
			break;
		case PLAYER_REMOVED :
			createTabs();
			break;
		default:
			break;
		}
	}
}
