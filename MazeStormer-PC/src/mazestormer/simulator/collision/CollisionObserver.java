package mazestormer.simulator.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import mazestormer.robot.Pilot;
import mazestormer.simulator.VirtualRobot;

public class CollisionObserver implements Runnable, MoveListener {

	private Pilot pilot;
	private VirtualCollisionDetector detector;

	private static final long interval = 100;
	private ScheduledFuture<?> future;
	private final ScheduledExecutorService executor;

	private List<CollisionListener> listeners = new ArrayList<CollisionListener>();

	public CollisionObserver(VirtualRobot robot) {
		this.pilot = robot.getPilot();
		this.detector = robot.getCollisionDetector();
		pilot.addMoveListener(this);

		// Named executor
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(
				getClass().getSimpleName() + "-%d").build();
		executor = Executors.newSingleThreadScheduledExecutor(factory);
	}

	public boolean isRunning() {
		return future != null && !future.isDone();
	}

	public void start() {
		if (isRunning())
			return;

		/*
		 * TODO @Stijn The initial delay is a dirty fix to allow the robot to
		 * back up from the wall after colliding with it. Of course, this also
		 * allows you to drive even further forward into the wall.
		 * 
		 * I don't think there's an easy and/or elegant solution using timed
		 * checks. To do this properly, you'd need to stop the robot before it
		 * makes colliding move.
		 */
		future = executor.scheduleWithFixedDelay(this, interval, interval,
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (future != null)
			future.cancel(false);
	}

	public void terminate() {
		stop();
		executor.shutdownNow();
	}

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		// Start checking for collisions while moving
		start();
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		// Stop checking for collisions when stopped
		stop();
	}

	@Override
	public void run() {
		// Crash when robot is moving and collides with a wall
		if (detector.onWall()) {
			pilot.stop();
			informListeners();
		}
	}

	private void informListeners() {
		for (CollisionListener cl : listeners) {
			cl.brutalCrashOccured();
		}
	}

	public void addCollisionListener(CollisionListener cl) {
		listeners.add(cl);
	}

}
