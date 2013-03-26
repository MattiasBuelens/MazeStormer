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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import mazestormer.controller.IMapController;
import mazestormer.ui.SplitButton;
import mazestormer.ui.ViewPanel;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapLayerHandler;
import mazestormer.ui.map.event.MapLayerRemoveEvent;

import org.apache.batik.bridge.UpdateManager;
import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.Subscribe;

public class MapPanel extends ViewPanel implements MapLayerHandler {

	private static final long serialVersionUID = 1L;

	private final IMapController controller;

	protected JToolBar actionBar;
	protected MapCanvas canvas;

	private final Action zoomInAction = new ZoomInAction();
	private final Action zoomOutAction = new ZoomOutAction();
	private final Action resetZoomAction = new ResetZoomAction();

	private JPopupMenu menuLayers;
	private Map<MapLayer, JMenuItem> layerMenuItems = new HashMap<MapLayer, JMenuItem>();

	public static final double zoomFactor = 1.5d;

	public MapPanel(IMapController controller) {
		this.controller = controller;

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

		createLeftActionButtons();

		Component horizontalGlue = Box.createHorizontalGlue();
		actionBar.add(horizontalGlue);

		createRightActionButtons();
	}

	protected void createLeftActionButtons() {
	}

	protected void createRightActionButtons() {
		actionBar.add(createZoomButton());
		actionBar.add(createLayersButton());
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

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

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
