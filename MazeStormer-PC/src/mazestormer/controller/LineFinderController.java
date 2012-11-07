package mazestormer.controller;

import lejos.geom.Line;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements
		ILineFinderController {

	private final static double rotateAngle = 135.0;

	// Correction angle
	private final static double extraAngle = 4.0;

	private LineFinderRunner runner;

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getRobot().getPilot();
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	private CalibratedLightSensor getLightSensor() {
		return getMainController().getRobot().getLightSensor();
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLightSensor().setFloodlight(true);
		}
	}

	@Override
	public void startSearching() {
		runner = new LineFinderRunner();
		runner.start();
	}

	@Override
	public void stopSearching() {
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}

	private void postState(EventType eventType) {
		postEvent(new LineFinderEvent(eventType));
	}

	private class LineFinderRunner implements Runnable {

		private final Pilot pilot;
		private final CalibratedLightSensor sensor;
		private boolean isRunning = false;

		private double originalTravelSpeed;
		private double originalRotateSpeed;

		public LineFinderRunner() {
			this.pilot = getPilot();
			this.sensor = getLightSensor();
		}

		public void start() {
			isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				isRunning = false;
				pilot.stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return isRunning;
		}

		private boolean shouldStop() {
			boolean shouldStop = !isRunning();
			if (shouldStop) {
				pilot.setTravelSpeed(originalTravelSpeed);
				pilot.setRotateSpeed(originalRotateSpeed);
				pilot.stop();
			}
			return shouldStop;
		}

		@Override
		public void run() {
			originalTravelSpeed = pilot.getTravelSpeed();
			originalRotateSpeed = pilot.getRotateSpeed();

			final int threshold = 30;

			double slowRotateSpeed, fastRotateSpeed;
			slowRotateSpeed = 20;
			fastRotateSpeed = 50;

			// TODO: Speed?
			pilot.setTravelSpeed(5);

			// Start looking for line
			log("Start looking for line.");
			pilot.forward();

			int value;

			double angle1 = 0, angle2 = 0;

			while (!shouldStop()) {
				value = sensor.getLightValue();
				if (value > threshold) {
					log("Found line, start rotating left.");
					pilot.stop();
					pilot.setRotateSpeed(fastRotateSpeed);
					if (shouldStop())
						return;
					pilot.rotate(rotateAngle, false);
					pilot.setRotateSpeed(slowRotateSpeed);
					if (shouldStop())
						return;
					pilot.rotateLeft();
					break;
				}

			}

			while (!shouldStop()) {
				value = sensor.getLightValue();
				if (value > threshold) {
					log("Found line, start rotating right.");
					pilot.stop();
					angle1 = pilot.getMovement().getAngleTurned();
					pilot.setRotateSpeed(fastRotateSpeed);
					if (shouldStop())
						return;
					pilot.rotate(-rotateAngle, false);
					pilot.setRotateSpeed(slowRotateSpeed);
					if (shouldStop())
						return;
					pilot.rotateRight();
					break;
				}
			}

			while (!shouldStop()) {
				value = sensor.getLightValue();
				if (value > threshold) {
					pilot.stop();
					angle2 = pilot.getMovement().getAngleTurned();
					break;
				}
			}

			angle1 = Math.abs(angle1) + rotateAngle;
			angle2 = Math.abs(angle2) + rotateAngle;

			pilot.setRotateSpeed(fastRotateSpeed);

			double finalAngle;

			if (isCross(angle1, angle2)) {
				log("Cross detected.");
				finalAngle = ((angle2 - angle1) / 2.0) - extraAngle;
			} else {
				finalAngle = ((angle2 - 360.0) / 2.0) - extraAngle;
			}

			log("Positioning robot perpendicular to the line.");

			if (shouldStop())
				return;
			pilot.rotate(finalAngle);
			double dist = Robot.sensorOffset
					* Math.cos(Math.toRadians(finalAngle));
			if (shouldStop())
				return;
			pilot.travel(dist);

		}

		private final double crossTreshold = 3.0;

		private boolean isCross(double angle1, double angle2) {
			double radAngle1 = Math.toRadians(angle1);
			double radAngle2 = Math.toRadians(angle2);

			float xp = 0;
			float yp = Robot.sensorOffset;

			float x0 = (float) (-Robot.sensorOffset * Math.sin(radAngle1));
			float y0 = (float) (Robot.sensorOffset * Math.cos(radAngle1));

			float x1 = (float) (-Robot.sensorOffset * Math.sin(radAngle1
					- radAngle2));
			float y1 = (float) (Robot.sensorOffset * Math.cos(radAngle1
					- radAngle2));

			double afstand = new Line(x0, y0, x1, y1).ptLineDist(xp, yp);

			// double lambda = ((x1 - x0) * (xp - x0) + (y1 - y0) * (yp - y0))
			// / (Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2));
			//
			// double afstand = Math.sqrt(Math
			// .pow(xp - x0 - lambda * (x1 - x0), 2)
			// + (Math.pow(yp - y0 - lambda * (y1 - y0), 2)));

			return afstand > crossTreshold;
		}
	}
}
