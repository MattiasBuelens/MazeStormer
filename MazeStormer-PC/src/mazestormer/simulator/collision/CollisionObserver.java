package mazestormer.simulator.collision;

import java.util.ArrayList;
import java.util.List;

import mazestormer.simulator.VirtualRobot;

public class CollisionObserver implements Runnable {

	private VirtualRobot robot;
	private VirtualCollisionDetector detector;
	private boolean isRunning = false;

	private List<CollisionListener> listeners = new ArrayList<CollisionListener>();

	public CollisionObserver(VirtualRobot robot) {
		this.robot = robot;
		this.detector = robot.getCollisionDetector();
	}

	public void start() {
		isRunning = true;
		new Thread(this).start();
	}

	public void run() {
		while (isRunning) {
			if (detector.onWall()) {
				robot.getPilot().stop();
				informListeners();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
