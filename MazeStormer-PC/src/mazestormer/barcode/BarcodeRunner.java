package mazestormer.barcode;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import lejos.robotics.navigation.Pose;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerTask;
import mazestormer.util.Future;

import com.google.common.base.Strings;
import com.google.common.math.DoubleMath;

public class BarcodeRunner extends Runner implements BarcodeRunnerListener {

	// private static final double START_BAR_LENGTH = 1.8; // [cm]
	// private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int BLACK_THRESHOLD = 50;
	private static final float NOISE_LENGTH = 0.65f;

	private final ControllableRobot robot;
	private final Maze maze;
	private boolean performAction;
	private double scanSpeed = 2; // cm/sec
	private double originalTravelSpeed;

	private Future<Void> handle;
	private double startOffset;
	private Pose oldPose;
	private Pose newPose;
	private boolean blackToWhite;
	private List<Float> distances = new ArrayList<Float>();

	private final List<BarcodeRunnerListener> listeners = new ArrayList<BarcodeRunnerListener>();

	public BarcodeRunner(ControllableRobot robot, Maze maze) {
		super(robot.getPilot());
		this.robot = robot;
		this.maze = maze;

		this.startOffset = robot.getLightSensor().getSensorRadius();

		addBarcodeListener(this);
	}

	protected ControllableRobot getRobot() {
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

	public boolean performsAction() {
		return performAction;
	}

	public void setPerformAction(boolean performAction) {
		this.performAction = performAction;
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

	public void addBarcodeListener(BarcodeRunnerListener listener) {
		listeners.add(listener);
	}

	public void removeBarcodeListener(BarcodeRunnerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onStartBarcode() {
	}

	@Override
	public void onEndBarcode(byte barcode) {
		// Log
		String paddedBarcode = Strings.padStart(
				Integer.toBinaryString(barcode), Barcode.getNbValueBars(), '0');
		log("Scanned barcode: " + paddedBarcode);

		// Action
		if (performsAction()) {
			performAction(barcode);
		}
	}

	public Future<?> performAction(byte barcode) {
		return BarcodeDecoder.getAction(barcode).performAction(getRobot(), getMaze());
	}

	public Future<?> performAction(Barcode barcode) {
		return BarcodeDecoder.getAction(barcode).performAction(getRobot(), getMaze());
	}

	private void reset() {
		// Cancel condition handle
		if (handle != null)
			handle.cancel();

		// Restore original speed
		getPilot().setTravelSpeed(originalTravelSpeed);
	}

	@Override
	public void onCompleted() {
		super.onCompleted();
		reset();
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		reset();
	}

	private void onBlack(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
		handle = getRobot().when(condition).stop().run(prepare(task)).build();
		// handle = getRobot().when(condition).run(prepare(task)).build();
	}

	@Override
	public void run() {
		// Reset
		originalTravelSpeed = getTravelSpeed();
		distances.clear();
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
		// Notify listeners
		for (BarcodeRunnerListener listener : listeners) {
			listener.onStartBarcode();
		}
		throwWhenCancelled();

		log("Go to the begin of the barcode zone.");
		// stop();
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
		handle = getRobot().when(condition).run(prepare(task)).build();
	}

	private void onTrespassNewWhite(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_GREATER_THAN,
				Threshold.BLACK_WHITE.getThresholdValue());
		handle = getRobot().when(condition).run(prepare(task)).build();
	}

	private void onChange() {
		newPose = getPose();
		float distance = getPoseDiff(this.oldPose, this.newPose);
		if (distance >= NOISE_LENGTH) {
			distances.add(distance);
			oldPose = this.newPose;
			blackToWhite = !this.blackToWhite;
		}

		if (getTotalSum(distances) <= (Barcode.getNbBars() - 1)
				* getBarLength()) {
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
		// Done
		resolve();
		// Notify listeners
		for (BarcodeRunnerListener listener : listeners) {
			listener.onEndBarcode(barcode);
		}
	}

	private int readBarcode(List<Float> distances) {
		final double barLength = getBarLength();
		int result = 0;
		int index = Barcode.getNbValueBars() - 1;

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
