package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import lejos.robotics.navigation.Pose;
import mazestormer.controller.IPlayerMapController;
import mazestormer.player.PlayerIdentifier;
import mazestormer.ui.SplitButton;
import mazestormer.ui.ViewPanel;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapLayerHandler;
import mazestormer.ui.map.event.MapLayerRemoveEvent;
import mazestormer.ui.map.event.MapRobotPoseChangeEvent;

import org.apache.batik.bridge.UpdateManager;
import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.Subscribe;

public class PlayerMapPanel extends ViewPanel implements MapLayerHandler {

	private static final long serialVersionUID = 1L;

	private final PlayerIdentifier player;

	private boolean isFollowing;

	private final IPlayerMapController controller;

	private JToolBar actionBar;
	private MapCanvas canvas;

	private final Action goToRobotAction = new GoToRobotAction();
	private final Action goToStartAction = new GoToStartAction();
	private final Action zoomInAction = new ZoomInAction();
	private final Action zoomOutAction = new ZoomOutAction();
	private final Action resetZoomAction = new ResetZoomAction();
	private final Action clearRangesAction = new ClearRangesAction();

	private JPopupMenu menuLayers;
	private Map<MapLayer, JMenuItem> layerMenuItems = new HashMap<MapLayer, JMenuItem>();

	public static final double zoomFactor = 1.5d;

	public PlayerMapPanel(IPlayerMapController controller, PlayerIdentifier player) {
		this.controller = controller;
		this.player = player;

		setLayout(new BorderLayout(0, 0));

		createCanvas();
		add(canvas, BorderLayout.CENTER);

		createActionBar();
		add(actionBar, BorderLayout.NORTH);

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		// Initialize map and layers
		setMap(controller.getDocument());
		for (MapLayer layer : controller.getLayers()) {
			addLayerMenuItem(layer);
		}
	}

	private void createCanvas() {
		canvas = new MapCanvas();
		canvas.setDocumentState(MapCanvas.ALWAYS_DYNAMIC);
	}

