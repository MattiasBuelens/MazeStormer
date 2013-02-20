package mazestormer.ui;
import java.beans.Beans;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

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
			temp.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
			MapPanel mapPanel = new MapPanel(pc.map());
			mapPanel.setBorder(UIManager.getBorder("TitledBorder.border"));
			LogPanel logPanel = new LogPanel(pc.log());
			
			temp.add(mapPanel);
			temp.add(logPanel);
			this.tabbedPane.addTab(pc.getPlayer().getPlayerName(), temp);
		}
	}
}
