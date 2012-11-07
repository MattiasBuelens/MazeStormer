package mazestormer.controller;

import mazestormer.connect.ConnectEvent;
import mazestormer.controller.LineFinderEvent.EventType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements ILineFinderController {

	private static final double sensorToWheels = 7.2;
	final static double rotateAngle = 135.0;
	
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
				getMainController().getLogger().warning("High light value is lower than low light value.");
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

			getLightSensor().setLow(lowLightValue);
			getLightSensor().setHigh(highLightValue);

			// TODO: Speed?
			pilot.setTravelSpeed(5);

			// Start looking for line
			log("Start looking for line.");
			pilot.forward();

			int value;

			double angle1, angle2;

			while (true) {
				value = getLightSensor().getLightValue();
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
				value = getLightSensor().getLightValue();
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
				value = getLightSensor().getLightValue();
				if (value > threshold) {
					pilot.stop();
					angle2 = pilot.getMovement().getAngleTurned();
					break;
				}

			}

			angle1 = Math.abs(angle1) + rotateAngle;
			angle2 = Math.abs(angle2) + rotateAngle;

			// Correction angle
			final double extra = 3.5;
			pilot.setRotateSpeed(fastRotateSpeed);

			double finalAngle;
			
			if (isCross(angle1,angle2)){
				log("Cross detected.");
				finalAngle = ((angle2 - angle1) / 2.0) - extra;
			}else{
				finalAngle = ((angle2 - 360.0) / 2.0) - extra;
			}

			log("Positioning robot perpendicular to the line.");
			
			pilot.rotate(finalAngle);
			double dist = sensorToWheels * Math.cos(Math.toRadians(finalAngle));
			pilot.travel(dist);

		}
		
		private final double crossTreshold = 3.0;
		private boolean isCross(double angle1, double angle2){
			double radAngle1 = Math.toRadians(angle1);
			double radAngle2 = Math.toRadians(angle2);
			
			double xp = 0;
			double yp = sensorToWheels;
			
			double x0 = -sensorToWheels*Math.sin(radAngle1);
			double y0 = sensorToWheels*Math.cos(radAngle1);
			
			double x1 = -sensorToWheels*Math.sin(radAngle1-radAngle2);
			double y1 = sensorToWheels*Math.cos(radAngle1-radAngle2);

			double lambda=((x1-x0)*(xp-x0)+(y1-y0)*(yp-y0))/
					(Math.pow((x1-x0),2)+Math.pow((y1-y0),2));
			
			double afstand=Math.sqrt(Math.pow(xp-x0-lambda*(x1-x0),2)+(Math.pow(yp-y0-lambda*(y1-y0),2)));
			
			return afstand > crossTreshold;
		}
	}
}
