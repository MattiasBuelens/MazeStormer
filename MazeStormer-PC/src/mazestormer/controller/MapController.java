package mazestormer.controller;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.player.Player;
import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.MazeLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapLayerRemoveEvent;
import mazestormer.ui.map.event.MapRobotPoseChangeEvent;
import mazestormer.util.CoordUtils;

import org.w3c.dom.svg.SVGDocument;

public abstract class MapController extends SubController implements IMapController {

	private MapDocument map;
	private Map<Player, RobotLayer> robotLayers = new HashMap<Player, RobotLayer>();

	public MapController(MainController mainController) {
		super(mainController);
		createMap();
	}

	private void createMap() {
		map = new MapDocument();

		// TODO Make maze define the view rectangle
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));

		resetMap();
	}

	protected void resetMap() {
		SVGDocument document = map.getDocument();
		postEvent(new MapChangeEvent(this, document));
	}

	protected void addLayer(MapLayer layer) {
		layer.registerEventBus(getEventBus());
		map.addLayer(layer);
		postEvent(new MapLayerAddEvent(this, layer));
	}

	protected void removeLayer(MapLayer layer) {
		map.removeLayer(layer);
		postEvent(new MapLayerRemoveEvent(this, layer));
	}

	protected void addPlayer(Player player) {
		RobotLayer robotLayer = new RobotLayer("Robot " + player.getPlayerID());
		robotLayers.put(player, robotLayer);
		addLayer(robotLayer);
	}

	protected void removePlayer(Player player) {
		RobotLayer robotLayer = robotLayers.get(player);
		if (robotLayer != null) {
			removeLayer(robotLayer);
			robotLayers.remove(player);
		}
	}

	protected MazeLayer addMaze(IMaze maze, String name, int zIndex) {
		MazeLayer mazeLayer = new MazeLayer(name, maze);
		mazeLayer.setZIndex(zIndex);
		addLayer(mazeLayer);
		return mazeLayer;
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

	protected Pose getMapPose(Player player) {
		// Not connected yet
		if (player.getRobot() == null)
			return null;

		return CoordUtils.toMapCoordinates(player.getRobot().getPoseProvider().getPose());
	}

	private void updatePose(Player player) {
		Pose pose = getMapPose(player);
		if (pose == null)
			return;

		RobotLayer layer = robotLayers.get(player);
		if (layer != null) {
			layer.setPosition(pose.getLocation());
			layer.setRotationAngle(pose.getHeading());
		}

		postEvent(new MapRobotPoseChangeEvent(this, player, pose));
	}

	@Override
	public void updatePoses() {
		// Invoke Swing methods in AWT thread
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (Player player : robotLayers.keySet()) {
					updatePose(player);
				}
			}
		});
	}

}
