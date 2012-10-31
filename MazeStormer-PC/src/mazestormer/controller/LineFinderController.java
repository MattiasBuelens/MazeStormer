package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import lejos.nxt.LightSensor;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.Pilot;

public class LineFinderController extends SubController implements
		ILineFinderController {

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getRobot().getPilot();
	}
	
	private void log(String logText){
		getMainController().getLogger().info(logText);
	}

	private LightSensor getLightSensor() {
		return getMainController().getRobot().getLightSensor();
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLightSensor().setFloodlight(true);
		}
	}

	@Override
	public int measureLightValue() {
		return getLightSensor().getNormalizedLightValue();
	}

	private LineFinderRunner runner;

	@Override
	public void startSearching(int highLightValue, int lowLightValue) {
		runner = new LineFinderRunner(highLightValue, lowLightValue);
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

		private final int lowLightValue;
		private final int highLightValue;

		private final Pilot pilot;
		private boolean isRunning = false;

		public LineFinderRunner(int highLightValue, int lowLightValue) {
			this.pilot = getPilot();
			this.lowLightValue = lowLightValue;
			this.highLightValue = highLightValue;
		}

		public void start() {
			if (lowLightValue >= highLightValue) {
				getMainController().getLogger().warning(
						"High light value is lower than low light value.");
			} else {
				isRunning = true;
				new Thread(this).start();
				postState(EventType.STARTED);
			}
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

		@Override
		public void run() {
			final int threshold = 30;

			double slowRotateSpeed, fastRotateSpeed;
			slowRotateSpeed = 20;
			fastRotateSpeed = 50;

			double rotateAngle = 180.0;

			getLightSensor().setLow(lowLightValue);
			getLightSensor().setHigh(highLightValue);

			// TODO: Speed?
			pilot.setTravelSpeed(5);

			// Start looking for line
			log("Start looking for line.");
			pilot.forward();

			int value;

			double angle1,angle2;

			while (true) {
				value = getLightSensor().readValue();
				if (value > threshold) {
					log("Found line, start rotating left.");
					pilot.stop();
					pilot.setRotateSpeed(fastRotateSpeed);
					pilot.rotate(rotateAngle, false);
					pilot.setRotateSpeed(slowRotateSpeed);
					pilot.rotateLeft();
					break;
				}

			}

			while (true) {
				value = getLightSensor().readValue();
				if (value > threshold) {
					log("Found line, start rotating right.");
					pilot.stop();
					angle1 = pilot.getMovement().getAngleTurned();
					pilot.setRotateSpeed(fastRotateSpeed);
					pilot.rotate(-rotateAngle, false);
					pilot.setRotateSpeed(slowRotateSpeed);
					pilot.rotateRight();
					break;
				}

			}

			while (true) {
				value = getLightSensor().readValue();
				if (value > threshold) {
					pilot.stop();
					angle2 = pilot.getMovement().getAngleTurned();
					break;
				}

			}
			double ang1tmp, ang2tmp;
			ang1tmp = Math.abs(angle1) + 180.0;
			ang2tmp = Math.abs(angle2) + 180.0;
			
			System.out.println("Angle: " + ang1tmp);
			System.out.println("Angle2: " + ang2tmp);
			angle2 = Math.abs(angle2) + rotateAngle - 360.0;
			
			// Correction angle
			final double extra = 3.5;

			pilot.setRotateSpeed(fastRotateSpeed);
			log("Positioning robot perpendicular to the line.");
			pilot.rotate((angle2 / 2.0) - extra);

			double dist = 7.2 * Math.cos(Math.toRadians(angle2 / 2.0));
			
			pilot.travel(dist);
		}
	}
}
