package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import lejos.robotics.navigation.Pose;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.game.player.Player;
import mazestormer.maze.IMaze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.state.State;
import mazestormer.util.state.StateListener;
import mazestormer.util.state.StateMachine;

import com.google.common.base.Strings;
import com.google.common.math.DoubleMath;

public class BarcodeScanner extends
		StateMachine<BarcodeScanner, BarcodeScanner.BarcodeState> implements
		StateListener<BarcodeScanner.BarcodeState>, BarcodeScannerListener {

	/*
	 * Constants
	 */

	// private static final double START_BAR_LENGTH = 1.8; // [cm]
	// private static final double BAR_LENGTH = 1.85; // [cm]
	private static final int BLACK_THRESHOLD = 50;
	private static final float NOISE_LENGTH = 0.65f;

	/*
	 * Settings
	 */

	private final Player player;
	private boolean performAction;
	private double scanSpeed = 2; // cm/sec
	private double originalTravelSpeed;
	private BarcodeMapping mapping = new ExplorerBarcodeMapping();

	/*
	 * State
	 */

	private volatile Pose strokeStart;
	private volatile Pose strokeEnd;
	private final List<Float> distances = new ArrayList<Float>();

	private final List<BarcodeScannerListener> listeners = new ArrayList<BarcodeScannerListener>();

	public BarcodeScanner(Player player) {
		this.player = checkNotNull(player);

		addBarcodeListener(this);
		addStateListener(this);
	}

	public ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	public IMaze getMaze() {
		return player.getMaze();
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

	protected float getStartOffset() {
		return getRobot().getLightSensor().getSensorRadius();
	}

	public BarcodeMapping getMapping() {
		return mapping;
	}

	public void setMapping(BarcodeMapping mapping) {
		this.mapping = mapping;
	}

	protected void log(String message) {
		System.out.println(message);
	}

	public void addBarcodeListener(BarcodeScannerListener listener) {
		listeners.add(listener);
	}

	public void removeBarcodeListener(BarcodeScannerListener listener) {
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
		return performAction(getMapping().getAction(barcode));
	}

	public Future<?> performAction(Barcode barcode) {
		return performAction(getMapping().getAction(barcode));
	}

	protected Future<?> performAction(IAction action) {
		return action.performAction(player);
	}

	private Future<Void> onFirstBlack() {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
		return getRobot().when(condition).stop().build();
	}

	private Future<Void> onWhiteToBlack() {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_SMALLER_THAN,
				Threshold.WHITE_BLACK.getThresholdValue());
		return getRobot().when(condition).build();
	}

	private Future<Void> onBlackToWhite() {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_GREATER_THAN,
				Threshold.BLACK_WHITE.getThresholdValue());
		return getRobot().when(condition).build();
	}

	protected void findStart() {
		// Save original speed
		originalTravelSpeed = getRobot().getPilot().getTravelSpeed();
		// Reset state
		strokeStart = null;
		strokeEnd = null;
		distances.clear();

		// Find first black line
		log("Start looking for black line.");
		bindTransition(onFirstBlack(), BarcodeState.GO_TO_START);
	}

	protected void goToStart() {
		// Notify listeners
		for (BarcodeScannerListener listener : listeners) {
			listener.onStartBarcode();
		}

		log("Go to the begin of the barcode zone.");
		getRobot().getPilot().setTravelSpeed(getScanSpeed());
		// TODO Check with start offset
		// travel(- START_BAR_LENGTH / 2);
		bindTransition(getRobot().getPilot().travelComplete(-getStartOffset()),
				BarcodeState.STROKE_START);
	}

	protected void strokeStart() {
		// At begin of barcode
		strokeStart = getPose();
		// Find white stroke
		transition(BarcodeState.FIND_STROKE_WHITE);
		// Go forward
		getRobot().getPilot().forward();
	}

	protected void findWhiteStroke() {
		bindTransition(onBlackToWhite(), BarcodeState.STROKE_WHITE);
	}

	protected void findBlackStroke() {
		bindTransition(onWhiteToBlack(), BarcodeState.STROKE_BLACK);
	}

	protected void stroke(boolean foundBlack) {
		// Get stroke width
		strokeEnd = getPose();
		float strokeWidth = getPoseDiff(strokeStart, strokeEnd);
		boolean nextStrokeBlack;

		if (strokeWidth >= NOISE_LENGTH) {
			// Store width
			distances.add(strokeWidth);
			// Set start of next stroke
			strokeStart = strokeEnd;
			// Find next stroke
			nextStrokeBlack = !foundBlack;
		} else {
			// Noise detected, retry same stroke
			nextStrokeBlack = foundBlack;
		}

		if (getTotalSum(distances) <= (Barcode.getNbBars() - 1)
				* getBarLength()) {
			// Iterate
			if (nextStrokeBlack) {
				transition(BarcodeState.FIND_STROKE_BLACK);
			} else {
				transition(BarcodeState.FIND_STROKE_WHITE);
			}
		} else {
			// Completed
			transition(BarcodeState.FINISH);
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
				at = Math.max((distance - getStartOffset() - barLength)
						/ barLength, 0);
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

	@Override
	public void stateStarted() {
		// Start
		transition(BarcodeState.FIND_START);
	}

	@Override
	public void stateStopped() {
		// Restore original speed
		getRobot().getPilot().setTravelSpeed(originalTravelSpeed);
	}

	@Override
	public void stateFinished() {
		// Reset
		stateStopped();
		// Read barcode
		byte barcode = (byte) readBarcode(distances);
		// Notify listeners
		for (BarcodeScannerListener listener : listeners) {
			listener.onEndBarcode(barcode);
		}
	}

	@Override
	public void statePaused(BarcodeState currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(BarcodeState currentState) {
	}

	@Override
	public void stateTransitioned(BarcodeState nextState) {
	}

	public enum BarcodeState implements State<BarcodeScanner, BarcodeState> {
		FIND_START {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.findStart();
			}
		},
		GO_TO_START {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.goToStart();
			}
		},
		STROKE_START {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.strokeStart();
			}
		},
		FIND_STROKE_BLACK {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.findBlackStroke();
			}
		},
		FIND_STROKE_WHITE {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.findWhiteStroke();
			}
		},
		STROKE_BLACK {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.stroke(true);
			}
		},
		STROKE_WHITE {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.stroke(false);
			}
		},
		FINISH {
			@Override
			public void execute(BarcodeScanner scanner) {
				scanner.finish();
			}
		}

	}

}
