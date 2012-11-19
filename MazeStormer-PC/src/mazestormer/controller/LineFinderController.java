package mazestormer.controller;

import lejos.geom.Line;
import mazestormer.command.ConditionalCommandBuilder.CommandBuilder;
import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.command.ConditionalCommandBuilder.CompareOperator;
import mazestormer.command.ConditionalCommandBuilder.ConditionSource;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements
		ILineFinderController {

	private final static double rotateAngle = 135.0;
	private final static double extraAngle = 0.0; // 4.0;

	private final static double travelSpeed = 5;
	private final static double slowRotateSpeed = 30;
	private final static double fastRotateSpeed = 50;

	private final static int threshold = 30;
	private final static double crossTreshold = 3.0;

	private LineFinderRunner runner;

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	private Pilot getPilot() {
		return getRobot().getPilot();
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

	private boolean isCross(double angle1, double angle2) {
		double radAngle1 = Math.toRadians(angle1);
		double radAngle2 = Math.toRadians(angle2);

		float xp = 0;
		float yp = Robot.sensorOffset;

		float x0 = (float) (-Robot.sensorOffset * Math.sin(radAngle1));
		float y0 = (float) (Robot.sensorOffset * Math.cos(radAngle1));

		float x1 = (float) (-Robot.sensorOffset * Math.sin(radAngle1
				- radAngle2));
		float y1 = (float) (Robot.sensorOffset * Math
				.cos(radAngle1 - radAngle2));

		double distance = new Line(x0, y0, x1, y1).ptLineDist(xp, yp);

		// double lambda = ((x1 - x0) * (xp - x0) + (y1 - y0) * (yp - y0))
		// / (Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2));
		//
		// double distance = Math.sqrt(Math
		// .pow(xp - x0 - lambda * (x1 - x0), 2)
		// + (Math.pow(yp - y0 - lambda * (y1 - y0), 2)));

		return distance > crossTreshold;
	}

	private class LineFinderRunner implements Runnable {

		private final Pilot pilot;
		private final CalibratedLightSensor sensor;
		private boolean isRunning = false;

		private double angle1;
		private double angle2;
		private CommandHandle handle;
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
				handle.cancel();
				pilot.setTravelSpeed(originalTravelSpeed);
				pilot.setRotateSpeed(originalRotateSpeed);
				pilot.stop();
			}
			return shouldStop;
		}

		private void findLine(final Runnable action) {
			CommandBuilder builder = getRobot().when(ConditionSource.LIGHT,
					CompareOperator.GREATER_THAN,
					sensor.getNormalizedLightValue(threshold));
			builder.stop();
			builder.run(action);
			handle = builder.build();
		}

		@Override
		public void run() {
			// Save original speeds
			originalTravelSpeed = pilot.getTravelSpeed();
			originalRotateSpeed = pilot.getRotateSpeed();

			// Set travel speed
			pilot.setTravelSpeed(travelSpeed);

			// Travel forward and fine line
			log("Start looking for line.");
			pilot.forward();
			findLine(new Runnable() {
				@Override
				public void run() {
					foundFirstLine();
				}
			});
		}

		private void foundFirstLine() {
			if (shouldStop())
				return;
			log("Found line, start rotating left.");

			// Rotate fixed angle
			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate(rotateAngle, false);
			if (shouldStop())
				return;

			// Rotate left and find line
			pilot.setRotateSpeed(slowRotateSpeed);
			pilot.rotateLeft();
			findLine(new Runnable() {
				@Override
				public void run() {
					foundSecondLine();
				}
			});
		}

		private void foundSecondLine() {
			if (shouldStop())
				return;
			log("Found line, start rotating right.");

			// Get first angle
			angle1 = pilot.getMovement().getAngleTurned();

			// Rotate fixed angle
			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate(-rotateAngle, false);
			if (shouldStop())
				return;

			// Rotate right and find line
			pilot.setRotateSpeed(slowRotateSpeed);
			pilot.rotateRight();
			findLine(new Runnable() {
				@Override
				public void run() {
					foundThirdLine();
				}
			});
		}

		private void foundThirdLine() {
			if (shouldStop())
				return;

			// Get second angle
			angle2 = pilot.getMovement().getAngleTurned();

			// Get absolute angles
			angle1 = Math.abs(angle1) + rotateAngle;
			angle2 = Math.abs(angle2) + rotateAngle;

			// Get final angle
			double finalAngle;
			if (isCross(angle1, angle2)) {
				log("Cross detected.");
				finalAngle = ((angle2 - angle1) / 2.0) - extraAngle;
			} else {
				finalAngle = ((angle2 - 360.0) / 2.0) - extraAngle;
			}

			// Position robot
			log("Positioning robot perpendicular to the line.");
			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate(finalAngle);
			if (shouldStop())
				return;

			double dist = Robot.sensorOffset
					* Math.cos(Math.toRadians(finalAngle));
			pilot.travel(dist);
			if (shouldStop())
				return;

			// Restore original speed
			pilot.setTravelSpeed(originalTravelSpeed);
			pilot.setRotateSpeed(originalRotateSpeed);
		}

	}
}
