package mazestormer.controller;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Strings;
import com.google.common.math.DoubleMath;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.ActionType;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.Threshold;
import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.maze.Maze;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Robot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerTask;

public class BarcodeController extends SubController implements
		IBarcodeController {
	// private static final double START_BAR_LENGTH = 1.8; // [cm]
	private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int NUMBER_OF_BARS = 6; // without black start bars
	private static final int BLACK_THRESHOLD = 50;

	private static final float NOISE_LENGTH = 0.65f;

	private ActionRunner actionRunner;
	private BarcodeRunner runner;

	private double scanTravelSpeed = 2; // [cm/sec]

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
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
			this.action.performAction(this.robot, getMaze());
			stop();
		}

	}

	@Override
	public void startScan() {
		this.runner = new BarcodeRunner();
		getRobot().getPilot().forward();
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

	@Override
	public int getWBThreshold() {
		return Threshold.WHITE_BLACK.getThresholdValue();
	}

	@Override
	public void setWBThreshold(int threshold) {
		Threshold.WHITE_BLACK.setThresholdValue(threshold);
	}

	@Override
	public int getBWThreshold() {
		return Threshold.BLACK_WHITE.getThresholdValue();
	}

	@Override
	public void setBWThreshold(int threshold) {
		Threshold.BLACK_WHITE.setThresholdValue(threshold);
	}

	private class BarcodeRunner extends Runner {

		private final CalibratedLightSensor light;
		private CommandHandle handle;

		private double startOffset;
		private double originalTravelSpeed;

		private Pose oldPose;
		private Pose newPose;
		private boolean blackToWhite;
		private List<Float> distances = new ArrayList<Float>();
		private byte barcode;

		public BarcodeRunner() {
			super(getRobot().getPilot());
			this.light = getRobot().getLightSensor();

			this.startOffset = light.getSensorRadius();
		}

		@Override
		public void onStarted() {
			super.onStarted();

			// Post state
			postState(EventType.SCAN_STARTED);
		}

		@Override
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
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
			this.handle = getRobot().when(condition).stop().run(wrap(task))
					.build();
		}

		@Override
		public void run() {
			this.originalTravelSpeed = getTravelSpeed();
			this.light.setFloodlight(true);
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
			// TODO Check with start offset
			// travel(- START_BAR_LENGTH / 2);
			travel(-startOffset);

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
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN,
					Threshold.WHITE_BLACK.getThresholdValue());
			this.handle = getRobot().when(condition).run(wrap(task)).build();
		}

		private void onTrespassNewWhite(final RunnerTask task) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_GREATER_THAN,
					Threshold.BLACK_WHITE.getThresholdValue());
			this.handle = getRobot().when(condition).run(wrap(task)).build();
		}

		private void onChange() {
			this.newPose = getRobot().getPoseProvider().getPose();
			float tempdis = getPoseDiff(this.oldPose, this.newPose);
			if (tempdis >= NOISE_LENGTH) {
				this.distances.add(tempdis);
				this.oldPose = this.newPose;
				this.blackToWhite = !this.blackToWhite;
			}

			if (getTotalSum(this.distances) <= (NUMBER_OF_BARS + 1)
					* BAR_LENGTH) {
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
			String paddedBarcode = Strings.padStart(
					Integer.toBinaryString(this.barcode), NUMBER_OF_BARS, '0');
			log("Scanned barcode: " + paddedBarcode);
		}

		private void decodeBarcode() {
			// TODO
			// BarcodeDecoder.getAction(this.barcode).performAction(getRobot(),
			// getMaze());
		}

		private int readBarcode(List<Float> distances) {
			int result = 0;
			int index = NUMBER_OF_BARS - 1;
			// Iterate over distances until barcode complete
			ListIterator<Float> it = distances.listIterator();
			while (it.hasNext() && index >= 0) {
				int i = it.nextIndex();
				float distance = it.next();
				double at;
				if (i == 0) {
					// First bar
					// TODO Check with start offset
					// at = Math.max((distance - START_BAR_LENGTH) / BAR_LENGTH,
					// 0);
					at = Math.max((distance - startOffset - BAR_LENGTH)
							/ BAR_LENGTH, 0);
				} else {
					at = Math.max(distance / BAR_LENGTH, 1);
				}
				// TODO Check rounding
				int limit = DoubleMath.roundToInt(at, RoundingMode.HALF_DOWN);
				// Odd indices are white, even indices are black
				int barBit = i & 1; // == i % 2
				// Set bit from index to index-a
				for (int j = 0; j < limit && index >= 0; j++) {
					result |= barBit << index;
					index--;
				}
			}
			return result;
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

}
