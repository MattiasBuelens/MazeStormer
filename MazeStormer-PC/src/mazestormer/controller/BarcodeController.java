package mazestormer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.ActionType;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.Threshold;
import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class BarcodeController extends SubController implements IBarcodeController {
	private static final double START_BAR_LENGTH = 1.8; // [cm]
	private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int NUMBER_OF_BARS = 6; // without black start bars

	private static final int BLACK_THRESHOLD = 50;

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

	private ActionRunner actionRunner;
	private BarcodeRunner barcodeRunner;

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	private void postState(EventType eventType) {
		postEvent(new ActionEvent(eventType));
	}

	@Override
	public void startAction(ActionType actionType) {
		this.actionRunner = new ActionRunner(getAction(actionType));
		this.actionRunner.start();
	}

	@Override
	public void stopAction() {
		if (this.actionRunner != null) {
			this.actionRunner.stop();
			this.actionRunner = null;
		}
	}

	private static IAction getAction(ActionType actionType) {
		return (actionType != null) ? actionType.build() : new NoAction();
	}

	private class ActionRunner implements Runnable {
		private final Robot robot;
		private boolean isRunning = false;
		private IAction action;

		public ActionRunner(IAction action) {
			this.robot = getRobot();
			this.action = action;
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				this.isRunning = false;
				this.robot.getPilot().stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		@Override
		public void run() {
			this.action.performAction(this.robot);
			stop();
		}

	}

	@Override
	public void startScan() {
		this.barcodeRunner = new BarcodeRunner();
		this.barcodeRunner.start();
	}

	@Override
	public void stopScan() {
		if (this.barcodeRunner != null) {
			this.barcodeRunner.stop();
			this.barcodeRunner = null;
		}
	}

	private double scanTravelSpeed = 2; // [cm/sec]

	public double getScantravelSpeed() {
		return this.scanTravelSpeed;
	}

	@Override
	public void setScanSpeed(double speed) {
		this.scanTravelSpeed = speed;
	}

	private class BarcodeRunner implements Runnable {

		private final Pilot pilot;
		private final CalibratedLightSensor light;
		private boolean isRunning = false;
		private CommandHandle handle;

		private double originalTravelSpeed;

		public BarcodeRunner() {
			this.pilot = getRobot().getPilot();
			this.light = getRobot().getLightSensor();
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.SCAN_STARTED);
		}

		public void stop() {
			if (isRunning()) {
				this.isRunning = false;
				this.handle.cancel();
				this.pilot.stop();
				postState(EventType.SCAN_STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		private void onBlack(final Runnable action) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
			this.handle = getRobot().when(condition).stop().run(action).build();
		}

		@Override
		public void run() {
			this.originalTravelSpeed = this.pilot.getTravelSpeed();
			this.light.setFloodlight(true);
			this.pilot.forward();
			log("Start looking for black line.");
			onBlack(new Runnable() {
				@Override
				public void run() {
					onBlackBackward();
				}
			});
		}

		private Pose oldPose;
		private Pose newPose;
		private boolean blackToWhite;
		private List<Float> distances = new ArrayList<Float>();
		private byte barcode;

		private void onBlackBackward() {
			log("Go to the begin of the barcode zone.");
			this.pilot.setTravelSpeed(getScantravelSpeed());
			this.pilot.travel(-START_BAR_LENGTH / 2, false);
			this.oldPose = getRobot().getPoseProvider().getPose();
			this.blackToWhite = true;

			this.pilot.forward();
			loop();
		}

		private void loop() {
			if (this.blackToWhite) {
				onTrespassBW();
			} else {
				onTrespassWB();
			}
		}

		private void onTrespassBW() {
			onTrespassNewWhite(new Runnable() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassWB() {
			onTrespassNewBlack(new Runnable() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassNewBlack(final Runnable action) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_SMALLER_THAN,
					Threshold.WHITE_BLACK.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onTrespassNewWhite(final Runnable action) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_GREATER_THAN,
					Threshold.BLACK_WHITE.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onChange() {
			this.newPose = getRobot().getPoseProvider().getPose();
			this.distances.add(getPoseDiff(oldPose, newPose));
			this.oldPose = newPose;
			this.blackToWhite = !blackToWhite;

			if (getTotalSum(this.distances) <= (NUMBER_OF_BARS + 1) * BAR_LENGTH) {
				loop();
			} else {
				this.pilot.stop();
				this.pilot.setTravelSpeed(this.originalTravelSpeed);
				encodeBarcode();
				decodeBarcode();
			}
		}

		private void encodeBarcode() {
			this.barcode = (byte) readBarcode(distances);
			log("Scanned barcode: " + Integer.toBinaryString(this.barcode));
		}

		private void decodeBarcode() {
			// BarcodeDecoder.getAction(this.barcode).performAction(getRobot());
		}
	}

	private static float getPoseDiff(Pose one, Pose two) {
		/*
		 * TODO @Matthias Perhaps we just need:
		 * one.getLocation().distance(two.getLocation()) ?
		 */
		float diffX = Math.abs(one.getX() - two.getX());
		float diffY = Math.abs(one.getY() - two.getY());
		return Math.max(diffX, diffY);
	}

	private static float getTotalSum(Iterable<Float> values) {
		float sum = 0;
		for (float value : values) {
			sum += value;
		}
		return sum;
	}

	private static int readBarcode(List<Float> distances) {
		int result = 0;
		int index = NUMBER_OF_BARS - 1;
		// Iterate over distances until barcode complete
		ListIterator<Float> it = distances.listIterator();
		while (it.hasNext() && index >= 0) {
			int i = it.nextIndex();
			float distance = it.next();
			int at;
			if (i == 0) {
				// First bar
				at = (int) Math.max((distance - START_BAR_LENGTH) / BAR_LENGTH, 0);
			} else {
				// Other bars
				at = (int) Math.max(distance / BAR_LENGTH, 1);
			}
			// Odd indices are white, even indices are white
			int barBit = i & 1; // == i % 2
			// Set bit from index to index-a
			for (int j = 0; j < at && index >= 0; j++) {
				result |= barBit << index;
				index--;
			}
		}
		return result;
	}

}
