package mazestormer.barcode;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.game.GameRunner;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.AbstractFuture;
import mazestormer.util.Future;

public class ObjectFoundAction extends
		StateMachine<ObjectFoundAction, ObjectFoundAction.ObjectFoundState>
		implements IAction {

	private final GameRunner gameRunner;
	private final int foundObjectNumber;
	private final int foundTeamNumber;
	private Player player;

	private static int threshold = 85;

	public ObjectFoundAction(int fon, int ftn, GameRunner gameRunner) {
		this.foundObjectNumber = fon;
		this.foundTeamNumber = ftn;
		this.gameRunner = gameRunner;
	}

	public int getFoundObjectNumber() {
		return foundObjectNumber;
	}

	public int getFoundTeamNumber() {
		return foundTeamNumber;
	}

	private GameRunner getGameRunner() {
		return gameRunner;
	}

	private ControllableRobot getControllableRobot() {
		return (ControllableRobot) player.getRobot();
	}

	private Pilot getPilot() {
		return getControllableRobot().getPilot();
	}

	private Future<Void> onLine() {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_GREATER_THAN, threshold);
		return getControllableRobot().when(condition).stop().build();
	}

	@Override
	public Future<?> performAction(Player player) {
		this.player = player;

		// Stop if still running
		stop();

		// Resolve when finished
		FinishFuture future = new FinishFuture();
		addStateListener(future);

		// Start from the initial state
		start();
		transition(ObjectFoundState.INITIAL);

		return future;
	}

	/**
	 * States
	 */

	protected void initial() {
		getGameRunner().setWallsOnNextTile();

		// Determine owner
		if (getGameRunner().getObjectNumber() == getFoundObjectNumber()) {
			transition(ObjectFoundState.OWN_OBJECT);
		} else {
			transition(ObjectFoundState.DONE);
		}

	}

	protected void ownObject() {
		// Publish
		getGameRunner().objectFound();
		// Forward until white line
		bindTransition(onLine(), ObjectFoundState.FIRST_LINE);
		getPilot().forward();
	}

	protected void firstLine() {
		// Turn 180°
		bindTransition(getPilot().rotateComplete(180),
				ObjectFoundState.BACKWARDS);
	}

	protected void backwards() {
		// Travel backwards
		bindTransition(getPilot().travelComplete(-28),
				ObjectFoundState.GOT_OBJECT);
	}

	protected void gotObject() {
		// Forward until white line
		bindTransition(onLine(), ObjectFoundState.SECOND_LINE);
		getPilot().forward();
	}

	protected void secondLine() {
		// Travel forward
		bindTransition(getPilot().travelComplete(26), ObjectFoundState.DONE);
	}

	protected void done() {
		// Prepare game runner
		getGameRunner().afterObjectBarcode();
		// Finished
		finish();
	}

	public enum ObjectFoundState implements
			State<ObjectFoundAction, ObjectFoundAction.ObjectFoundState> {
		INITIAL {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.initial();
			}

		},

		OWN_OBJECT {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.ownObject();
			}

		},

		FIRST_LINE {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.firstLine();
			}

		},

		BACKWARDS {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.backwards();
			}

		},

		GOT_OBJECT {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.gotObject();
			}

		},

		SECOND_LINE {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.secondLine();
			}

		},

		DONE {
			@Override
			public void execute(ObjectFoundAction objectFoundAction) {
				objectFoundAction.done();
			}

		};
	}

	private class FinishFuture extends AbstractFuture<Void> implements
			StateListener<ObjectFoundState> {

		@Override
		public void stateStarted() {
		}

		@Override
		public void stateStopped() {
			// Failed
			cancel();
		}

		@Override
		public void stateFinished() {
			// Success
			if (getGameRunner().isRunning()) {
				resolve(null);
			} else {
				cancel();
			}
		}

		@Override
		public void statePaused(ObjectFoundState currentState,
				boolean onTransition) {
		}

		@Override
		public void stateResumed(ObjectFoundState currentState) {
		}

		@Override
		public void stateTransitioned(ObjectFoundState nextState) {
		}

	}

}
