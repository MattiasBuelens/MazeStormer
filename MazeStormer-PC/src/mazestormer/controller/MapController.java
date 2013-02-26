package mazestormer.controller;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.MoveEvent;
import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.MazeLayer;
import mazestormer.ui.map.RangesLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapDOMChangeRequest;
import mazestormer.ui.map.event.MapLayerAddEvent;
import mazestormer.ui.map.event.MapRobotPoseChangeEvent;
import mazestormer.util.MapUtils;

import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class MapController extends SubController implements IMapController {

	private Player player;
	
	private MapDocument map;
	private RobotLayer robotLayer;
	private MazeLayer mazeLayer;
	private MazeLayer sourceMazeLayer;
	private RangesLayer rangesLayer;

	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor(factory);
	private Runnable updateTask = new UpdateTask();
	private ScheduledFuture<?> updateHandle;
	private long updateInterval;
	
	private static final ThreadFactory factory = new ThreadFactoryBuilder()
			.setNameFormat("MapController-%d").build();

	private static final long defaultUpdateInterval = 1000 / 25;

	public MapController(MainController mainController, Player player) {
		super(mainController);
		setUpdateInterval(defaultUpdateInterval);
		
		this.player = player;

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

		if(getMainController().getPlayer() == getPlayer()) {
			Maze sourceMaze = getMainController().getSourceMaze();
			sourceMazeLayer = new MazeLayer("Source maze", sourceMaze);
			sourceMazeLayer.setZIndex(1);
			sourceMazeLayer.setOpacity(0.5f);
			addLayer(sourceMazeLayer);
		}

		Maze maze = getPlayer().getMaze();
		mazeLayer = new MazeLayer("Discovered maze", maze);
		mazeLayer.setZIndex(2);
		addLayer(mazeLayer);

		rangesLayer = new RangesLayer("Detected ranges");
		addLayer(rangesLayer);
	}

	private void addLayer(MapLayer layer) {
		layer.registerEventBus(getEventBus());
		map.addLayer(layer);
		postEvent(new MapLayerAddEvent(layer));
	}
	
	private Player getPlayer() {
		return this.player;
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

	@Override
	public Pose getRobotPose() {
		return MapUtils.toMapCoordinates(getPlayer().getRobot().getPoseProvider().getPose());
	}

	private void updateRobotPose() {
		Pose pose = getRobotPose();

		if (robotLayer != null) {
			robotLayer.setPosition(pose.getLocation());
			robotLayer.setRotationAngle(pose.getHeading());
		}

		postEvent(new MapRobotPoseChangeEvent(pose));
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
		interval = Math.abs(interval);
		if (interval != this.updateInterval) {
			this.updateInterval = interval;
			// Reschedule with new delay
			rescheduleUpdater();
		}
	}

	public void setUpdateFPS(long fps) {
		setUpdateInterval((long) (1000f / (float) fps));
	}

	private void scheduleUpdater() {
		// Cancel if still running
		cancelUpdater();
		// Reschedule updater
		updateHandle = executor.scheduleAtFixedRate(updateTask, 0,
				getUpdateInterval(), TimeUnit.MILLISECONDS);
	}

	private void cancelUpdater() {
		if (updateHandle != null) {
			updateHandle.cancel(false);
		}
	}

	private void rescheduleUpdater() {
		// Schedule only if still running
		if (updateHandle != null && !updateHandle.isDone()) {
			scheduleUpdater();
		}
	}

	@Override
	public void clearRanges() {
		rangesLayer.clear();
	}

	@Subscribe
	public void clearMazeOnConnect(ConnectEvent e) {
		if (e.isConnected()) {
			// Clear detected maze
			getPlayer().getMaze().clear();
			// Clear detected ranges
			clearRanges();
		}
	}

	@Subscribe
	public void updateRobotPoseOnConnect(ConnectEvent e) {
		if (e.isConnected()) {
			// Set initial pose
			invokeUpdateRobotPose();
		} else {
			// Stop updating pose
			cancelUpdater();
		}
	}

	@Subscribe
	public void updateRobotPoseOnMove(MoveEvent e) {
		if (e.getEventType() == MoveEvent.EventType.STARTED) {
			// Start updating while moving
			scheduleUpdater();
		} else {
			// Stop updating when move ended
			cancelUpdater();
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

	private class UpdateTask implements Runnable {
		@Override
		public void run() {
			invokeUpdateRobotPose();
		}
	}

}
