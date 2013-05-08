package mazestormer.barcode.action;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.state.State;
import mazestormer.state.StateMachine;
import mazestormer.util.Future;

public class ObjectFoundAction extends StateMachine<ObjectFoundAction, ObjectFoundAction.ObjectFoundState> implements
		IAction {

	private Player player;

	/**
	 * Normalized light value between white and brown
	 */
	private final static int threshold = 530;

	private ControllableRobot getControllableRobot() {
		return (ControllableRobot) player.getRobot();
	}

	private Pilot getPilot() {
		return getControllableRobot().getPilot();
	}

	private Future<Void> onLine() {
		Condition condition = new LightCompareCondition(ConditionType.LIGHT_GREATER_THAN, threshold);
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
		transition(ObjectFoundState.OWN_OBJECT);

		return future;
	}

	protected void ownObject() {
		// Forward until white line
		bindTransition(onLine(), ObjectFoundState.FIRST_LINE);
		getPilot().forward();
	}

	protected void firstLine() {
		// Turn 180°
		bindTransition(getPilot().rotateComplete(180), ObjectFoundState.BACKWARDS);
	}

	protected void backwards() {
		// Travel backwards
		bindTransition(getPilot().travelComplete(-28), ObjectFoundState.GOT_OBJECT);
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
		// Finished
		finish();
	}

	public enum ObjectFoundState implements State<ObjectFoundAction, ObjectFoundAction.ObjectFoundState> {
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

	private class FinishFuture extends StateMachine.FinishFuture<ObjectFoundState> {

		@Override
		public boolean isFinished() {
			return true;
		}

	}

}
