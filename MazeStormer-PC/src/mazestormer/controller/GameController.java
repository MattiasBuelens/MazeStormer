package mazestormer.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import mazestormer.controller.PlayerEvent.EventType;
import mazestormer.game.player.Player;
import mazestormer.game.player.PlayerIdentifier;
import mazestormer.game.player.PlayerListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class GameController extends SubController implements IGameController {

	private Map<PlayerIdentifier, IPlayerController> pcs = new LinkedHashMap<PlayerIdentifier, IPlayerController>();
	private final Listener listener = new Listener();

	private final WorldController worldController;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(factory);
	private Runnable updateTask = new UpdateTask();
	private ScheduledFuture<?> updateHandle;
	private long updateInterval;
	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("MapController-%d").build();
	private static final long defaultUpdateFPS = 25;

	public GameController(MainController mainController) {
		super(mainController);

		worldController = new WorldController(mainController, mainController.getWorld());

		setUpdateFPS(defaultUpdateFPS);
		scheduleUpdater();
	}

	@Override
	public IWorldController getWorldController() {
		return worldController;
	}

	/*
	 * Player controllers
	 */

	@Override
	public IPlayerController getPlayerController(PlayerIdentifier player) {
		return pcs.get(player);
	}

	@Override
	public IPlayerController getPersonalPlayerController() {
		return getPlayerController(getMainController().getPlayer());
	}

	@Override
	public Collection<IPlayerController> getPlayerControllers() {
		return Collections.unmodifiableCollection(pcs.values());
	}

	@Override
	public void addPlayer(Player player) {
		player.addPlayerListener(listener);
		this.pcs.put(player, new PlayerController(this.getMainController(), player));
		postEvent(new PlayerEvent(EventType.PLAYER_ADDED, player));
	}

	@Override
	public void removePlayer(Player player) {
		player.removePlayerListener(listener);
		this.pcs.remove(player);
		postEvent(new PlayerEvent(EventType.PLAYER_REMOVED, player));
	}

	private void renamePlayer(Player player) {
		postEvent(new PlayerEvent(EventType.PLAYER_RENAMED, player));
	}

	private class Listener implements PlayerListener {

		@Override
		public void playerRenamed(Player player, String previousID, String newID) {
			renamePlayer(player);
		}

	}

	/*
	 * Map updates
	 */

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
		updateHandle = executor.scheduleAtFixedRate(updateTask, 0, getUpdateInterval(), TimeUnit.MILLISECONDS);
	}

	private void cancelUpdater() {
		if (updateHandle != null) {
			updateHandle.cancel(false);
			updateHandle = null;
		}
	}

	private void rescheduleUpdater() {
		// Schedule only if still running
		if (updateHandle != null && !updateHandle.isDone()) {
			scheduleUpdater();
		}
	}

	public void terminate() {
		// Shutdown executor
		executor.shutdown();
	}

	private class UpdateTask implements Runnable {

		@Override
		public void run() {
			// Update player controllers
			for (IPlayerController pc : pcs.values()) {
				pc.map().updatePoses();
			}
			// Update world
			getWorldController().map().updatePoses();
		}

	}

}