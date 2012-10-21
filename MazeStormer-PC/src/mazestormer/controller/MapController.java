package mazestormer.controller;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.robot.MoveEvent;
import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapDOMChangeRequest;
import mazestormer.ui.map.event.MapLayerAddEvent;

import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class MapController extends SubController implements IMapController {

	private MapDocument map;
	private RobotLayer robotLayer;

	private Timer updater;
	private long updateInterval;

	private static final long defaultUpdateInterval = 1000 / 5;

	public MapController(MainController mainController) {
		super(mainController);

		setUpdateFPS(defaultUpdateInterval);

		createMap();
		createLayers();
	}

	private void createMap() {
		map = new MapDocument();

		// TODO Make maze define the view rectangle
		map.setViewRect(new Rectangle(-250, -250, 500, 500));

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

	private void updateRobotPose() {
		Pose pose = toMapCoordinates(getPose());

		if (robotLayer != null) {
			robotLayer.setPosition(pose.getLocation());
			robotLayer.setRotationAngle(pose.getHeading());
		}
	}

	protected static Pose toMapCoordinates(Pose pose) {
		return new Pose(pose.getX(), -pose.getY(), -pose.getHeading() + 90f);
	}

	private void invokeUpdateRobotPose() {
		// Invoke Swing methods in AWT thread
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateRobotPose();
			}
		});
	}

	private long getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(long interval) {
		updateInterval = Math.abs(interval);
	}

	public void setUpdateFPS(long fps) {
		setUpdateInterval((long) (1000f / (float) fps));
	}

	private void startUpdateTimer() {
		stopUpdateTimer();

		updater = new Timer();
		updater.scheduleAtFixedRate(new UpdateTimerTask(), 0,
				getUpdateInterval());
	}

	private void stopUpdateTimer() {
		if (updater != null) {
			updater.cancel();
			updater = null;
		}
	}

	@Subscribe
	public void updateRobotPoseOnConnect(ConnectEvent e) {
		if (e.isConnected()) {
			// Set initial pose
			invokeUpdateRobotPose();
		} else {
			// Stop updating pose
			stopUpdateTimer();
		}
	}

	@Subscribe
	public void updateRobotPoseOnMove(MoveEvent e) {
		if (e.getEventType() == MoveEvent.EventType.STARTED) {
			// Start updating while moving
			startUpdateTimer();
		} else {
			// Stop updating when move ended
			stopUpdateTimer();
		}
	}

	/**
	 * When no view is attached yet, DOM change requests may not be consumed by
	 * any listener and potentially get lost.
	 * 
	 * This event listener catches these dead requests and runs them directly.
	 */
	@Subscribe
	public void recoverDeadDOMChangeRequest(DeadEvent event) {
		if (event.getEvent() instanceof MapDOMChangeRequest) {
			((MapDOMChangeRequest) event.getEvent()).getRequest().run();
		}
	}

	private class UpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			invokeUpdateRobotPose();
		}

	}

}
