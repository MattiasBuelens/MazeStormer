package mazestormer.polygon;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.atomic.AtomicInteger;

import mazestormer.controller.IPolygonControlController.Direction;
import mazestormer.robot.Pilot;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;

public class PolygonRunner extends
		StateMachine<PolygonRunner, PolygonRunner.PolygonState> implements
		StateListener<PolygonRunner.PolygonState> {

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

	public PolygonRunner(Pilot pilot, int nbSides, double sideLength,
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
	private void next() {
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
	private void travel() {
		bindTransition(pilot.travelComplete(sideLength), PolygonState.ROTATE);
	}

	/**
	 * Rotate on corner.
	 */
	private void rotate() {
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

	public enum PolygonState implements State<PolygonRunner, PolygonState> {
		NEXT {
			@Override
			public void execute(PolygonRunner runner) {
				runner.next();
			}
		},
		TRAVEL {
			@Override
			public void execute(PolygonRunner runner) {
				runner.travel();
			}
		},
		ROTATE {
			@Override
			public void execute(PolygonRunner runner) {
				runner.rotate();
			}
		};
	}

}