package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.barcode.ObjectFoundAction.ObjectFoundState;
import mazestormer.explore.ExplorerRunner;
import mazestormer.game.GameRunner;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinderRunner;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
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
		// TODO
	}

	/**
	 * @pre robot staat voor de wip aan de neergelaten kant, hij kijkt naar de
	 *      wip
	 * @post robot staat op een tegel achter de tegel achter de wip, in het
	 *       midden, en kijkt weg van de wip (tegel achter de wip bevat een
	 *       andere barcode). alle informatie over de gepasseerde tegels staat
	 *       in de observedMaze. de eerste tegel, de tegels van de wip en de
	 *       tegel na de wip staan niet meer in de queue
	 * @param player
	 */
	private void ridableAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
		// 1) rijd vooruit tot aan een bruin-zwart overgang (van de barcode aan
		// de andere kant van de wip)
		// 2) informatie over wip aan het ontdekte doolhof toevoegen
		// 3) rijd vooruit tot 20 cm over een witte lijn (= eerste bruin-wit
		// overgang)
		// 4) verwijder eventueel tegels uit de queue
	}

	/**
	 * @pre robot staat voor de wip aan de opgelaten kant, hij kijkt naar de wip
	 * @post robot staan op de tegel voor de tegel voor de wip, in het midden,
	 *       en kijkt weg van de wip (tegel voor de wip bevat de barcode) alle
	 *       informatie over de tegel voor de wip, de tegels van de wip en de
	 *       tegel achter de wip is toegevoegd aan de observedMaze. geen van die
	 *       tegels staat nog in de queue
	 * @param player
	 */
	private void notRidableAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
		// 1) informatie over wip aan het ontdekte doolhof toevoegen
		// 2) 180° omdraaien
		// 3) rijd vooruit tot 20 cm over een witte lijn
		// 4) verwijder eventueel tegels uit de queue
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
			
		}

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
