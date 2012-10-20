package mazestormer.controller;

import java.awt.Rectangle;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.robot.MoveEvent;
import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;

import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.Subscribe;

public class MapController extends SubController implements IMapController {

	private MapDocument map;
	private RobotLayer robotLayer;

	public MapController(MainController mainController) {
		super(mainController);

		createMap();
		createLayers();
	}

	private void createMap() {
		map = new MapDocument();

		// TODO Make maze define the view rectangle
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));

		SVGDocument document = map.getDocument();
		postEvent(new MapChangeEvent(document));
	}

	private void createLayers() {
		robotLayer = new RobotLayer("Robot");
		addLayer(robotLayer);
	}

	private void addLayer(MapLayer layer) {
		layer.registerEventBus(getEventBus());
		map.addLayer(layer);
		postEvent(new MapLayerAddEvent(layer));
	}

	@Override
	public SVGDocument getDocument() {
		return map.getDocument();
	}

	@Override
	public Set<MapLayer> getLayers() {
		return map.getLayers();
	}

	@Override
	public void setLayerVisible(MapLayer layer, boolean isVisible) {
		layer.setVisible(isVisible);
	}

	public Pose getPose() {
		return getMainController().getPose();
	}

	public void updateRobotPose() {
		Pose pose = getPose();

		if (robotLayer != null) {
			robotLayer.setPosition(pose.getLocation());
			robotLayer.setRotationAngle(pose.getHeading());
		}
	}

	@Subscribe
	public void updateRobotPose(MoveEvent e) {
		updateRobotPose();
	}

}
