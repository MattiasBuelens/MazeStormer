package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.game.GameRunner;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinderRunner;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.state.AbstractStateListener;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.AbstractFuture;
import mazestormer.util.Future;

public class SeesawAction extends
		StateMachine<SeesawAction, SeesawAction.SeesawState> implements IAction {

	private final GameRunner gameRunner;
	private final int barcode;
	private Player player;

	public SeesawAction(GameRunner gameRunner, int barcode) {
		this.gameRunner = gameRunner;
		this.barcode = barcode;
	}

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		this.player = player;
		stop(); // indien nog niet gestopt.

		// Resolve when finished
		FinishFuture future = new FinishFuture();
		addStateListener(future);

		// Start from the initial state
		start();
		transition(SeesawState.INITIAL);

		return future;
	}

	private GameRunner getGameRunner() {
		return this.gameRunner;
	}

	private ControllableRobot getControllableRobot() {
		return (ControllableRobot) player.getRobot();
	}

	private Pilot getPilot() {
		return getControllableRobot().getPilot();
	}

	protected void initial() {
		getGameRunner().setSeesawWalls();
		transition(SeesawState.SCAN);
	}
	
	protected void scan() {
		boolean seesawOpen = false;
		//TODO vraag aan IRSensor (matthias)
		if(seesawOpen)
			transition(SeesawState.ONWARDS);
	}
	
	protected void onwards() {
		getGameRunner().onSeesaw(barcode);
		bindTransition(getPilot().travelComplete(130), // TODO 130 juist?
				SeesawState.FIND_LINE);
	}
	
	protected void findLine() {
		LineFinderRunner lineFinder = new LineFinderRunner(getControllableRobot()) {
			@Override
			protected void log(String message) {
				// log indien nodig
			}
		};

		@SuppressWarnings("unused")
		LineAdjuster lineAdjuster = new LineAdjuster(player, lineFinder);
		lineFinder.addStateListener(new LineFinderListener());
	}
	
	private class LineFinderListener extends AbstractStateListener<LineFinderRunner.LineFinderState> {
		@Override
		public void stateFinished() {
			transition(SeesawState.RESUME_EXPLORING);
		}
	}
	
	protected void waitAndScan() {
		boolean seesawOpen = false;
		//TODO vraag aan IRSensor (matthias)
		if(seesawOpen)
			transition(SeesawState.ONWARDS);
		else {
			try {
				Thread.sleep(5000);
				transition(SeesawState.WAIT_AND_SCAN);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void resumeExploring() {
		stop(); // stops this seesaw action
	}

	public enum SeesawState implements
			State<SeesawAction, SeesawAction.SeesawState> {

		INITIAL {
			
			@Override
			public void execute(SeesawAction input) {
				input.initial();
			}
		},
		
		SCAN {

			@Override
			public void execute(SeesawAction input) {
				input.scan();
			}
		},
		
		ONWARDS {

			@Override
			public void execute(SeesawAction input) {
				input.onwards();
			}
		},
		
		FIND_LINE {

			@Override
			public void execute(SeesawAction input) {
				input.findLine();
			}
			
		},
		
		WAIT_AND_SCAN {

			@Override
			public void execute(SeesawAction input) {
				input.waitAndScan();
			}
			
		},
		
		RESUME_EXPLORING {

			@Override
			public void execute(SeesawAction input) {
				input.resumeExploring();
			}
			
		};

	}

	private class FinishFuture extends AbstractFuture<Void> implements
			StateListener<SeesawState> {

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
		public void statePaused(SeesawState currentState,
				boolean onTransition) {
		}

		@Override
		public void stateResumed(SeesawState currentState) {
		}

		@Override
		public void stateTransitioned(SeesawState nextState) {
		}

	}

}
