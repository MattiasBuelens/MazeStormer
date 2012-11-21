package mazestormer.controller;

import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements
		ILineFinderController {

	private final static double slowRotateSpeed = 30;
	private final static double fastRotateSpeed = 50;
	private final static double slowTravelSpeed = 1.0;
	private final static double fastTravelSpeed = 5.0;

	private final double maxAttackAngle = 20.0;
	private final double safetyAngle = 10.0;
	private final double fastRotateAngle = -(90 - maxAttackAngle - safetyAngle);

	private final static int threshold = 50;

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

	private class LineFinderRunner implements Runnable {

		private final Pilot pilot;
		private final CalibratedLightSensor light;
		private boolean isRunning = false;

		private CommandHandle handle;
		private double originalTravelSpeed;
		private double originalRotateSpeed;

		public LineFinderRunner() {
			this.pilot = getPilot();
			this.light = getLightSensor();
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

		private void onLine(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_GREATER_THAN, threshold);
			handle = getRobot().when(condition).stop().run(action).build();
		}

		private void offLine(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN, threshold);
			handle = getRobot().when(condition).stop().run(action).build();
		}

		@Override
		public void run() {
			// Save original speeds
			originalTravelSpeed = pilot.getTravelSpeed();
			originalRotateSpeed = pilot.getRotateSpeed();

			// Travel forward until on line
			log("Start looking for line.");
			pilot.setTravelSpeed(fastTravelSpeed);
			pilot.forward();
			onLine(new Runnable() {
				@Override
				public void run() {
					onFirstLine();
				}
			});
		}

		private void onFirstLine() {
			if (shouldStop())
				return;
			log("On line, start looking for end of line.");

			// Travel forward until off line
			pilot.setTravelSpeed(slowTravelSpeed);
			pilot.forward();
			offLine(new Runnable() {
				@Override
				public void run() {
					offFirstLine();
				}
			});
		}

		private void offFirstLine() {
			if (shouldStop())
				return;
			log("Off line, centering robot on line.");
			double lineWidth = pilot.getMovement().getDistanceTraveled();
			double centerOffset = Robot.sensorOffset - lineWidth / 2
					- light.getSensorRadius();
			log("Line width: " + lineWidth);
			log("Offset from center: " + centerOffset);

			// Travel forward to center robot on end of line
			pilot.setTravelSpeed(fastTravelSpeed);
			pilot.travel(centerOffset);
			if (shouldStop())
				return;

			// Rotate fixed angle
			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate(fastRotateAngle);
			if (shouldStop())
				return;

			// Rotate until on line
			log("Start looking for line again.");
			pilot.setRotateSpeed(slowRotateSpeed);
			pilot.rotateRight();
			onLine(new Runnable() {
				@Override
				public void run() {
					onSecondLine();
				}
			});
		}

		private void onSecondLine() {
			if (shouldStop())
				return;
			log("On line, rotating robot perpendicular to line.");

			// Position perpendicular
			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate(90.0);

			// Restore original speed
			pilot.setTravelSpeed(originalTravelSpeed);
			pilot.setRotateSpeed(originalRotateSpeed);
		}

	}
}
