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
import mazestormer.robot.Robot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerTask;

public class BarcodeController extends SubController implements IBarcodeController {
	private static final double START_BAR_LENGTH = 1.8; // [cm]
	private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int NUMBER_OF_BARS = 6; // without black start bars
	private static final int BLACK_THRESHOLD = 50;

	private ActionRunner actionRunner;
	private BarcodeRunner runner;

	private double scanTravelSpeed = 2; // [cm/sec]

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

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
		this.runner = new BarcodeRunner();
		this.runner.start();
	}

	@Override
	public void stopScan() {
		if (this.runner != null) {
			this.runner.cancel();
			this.runner = null;
		}
	}

	@Override
	public double getScanSpeed() {
		return this.scanTravelSpeed;
	}

	@Override
	public void setScanSpeed(double speed) {
		this.scanTravelSpeed = speed;
	}

	private class BarcodeRunner extends Runner {

		private final CalibratedLightSensor light;
		private CommandHandle handle;

		private double originalTravelSpeed;

		private Pose oldPose;
		private Pose newPose;
		private boolean blackToWhite;
		private List<Float> distances = new ArrayList<Float>();
		private byte barcode;

		public BarcodeRunner() {
			super(getRobot().getPilot());
			this.light = getRobot().getLightSensor();
		}

		public void onStarted() {
			super.onStarted();

			// Post state
			postState(EventType.SCAN_STARTED);
		}

		public void onCancelled() {
			super.onCancelled();

			// Cancel condition handle
			if (this.handle != null)
				this.handle.cancel();

			// Restore original speed
			getPilot().setTravelSpeed(this.originalTravelSpeed);

			// Post state
			postState(EventType.SCAN_STOPPED);
		}

		private void onBlack(final RunnerTask task) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
			this.handle = getRobot().when(condition).stop().run(wrap(task)).build();
		}

		@Override
		public void run() {
			this.originalTravelSpeed = getTravelSpeed();
			this.light.setFloodlight(true);
			forward();
			log("Start looking for black line.");
			onBlack(new RunnerTask() {
				@Override
				public void run() {
					onBlackBackward();
				}
			});
		}

		private void onBlackBackward() {
			log("Go to the begin of the barcode zone.");
			setTravelSpeed(getScanSpeed());
			travel(-START_BAR_LENGTH / 2);

			this.oldPose = getRobot().getPoseProvider().getPose();
			this.blackToWhite = true;

			forward();
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
			onTrespassNewWhite(new RunnerTask() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassWB() {
			onTrespassNewBlack(new RunnerTask() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassNewBlack(final RunnerTask task) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_SMALLER_THAN,
					Threshold.WHITE_BLACK.getThresholdValue());
			this.handle = getRobot().when(condition).run(wrap(task)).build();
		}

		private void onTrespassNewWhite(final RunnerTask task) {
			Condition condition = new LightCompareCondition(ConditionType.LIGHT_GREATER_THAN,
					Threshold.BLACK_WHITE.getThresholdValue());
			this.handle = getRobot().when(condition).run(wrap(task)).build();
		}

		private void onChange() {
			this.newPose = getRobot().getPoseProvider().getPose();
			this.distances.add(getPoseDiff(this.oldPose, this.newPose));
			this.oldPose = this.newPose;
			this.blackToWhite = !this.blackToWhite;

			if (getTotalSum(this.distances) <= (NUMBER_OF_BARS + 1) * BAR_LENGTH) {
				// Iterate
				loop();
			} else {
				// Done
				cancel();
				// Read barcode
				encodeBarcode();
				decodeBarcode();
			}
		}

		private void encodeBarcode() {
			this.barcode = (byte) readBarcode(this.distances);
			log("Scanned barcode: " + Integer.toBinaryString(this.barcode));
		}

		private void decodeBarcode() {
			//TODO
			// BarcodeDecoder.getAction(this.barcode).performAction(getRobot());
		}
	}

	private static float getPoseDiff(Pose one, Pose two) {
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
			// Odd indices are white, even indices are black
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
