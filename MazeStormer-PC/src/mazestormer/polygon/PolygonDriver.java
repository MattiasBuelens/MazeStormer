package mazestormer.polygon;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.atomic.AtomicInteger;

import mazestormer.controller.IPolygonControlController.Direction;
import mazestormer.robot.Pilot;
import mazestormer.util.state.State;
import mazestormer.util.state.StateListener;
import mazestormer.util.state.StateMachine;

public class PolygonDriver extends
		StateMachine<PolygonDriver, PolygonDriver.PolygonState> implements
		StateListener<PolygonDriver.PolygonState> {

	/*
	 * Settings
	 */

	private final Pilot pilot;
	private final int nbSides;
	private final double sideLength;
	private final double cornerAngle;

	/*
	 * State
	 */

	private final AtomicInteger remaining = new AtomicInteger();

	public PolygonDriver(Pilot pilot, int nbSides, double sideLength,
			Direction direction) {
		this.pilot = checkNotNull(pilot);
		this.nbSides = nbSides;
		this.sideLength = sideLength;
		this.cornerAngle = getCornerAngle(nbSides, direction);

		addStateListener(this);
	}

	/**
	 * Next side.
	 */
	protected void next() {
		if (remaining.getAndDecrement() > 0) {
			// Start traveling along side
			transition(PolygonState.TRAVEL);
		} else {
			// All sides traveled, done
			finish();
		}
	}

	/**
	 * Travel along side.
	 */
	protected void travel() {
		bindTransition(pilot.travelComplete(sideLength), PolygonState.ROTATE);
	}

	/**
	 * Rotate on corner.
	 */
	protected void rotate() {
		bindTransition(pilot.rotateComplete(cornerAngle), PolygonState.NEXT);
	}

	/**
	 * Calculate the corner angle for a given number of sides in a given
	 * direction.
	 * 
	 * @param nbSides
	 *            The number of sides.
	 * @param direction
	 *            The direction.
	 */
	private static double getCornerAngle(int nbSides, Direction direction) {
		double parity = (direction == Direction.ClockWise) ? -1d : 1d;
		return parity * 360d / (double) nbSides;
	}

	@Override
	public void stateStarted() {
		// Set counter
		this.remaining.set(nbSides);
		// Start
		transition(PolygonState.NEXT);
	}

	@Override
	public void stateStopped() {
		// Stop pilot
		pilot.stop();
	}

	@Override
	public void stateFinished() {
		// Stop
		stop();
	}

	@Override
	public void statePaused(PolygonState currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(PolygonState currentState) {
	}

	@Override
	public void stateTransitioned(PolygonState nextState) {
	}

	public enum PolygonState implements State<PolygonDriver, PolygonState> {
		NEXT {
			@Override
			public void execute(PolygonDriver driver) {
				driver.next();
			}
		},
		TRAVEL {
			@Override
			public void execute(PolygonDriver driver) {
				driver.travel();
			}
		},
		ROTATE {
			@Override
			public void execute(PolygonDriver driver) {
				driver.rotate();
			}
		};
	}

}