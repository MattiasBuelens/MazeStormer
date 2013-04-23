package mazestormer.game;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import mazestormer.player.Player;
import peno.htttp.Callback;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.Connection;

public class DummyGame extends Game {

	/**
	 * The frequency of position updates.
	 */
	private static final long updateFrequency = 2000; // in ms

	private final PositionReporter positionReporter = new PositionReporter();
	private final ScheduledExecutorService positionExecutor = Executors.newSingleThreadScheduledExecutor(factory);

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("DummyGame-%d").build();

	public DummyGame(Connection connection, String id, Player localPlayer) throws IOException, IllegalStateException {
		super(connection, id, localPlayer);
		addGameListener(new Handler());
	}

	public void join() {
		join(new EmptyCallback());
	}

	private void setReady() {
		setReady(true, new EmptyCallback());
	}

	private class Handler implements GameListener {

		@Override
		public void onGameJoined() {
			setReady();
		}

		@Override
		public void onGameLeft() {
		}

		@Override
		public void onGameRolled(int playerNumber, int objectNumber) {
		}

		@Override
		public void onGameStarted() {
			positionReporter.start();
		}

		@Override
		public void onGamePaused() {
			positionReporter.stop();
			setReady();
		}

		@Override
		public void onGameStopped() {
			positionReporter.stop();
			setReady();
		}

		@Override
		public void onGameWon(int teamNumber) {
		}

		@Override
		public void onPlayerReady(String playerID, boolean isReady) {
		}

		@Override
		public void onObjectFound(String playerID) {
		}

		@Override
		public void onPartnerConnected(Player partner) {
		}

		@Override
		public void onPartnerDisconnected(Player partner) {
		}

	}

	private class PositionReporter {

		private ScheduledFuture<?> task;

		public void start() {
			// Start publishing
			task = positionExecutor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					updatePosition(getLocalPlayer().getRobot().getPoseProvider().getPose());
				}
			}, 0, updateFrequency, TimeUnit.MILLISECONDS);
		}

		public void stop() {
			if (task == null)
				return;

			// Stop publishing
			task.cancel(false);
			task = null;
		}

	}

	private static class EmptyCallback implements Callback<Void> {
		@Override
		public void onSuccess(Void result) {
		}

		@Override
		public void onFailure(Throwable t) {
			System.err.println("Error in dummy game: " + t.getMessage());
		}
	}

}