	private void createActionBar() {
		actionBar = new JToolBar();
		actionBar.setFloatable(false);

		JToggleButton btnFollow = new JToggleButton("Follow robot");
		btnFollow.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setFollowing(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		actionBar.add(btnFollow);
		actionBar.add(createGoButton());

		Component horizontalGlue = Box.createHorizontalGlue();
		actionBar.add(horizontalGlue);

		actionBar.add(createClearRangesButton());
		actionBar.add(createZoomButton());
		actionBar.add(createLayersButton());
	}

	private SplitButton createGoButton() {
		JPopupMenu menuGo = new JPopupMenu();

		JMenuItem menuGoRobot = new JMenuItem("Go to robot");
		menuGoRobot.setAction(goToRobotAction);
		menuGo.add(menuGoRobot);
		JMenuItem menuGoStart = new JMenuItem("Go to start");
		menuGoStart.setAction(goToStartAction);
		menuGo.add(menuGoStart);

		SplitButton btnGo = new SplitButton();
		btnGo.setAlwaysDropDown(true);
		btnGo.setText("Go to");

		btnGo.setPopupMenu(menuGo);
		addPopup(this, menuGo);

		return btnGo;
	}

	private JButton createClearRangesButton() {
		JButton btnClearRanges = new JButton("Clear ranges");
		btnClearRanges.setAction(clearRangesAction);
		return btnClearRanges;
	}

	private SplitButton createZoomButton() {
		JPopupMenu menuZoom = new JPopupMenu();

		JMenuItem menuZoomIn = new JMenuItem("Zoom in");
		menuZoomIn.setAction(zoomInAction);
		menuZoom.add(menuZoomIn);
		JMenuItem menuZoomOut = new JMenuItem("Zoom out");
		menuZoomOut.setAction(zoomOutAction);
		menuZoom.add(menuZoomOut);
		JMenuItem menuResetZoom = new JMenuItem("Reset zoom");
		menuResetZoom.setAction(resetZoomAction);
		menuZoom.add(menuResetZoom);

		SplitButton btnZoom = new SplitButton();
		btnZoom.setAlwaysDropDown(true);
		btnZoom.setText("Zoom");

		btnZoom.setPopupMenu(menuZoom);
		addPopup(this, menuZoom);

		return btnZoom;
	}

	private SplitButton createLayersButton() {
		menuLayers = new JPopupMenu();

		SplitButton btnLayers = new SplitButton();
		btnLayers.setAlwaysDropDown(true);
		btnLayers.setText("Layers");

		btnLayers.setPopupMenu(menuLayers);
		addPopup(this, menuLayers);

		return btnLayers;
	}

	private void setMap(SVGDocument document) {
		canvas.setDocument(document);
	}

	@Subscribe
	public void onMapChanged(MapChangeEvent event) {
		if (event.getOwner().equals(controller)) {
			setMap(event.getDocument());
		}
	}

	private void addLayerMenuItem(final MapLayer layer) {
		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(layer.getName());
		menuItem.setSelected(layer.isVisible());
		menuItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean isChecked = (e.getStateChange() == ItemEvent.SELECTED);
				controller.setLayerVisible(layer, isChecked);
			}
		});

		layer.setMapLayerHandler(this);
		layerMenuItems.put(layer, menuItem);
		menuLayers.add(menuItem);
	}

	private void removeLayerMenuItem(final MapLayer layer) {
		JMenuItem menuItem = layerMenuItems.get(layer);
		if (menuItem != null) {
			layer.setMapLayerHandler(null);
			layerMenuItems.remove(layer);
			menuLayers.remove(menuItem);
		}
	}

	@Subscribe
	public void onMapLayerAdded(MapLayerAddEvent event) {
		if (event.getOwner().equals(controller)) {
			addLayerMenuItem(event.getLayer());
		}
	}

	@Subscribe
	public void onMapLayerRemoved(MapLayerRemoveEvent event) {
		if (event.getOwner().equals(controller)) {
			removeLayerMenuItem(event.getLayer());
		}
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
		updateRobotPose(controller.getRobotPose());
	}

	private void updateRobotPose(Pose pose) {
		canvas.setEnablePanInteractor(!isFollowing());
		if (isFollowing()) {
			canvas.centerOn(pose.getX(), pose.getY(), pose.getHeading());
		}
	}

	@Subscribe
	public void onMapRobotPoseChanged(MapRobotPoseChangeEvent event) {
		if (event.getOwner().equals(controller) && event.getPlayer().equals(player)) {
			updateRobotPose(event.getPose());
		}
	}

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

	}

	private class GoToRobotAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public GoToRobotAction() {
			putValue(NAME, "Go to robot");
			putValue(SHORT_DESCRIPTION, "Center the map on the robot.");
		}

		public void actionPerformed(ActionEvent e) {
			Pose pose = controller.getRobotPose();
			canvas.centerOn(pose.getX(), pose.getY(), 0);
		}
	}

	private class GoToStartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public GoToStartAction() {
			putValue(NAME, "Go to start");
			putValue(SHORT_DESCRIPTION, "Center the map on the start position.");
		}

		public void actionPerformed(ActionEvent e) {
			canvas.centerOn(0, 0, 0);
		}
	}

	private class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ZoomInAction() {
			putValue(NAME, "Zoom in");
			putValue(SHORT_DESCRIPTION, "Zoom in on the map.");
		}

		public void actionPerformed(ActionEvent e) {
			canvas.zoom(zoomFactor);
		}
	}

	private class ZoomOutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ZoomOutAction() {
			putValue(NAME, "Zoom out");
			putValue(SHORT_DESCRIPTION, "Zoom out on the map.");
		}

		public void actionPerformed(ActionEvent e) {
			canvas.zoom(1d / zoomFactor);
		}
	}

	private class ResetZoomAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ResetZoomAction() {
			putValue(NAME, "Reset zoom");
			putValue(SHORT_DESCRIPTION, "Reset the zoom on the map.");
		}

		public void actionPerformed(ActionEvent e) {
			canvas.resetZoom();
		}
	}

	private class ClearRangesAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ClearRangesAction() {
			putValue(NAME, "Clear ranges");
			putValue(SHORT_DESCRIPTION, "Clear the detected ranges on the map.");
		}

		public void actionPerformed(ActionEvent e) {
			controller.clearRanges();
		}
	}

	@Override
	public void requestDOMChange(Runnable request) {
		UpdateManager updateManager = canvas.getUpdateManager();
		if (updateManager != null) {
			updateManager.getUpdateRunnableQueue().invokeLater(request);
		}
	}

	@Override
	public void layerPropertyChanged(MapLayer layer, String propertyName, Object propertyValue) {
		if (propertyName.equals("isVisible")) {
			JMenuItem menuItem = layerMenuItems.get(layer);
			if (menuItem != null) {
				menuItem.setSelected((Boolean) propertyValue);
			}
		}
	}

}