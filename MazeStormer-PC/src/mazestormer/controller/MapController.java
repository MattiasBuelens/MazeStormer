package mazestormer.controller;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.player.Player;
import mazestormer.robot.Robot;
import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.MapLayerHandler;
import mazestormer.ui.map.MazeLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapLayerRemoveEvent;
import mazestormer.ui.map.event.MapRobotPoseChangeEvent;
import mazestormer.util.CoordUtils;

import org.w3c.dom.svg.SVGDocument;

public abstract class MapController extends SubController implements IMapController {

	private MapDocument map;
	private Map<Player, RobotLayer> robotLayers = new ConcurrentHashMap<Player, RobotLayer>();
	private MapLayerHandler mapLayerHandler;

	public MapController(MainController mainController) {
		super(mainController);

		// Create map
		map = new MapDocument();

		// TODO Make maze define the view rectangle
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));
	}

	protected void addLayer(MapLayer layer) {
		map.addLayer(layer);
		layer.setMapLayerHandler(getMapLayerHandler());
		postEvent(new MapLayerAddEvent(this, layer));
	}

	protected void removeLayer(MapLayer layer) {
		map.removeLayer(layer);
		layer.setMapLayerHandler(null);
		postEvent(new MapLayerRemoveEvent(this, layer));
	}

	protected void addPlayer(Player player) {
		if (!robotLayers.containsKey(player)) {
			RobotLayer robotLayer = new RobotLayer("Robot " + player.getPlayerID());
			robotLayers.put(player, robotLayer);
			addLayer(robotLayer);
		}
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

	protected MapLayerHandler getMapLayerHandler() {
		return mapLayerHandler;
	}

	@Override
	public void setMapLayerHandler(MapLayerHandler handler) {
		this.mapLayerHandler = handler;
		// Set map handler
		map.setMapHandler(handler);
		// Set map layer handlers
		for (MapLayer layer : map.getLayers()) {
			layer.setMapLayerHandler(handler);
		}
	}

	@Override
	public void setLayerVisible(MapLayer layer, boolean isVisible) {
		layer.setVisible(isVisible);
	}

	protected Pose getRobotPose(Player player) {
		Robot robot = player.getRobot();
		return robot == null ? new Pose() : robot.getPoseProvider().getPose();
	}

	protected Pose getMapPose(Player player) {
		Robot robot = player.getRobot();
		return robot == null ? null : getMapPose(getRobotPose(player));
	}

	protected Pose getMapPose(Pose pose) {
		return pose == null ? null : CoordUtils.toMapCoordinates(pose);
	}

	private void updatePose(Player player) {
		Pose robotPose = getRobotPose(player);
		if (robotPose == null)
			return;
		Pose mapPose = getMapPose(robotPose);

		// Update layer
		RobotLayer layer = robotLayers.get(player);
		if (layer != null) {
			// Transform
			layer.setPosition(mapPose.getLocation());
			layer.setRotationAngle(mapPose.getHeading());
			// Tooltip
			layer.setTooltipText(getTooltipText(player, robotPose));
		}

		// Post changed map pose
		postEvent(new MapRobotPoseChangeEvent(this, player, mapPose));
	}

	protected String getTooltipText(Player player, Pose pose) {
		StringBuilder sb = new StringBuilder();
		// Name
		sb.append("<strong>").append(player.getPlayerID()).append("</strong>");
		// Pose
		String x = String.format("%.2f", pose.getX());
		String y = String.format("%.2f", pose.getY());
		String heading = String.format("%.2f", pose.getHeading());
		sb.append("<br>X: ").append(x);
		sb.append("<br>Y: ").append(y);
		sb.append("<br>A: ").append(heading).append('°');
		return sb.toString();
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
