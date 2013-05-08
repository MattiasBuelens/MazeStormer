package mazestormer.line;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;

import mazestormer.barcode.Threshold;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.player.Player;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.Future;

public class LineFinder extends StateMachine<LineFinder, LineFinder.LineFinderState> implements
		StateListener<LineFinder.LineFinderState> {

	/*
	 * Constants
	 */

	private final static double slowRotateSpeed = 30;
	private final static double fastRotateSpeed = 50;
	private final static double slowTravelSpeed = 1.0;
	private final static double fastTravelSpeed = 5.0;

	private final static double maxAttackAngle = 20.0;
	private final static double safetyAngle = 10.0;
	private final static double fastRotateAngle = -(90 - maxAttackAngle - safetyAngle);

	/*
	 * Settings
	 */

	private final Player player;
	private double originalTravelSpeed;
	private double originalRotateSpeed;

	/*
	 * State
	 */

	private volatile double lineWidth;

	public LineFinder(Player player) {
		this.player = checkNotNull(player);
		addStateListener(this);
	}

	protected void log(String message) {
		player.getLogger().log(Level.FINE, message);
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	private Pilot getPilot() {
		return getRobot().getPilot();
	}

	private Future<Void> onLine() {
		Condition condition = new LightCompareCondition(ConditionType.LIGHT_GREATER_THAN,
				Threshold.WHITE_BROWN.getThresholdValue());
		return getRobot().when(condition).stop().build();
	}

	private Future<Void> offLine() {
		Condition condition = new LightCompareCondition(ConditionType.LIGHT_SMALLER_THAN,
				Threshold.WHITE_BROWN.getThresholdValue());
		return getRobot().when(condition).stop().build();
	}

	protected void findLineStart() {
		// Save original speeds
		originalTravelSpeed = getPilot().getTravelSpeed();
		originalRotateSpeed = getPilot().getRotateSpeed();

		// Travel forward until on line
		log("Start looking for line.");
		getPilot().setTravelSpeed(fastTravelSpeed);
		getPilot().forward();
		bindTransition(onLine(), LineFinderState.FIND_LINE_END);
	}

	protected void findLineEnd() {
		log("On line, start looking for end of line.");

		// Travel forward until off line
		getPilot().setTravelSpeed(slowTravelSpeed);
		getPilot().forward();

		bindTransition(offLine(), LineFinderState.ROTATE_CENTER);
	}

	protected void rotateCenter() {
		log("Off line, positioning robot on line edge.");

		lineWidth = getPilot().getMovement().getDistanceTraveled();
		double centerOffset = ControllableRobot.sensorOffset - getRobot().getLightSensor().getSensorRadius();
		log("Line width: " + lineWidth);
		log("Offset from center: " + centerOffset);

		// Travel forward to center robot on end of line
		getPilot().setTravelSpeed(fastTravelSpeed);
		bindTransition(getPilot().travelComplete(centerOffset), LineFinderState.ROTATE_FIXED);
	}

	protected void rotateFixed() {
		// Rotate fixed angle
		getPilot().setRotateSpeed(fastRotateSpeed);
		bindTransition(getPilot().rotateComplete(fastRotateAngle), LineFinderState.ROTATE_UNTIL_LINE);
	}

	protected void rotateUntilLine() {
		// Rotate until on line again
		log("Start looking for line again.");
		getPilot().setRotateSpeed(slowRotateSpeed);
		// getPilot().rotateRight();

		bindTransition(onLine(), LineFinderState.POSITION_PERPENDICULAR);
		bindTransition(getPilot().rotateComplete(-360), LineFinderState.FAILED);
	}

	protected void positionPerpendicular() {
		log("On line, rotating robot perpendicular to line.");

		// Get sensor angle
		double sensorAngle = getSensorAngle();
		log("Angle adjusting for sensor radius: " + sensorAngle);

		// Position perpendicular to line
		double angle = 90d + sensorAngle;
		getPilot().setRotateSpeed(fastRotateSpeed);
		bindTransition(getPilot().rotateComplete(angle), LineFinderState.POSITION_CENTER);
	}

	protected void positionCenter() {
		// Position robot center on center of line
		log("Positioning on center of line.");
		double offset = -lineWidth / 2;
		bindTransition(getPilot().travelComplete(offset), LineFinderState.FINISH);
	}

	protected void failed() {
		log("Failed to find line.");
		// TODO Go back and try again?
		stop();
	}

	/**
	 * Get the angle needed to account for the sensor radius.
	 * 
	 * <p>
	 * The sensor radius causes the robot to rotate further than the actual edge
	 * of the line. This is adjusted by rotating by an extra angle.
	 * </p>
	 * 
	 * <p>
	 * This angle corresponds to the central angle ({@code alpha}) of a secant
	 * line in a circle. The circle has a radius given by
	 * {@link ControllableRobot#sensorOffset} ({@code so}) and the secant line
	 * has a length given by {@link CalibratedLightSensor#getSensorRadius()} (
	 * {@code sr}). <br/>
	 * The length of a secant line in a circle given its central angle is:
	 * {@code sr = 2*so*sin(alpha/2)}
	 * </p>
	 * 
	 * <p>
	 * Therefore: {@code alpha = 2*asin(sr/(2*so))}
	 * </p>
	 */
	private double getSensorAngle() {
		return 2 * Math.asin(getRobot().getLightSensor().getSensorRadius() / (2 * ControllableRobot.sensorOffset));
	}

	@Override
	public void stateStarted() {
		// Start
		transition(LineFinderState.FIND_LINE_START);
	}

	@Override
	public void stateStopped() {
		// Stop pilot
		getPilot().stop();
		// Restore original speeds
		getPilot().setTravelSpeed(originalTravelSpeed);
		getPilot().setRotateSpeed(originalRotateSpeed);
	}

	@Override
	public void stateFinished() {
		// Stop
		stop();
	}

	@Override
	public void statePaused(LineFinderState currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(LineFinderState currentState) {
	}

	@Override
	public void stateTransitioned(LineFinderState nextState) {
	}

	public enum LineFinderState implements State<LineFinder, LineFinderState> {
		FIND_LINE_START {
			@Override
			public void execute(LineFinder finder) {
				finder.findLineStart();
			}
		},
		FIND_LINE_END {
			@Override
			public void execute(LineFinder finder) {
				finder.findLineEnd();
			}
		},
		ROTATE_CENTER {
			@Override
			public void execute(LineFinder finder) {
				finder.rotateCenter();
			}
		},
		ROTATE_FIXED {
			@Override
			public void execute(LineFinder finder) {
				finder.rotateFixed();
			}
		},
		ROTATE_UNTIL_LINE {
			@Override
			public void execute(LineFinder finder) {
				finder.rotateUntilLine();
			}
		},
		POSITION_PERPENDICULAR {
			@Override
			public void execute(LineFinder finder) {
				finder.positionPerpendicular();
			}
		},
		POSITION_CENTER {
			@Override
			public void execute(LineFinder finder) {
				finder.positionCenter();
			}
		},
		FINISH {
			@Override
			public void execute(LineFinder finder) {
				finder.finish();
			}
		},
		FAILED {
			@Override
			public void execute(LineFinder finder) {
				finder.failed();
			}
		};
	}

}