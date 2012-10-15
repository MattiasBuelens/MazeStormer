package mazestormer.ui.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import mazestormer.ui.SplitButton;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapLayerPropertyChangeEvent;
import mazestormer.util.EventSource;

import org.apache.batik.swing.JSVGCanvas;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MapPanel extends JPanel implements EventSource {

	private static final long serialVersionUID = 1L;

	private EventBus eventBus;

	private JToolBar actionBar;
	private JSVGCanvas canvas;
	private JPopupMenu menuLayers;

	private Map<MapLayer, JMenuItem> layerMenuItems = new HashMap<MapLayer, JMenuItem>();

	public MapPanel(EventBus eventBus) {
		registerEventBus(eventBus);
		initialize();
	}

	public MapPanel() {
		this(new EventBus());
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
		if (eventBus != null)
			eventBus.post(event);
	}

	private void initialize() {
		setLayout(new BorderLayout(0, 0));

		createCanvas();
		add(canvas, BorderLayout.CENTER);

		createActionBar();
		actionBar.setFloatable(false);
		add(actionBar, BorderLayout.NORTH);
	}

	private void createCanvas() {
		canvas = new MapCanvas();
		canvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
	}

	@Subscribe
	public void onMapChanged(MapChangeEvent event) {
		canvas.setDocument(event.getDocument());
	}

	private void createActionBar() {
		actionBar = new JToolBar();

		JToggleButton btnFollow = new JToggleButton("Follow robot");
		actionBar.add(btnFollow);

		actionBar.add(createGoButton());

		Component horizontalGlue = Box.createHorizontalGlue();
		actionBar.add(horizontalGlue);

		JButton btnReset = new JButton("Reset zoom");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvas.resetRenderingTransform();
			}
		});
		actionBar.add(btnReset);

		actionBar.add(createLayersButton());
	}

	private SplitButton createGoButton() {
		JPopupMenu menuGo = new JPopupMenu();

		JMenuItem menuGoRobot = new JMenuItem("Go to robot");
		menuGo.add(menuGoRobot);
		JMenuItem menuGoStart = new JMenuItem("Go to start");
		menuGo.add(menuGoStart);

		SplitButton btnGo = new SplitButton();
		btnGo.setAlwaysDropDown(true);
		btnGo.setText("Go to");

		btnGo.setPopupMenu(menuGo);
		addPopup(this, menuGo);

		return btnGo;
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

	private void addLayerMenuItem(final MapLayer layer) {
		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(
				layer.getName());
		menuItem.setSelected(layer.isVisible());
		menuItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean isChecked = (e.getStateChange() == ItemEvent.SELECTED);
				layer.setVisible(isChecked);
			}
		});

		layerMenuItems.put(layer, menuItem);
		menuLayers.add(menuItem);
	}

	@Subscribe
	public void onMapLayerAdded(MapLayerAddEvent event) {
		addLayerMenuItem(event.getLayer());
	}

	@Subscribe
	public void onMapLayerPropertyChanged(MapLayerPropertyChangeEvent event) {
		if (event.getPropertyName() == "isVisible") {
			JMenuItem menuItem = layerMenuItems.get(event.getLayer());
			if (menuItem != null) {
				menuItem.setSelected((boolean) event.getPropertyValue());
			}
		}
	}

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

	}
}
