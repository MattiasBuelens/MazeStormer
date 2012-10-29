package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.Pilot;
import mazestormer.robot.RemoteSensorPort;

public class LineFinderController extends SubController implements
		ILineFinderController {

	// TODO Do not store reference here!
	LightSensor light;

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getPilot();
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			// TODO Retrieve from Connector!
			light = new LightSensor(RemoteSensorPort.get(0));
			light.setFloodlight(true);
		} else {
			light = null;
		}
	}

	@Override
	public int measureLightValue() {
		return light.getNormalizedLightValue();
	}

	private LineFinderRunner runner;

	@Override
	public void startSearching(int highLightValue, int lowLightValue) {
		runner = new LineFinderRunner(lowLightValue, highLightValue);
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

		@Override
		public void run() {
			final int threshold = 30;

			double slowRotateSpeed, fastRotateSpeed;
			slowRotateSpeed = 20;
			fastRotateSpeed = 50;

			double rotateAngle = 180.0;

			light.setLow(lowLightValue);
			light.setHigh(highLightValue);

			// TODO: Speed?
			// pilot.setTravelSpeed(5);

			// Start looking for line
			pilot.forward();

			int value;

			double angle;

			while (true) {
				value = light.readValue();
				if (value > threshold) {
					startRotating(true, slowRotateSpeed, fastRotateSpeed,
							rotateAngle);
					break;
				}

			}

			while (true) {
				value = light.readValue();
				if (value > threshold) {
					startRotating(false, slowRotateSpeed, fastRotateSpeed,
							rotateAngle);
					break;
				}

			}

			while (true) {
				value = light.readValue();
				if (value > threshold) {
					pilot.stop();
					angle = pilot.getMovement().getAngleTurned();
					break;
				}

			}

			angle = Math.abs(angle) + rotateAngle;

			final double extra = 3.0;

			pilot.setRotateSpeed(fastRotateSpeed);
			pilot.rotate((angle / 2.0) - extra);

			double dist = 7.2 * Math.cos(Math.toRadians(angle / 2.0));
			pilot.travel(dist);
		}

		private void startRotating(boolean goingLeft, double slowRotateSpeed,
				double fastRotateSpeed, double rotateAngle) {
			pilot.stop();
			pilot.setRotateSpeed(fastRotateSpeed);

			if (goingLeft)
				pilot.rotate(rotateAngle, false);
			else
				pilot.rotate(-rotateAngle, false);

			pilot.setRotateSpeed(slowRotateSpeed);

			if (goingLeft)
				pilot.rotateLeft();
			else
				pilot.rotateRight();
		}

	}
}
