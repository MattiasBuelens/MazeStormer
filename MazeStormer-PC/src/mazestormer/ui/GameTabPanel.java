package mazestormer.ui;

import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import mazestormer.controller.IGameController;
import mazestormer.controller.IPlayerController;
import mazestormer.controller.IWorldController;
import mazestormer.controller.PlayerEvent;
import mazestormer.player.PlayerIdentifier;

import com.google.common.eventbus.Subscribe;

public class GameTabPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IGameController controller;

	private JTabbedPane tabbedPane;
	private Map<PlayerIdentifier, PlayerTabPanel> playerPanels = new HashMap<PlayerIdentifier, PlayerTabPanel>();

	public GameTabPanel(IGameController controller) {
		this.controller = controller;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(this.tabbedPane);

		setBorder(new TitledBorder(null, "Players", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createTabs();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void createTabs() {
		for (IPlayerController pc : controller.getPlayerControllers()) {
			addPlayerTab(pc);
		}
		addWorldTab(controller.getWorldController());
		validate();
	}

	private void addPlayerTab(PlayerIdentifier player) {
		addPlayerTab(controller.getPlayerController(player));
	}

	private void addPlayerTab(IPlayerController pc) {
		PlayerTabPanel panel = new PlayerTabPanel(pc);
		playerPanels.put(pc.getPlayer(), panel);
		this.tabbedPane.addTab(pc.getPlayer().getPlayerID(), panel);
	}

	private void removePlayerTab(PlayerIdentifier player) {
		PlayerTabPanel panel = playerPanels.get(player);
		if (panel != null) {
			this.tabbedPane.remove(panel);
			playerPanels.remove(player);
		}
	}

	private void renamePlayerTab(PlayerIdentifier player) {
		PlayerTabPanel panel = playerPanels.get(player);
		if (panel != null) {
			int index = this.tabbedPane.indexOfComponent(panel);
			this.tabbedPane.setTitleAt(index, player.getPlayerID());
			panel.revalidate();
		}
	}

	@Subscribe
	public void onPlayerEvent(PlayerEvent e) {
		switch (e.getEventType()) {
		case PLAYER_ADDED:
			addPlayerTab(e.getPlayer());
			break;
		case PLAYER_REMOVED:
			removePlayerTab(e.getPlayer());
			break;
		case PLAYER_RENAMED:
			renamePlayerTab(e.getPlayer());
		default:
			break;
		}
	}

	private void addWorldTab(IWorldController wc) {
		WorldTabPanel panel = new WorldTabPanel(wc);
		this.tabbedPane.addTab("World", panel);
	}

}
