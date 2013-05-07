package mazestormer.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import mazestormer.robot.ControllableRobot;
import mazestormer.robot.RobotUpdate;
import mazestormer.robot.RobotUpdateListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class VirtualUpdateProducer {

	private final ControllableRobot robot;

	private final List<RobotUpdateListener> listeners = new ArrayList<RobotUpdateListener>();

	private final ScheduledExecutorService executor;
	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("VirtualUpdateProducer-%d")
			.build();

	public VirtualUpdateProducer(ControllableRobot robot) {
		this.robot = robot;

		executor = Executors.newSingleThreadScheduledExecutor(factory);
		start();
	}

	protected void start() {
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				produce();
			}
		}, 0, ControllableRobot.updateReportDelay, TimeUnit.MILLISECONDS);
	}

	protected void produce() {
		// Create update
		RobotUpdate update = RobotUpdate.create(robot, true, false);
		// Call listeners
		for (RobotUpdateListener listener : listeners) {
			listener.updateReceived(update);
		}
	}

	public void terminate() {
		if (!executor.isShutdown()) {
			executor.shutdownNow();
		}
	}

	public void addUpdateListener(RobotUpdateListener listener) {
		listeners.add(listener);
	}

	public void removeUpdateListener(RobotUpdateListener listener) {
		listeners.remove(listener);
	}

}
