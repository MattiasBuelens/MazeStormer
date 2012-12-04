package mazestormer.barcode;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import lejos.robotics.navigation.Pose;
import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerTask;

import com.google.common.base.Strings;
import com.google.common.math.DoubleMath;

public class BarcodeRunner extends Runner {

	// private static final double START_BAR_LENGTH = 1.8; // [cm]
	// private static final double BAR_LENGTH = 1.85; // [cm]
	static final int NUMBER_OF_BARS = 6; // without black start bars
	static final int BLACK_THRESHOLD = 50;

	static final float NOISE_LENGTH = 0.65f;

	private final Robot robot;
	private final Maze maze;

	private double scanSpeed = 2; // cm/sec
	private double originalTravelSpeed;

	private CommandHandle handle;
	private double startOffset;
	private Pose oldPose;
	private Pose newPose;
	private boolean blackToWhite;
	private List<Float> distances = new ArrayList<Float>();

	public BarcodeRunner(Robot robot, Maze maze) {
		super(robot.getPilot());
		this.robot = robot;
		this.maze = maze;

		this.startOffset = robot.getLightSensor().getSensorRadius();
	}

	protected Robot getRobot() {
		return robot;
	}

	protected Maze getMaze() {
		return maze;
	}

	public double getScanSpeed() {
		return scanSpeed;
	}

	public void setScanSpeed(double scanSpeed) {
		this.scanSpeed = scanSpeed;
	}

	protected Pose getPose() {
		return getRobot().getPoseProvider().getPose();
	}

	protected double getBarLength() {
		return getMaze().getBarLength();
	}

	protected void log(String message) {
		System.out.println(message);
	}

	/**
	 * Triggered when the start of a barcode was found.
	 */
	protected void onStartBarcode() {
	}

	/**
	 * Triggered when the barcode was successfully read.
	 * 
	 * <p>
	 * The default implementation logs the read barcode and performs the
	 * associated action.
	 * </p>
	 */
	protected void onEndBarcode(byte barcode) {
		// Log
		String paddedBarcode = Strings.padStart(
				Integer.toBinaryString(barcode), NUMBER_OF_BARS, '0');
		log("Scanned barcode: " + paddedBarcode);

		// Action
		performAction(barcode);
	}

	@Override
	public void onCancelled() {
		super.onCancelled();

		// Cancel condition handle
		if (handle != null)
			handle.cancel();

		// Restore original speed
		getPilot().setTravelSpeed(originalTravelSpeed);
	}

	private void onBlack(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
		handle = getRobot().when(condition).stop().run(wrap(task)).build();
	}

	@Override
	public void run() {
		originalTravelSpeed = getTravelSpeed();
		robot.getLightSensor().setFloodlight(true);
		log("Start looking for black line.");
		onBlack(new RunnerTask() {
			@Override
			public void run() {
				onBlackBackward();
			}
		});
	}

	private void onBlackBackward() {
		// Notify
		onStartBarcode();

		log("Go to the begin of the barcode zone.");
		setTravelSpeed(getScanSpeed());
		// TODO Check with start offset
		// travel(- START_BAR_LENGTH / 2);
		travel(-startOffset);

		oldPose = getPose();
		blackToWhite = true;

		forward();
		loop();
	}

	private void loop() {
		if (blackToWhite) {
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
		handle = getRobot().when(condition).run(wrap(task)).build();
	}

	private void onTrespassNewWhite(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_GREATER_THAN,
				Threshold.BLACK_WHITE.getThresholdValue());
		handle = getRobot().when(condition).run(wrap(task)).build();
	}

	private void onChange() {
		newPose = getPose();
		float distance = getPoseDiff(this.oldPose, this.newPose);
		if (distance >= NOISE_LENGTH) {
			distances.add(distance);
			oldPose = this.newPose;
			blackToWhite = !this.blackToWhite;
		}

		if (getTotalSum(distances) <= (NUMBER_OF_BARS + 1) * getBarLength()) {
			// Iterate
			loop();
		} else {
			// Completed
			completed();
		}
	}

	private void completed() {
		// Read barcode
		byte barcode = (byte) readBarcode(distances);
		// Notify
		onEndBarcode(barcode);
		// Done
		cancel();
	}

	protected void performAction(byte barcode) {
		// TODO
		// BarcodeDecoder.getAction(this.barcode).performAction(getRobot(),
		// getMaze());
	}

	private int readBarcode(List<Float> distances) {
		final double barLength = getBarLength();
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
				// at = Math.max((distance - START_BAR_LENGTH) / barLength,
				// 0);
				at = Math.max((distance - startOffset - barLength) / barLength,
						0);
			} else {
				at = Math.max(distance / barLength, 1);
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