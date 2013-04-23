package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.command.game.GameRunner;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinder;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.util.Future;
import mazestormer.util.state.AbstractStateListener;
import mazestormer.util.state.State;
import mazestormer.util.state.StateMachine;

public class SeesawAction extends StateMachine<SeesawAction, SeesawAction.SeesawState> implements IAction {

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
		// TODO vraag aan IRSensor (matthias)
		if (seesawOpen) {
			transition(SeesawState.ONWARDS);
		} else {
			// TODO opvragen of we moeten hervatten of wait and scan?
			transition(SeesawState.RESUME_EXPLORING);
		}
	}

	/**
	 * <ol>
	 * <li>rijd vooruit tot aan een bruin-zwart overgang (van de barcode aan de
	 * andere kant van de wip)</li>
	 * <li>informatie over wip aan het ontdekte doolhof toevoegen</li>
	 * <li>rijd vooruit tot 20 cm over een witte lijn (= eerste bruin-wit
	 * overgang)</li>
	 * <li>verwijder eventueel tegels uit de queue</li>
	 * </ol>
	 * 
	 * @pre robot staat voor de wip aan de neergelaten kant, hij kijkt naar de
	 *      wip
	 * @post robot staat op een tegel achter de tegel achter de wip, in het
	 *       midden, en kijkt weg van de wip (tegel achter de wip bevat een
	 *       andere barcode). alle informatie over de gepasseerde tegels staat
	 *       in de observedMaze. de eerste tegel, de tegels van de wip en de
	 *       tegel na de wip staan niet meer in de queue
	 */
	protected void onwards() {
		// TODO Implement seesaw action
		getGameRunner().onSeesaw(barcode);
		bindTransition(getPilot().travelComplete(130), // TODO 130 juist?
				SeesawState.FIND_LINE);
	}

	protected void findLine() {
		LineFinder lineFinder = new LineFinder(getControllableRobot()) {
			@Override
			protected void log(String message) {
				// log indien nodig
			}
		};

		@SuppressWarnings("unused")
		LineAdjuster lineAdjuster = new LineAdjuster(player, lineFinder);
		lineFinder.addStateListener(new LineFinderListener());
	}

	private class LineFinderListener extends AbstractStateListener<LineFinder.LineFinderState> {
		@Override
		public void stateFinished() {
			transition(SeesawState.RESUME_EXPLORING);
		}
	}

	protected void waitAndScan() {
		boolean seesawOpen = false;
		// TODO vraag aan IRSensor (matthias)
		if (seesawOpen)
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

	/**
	 * <ol>
	 * <li>informatie over wip aan het ontdekte doolhof toevoegen</li>
	 * <li>180° omdraaien</li>
	 * <li>rijd vooruit tot 20 cm over een witte lijn</li>
	 * <li>verwijder eventueel tegels uit de queue</li>
	 * </ol>
	 * 
	 * @pre robot staat voor de wip aan de opgelaten kant, hij kijkt naar de wip
	 * @post robot staan op de tegel voor de tegel voor de wip, in het midden,
	 *       en kijkt weg van de wip (tegel voor de wip bevat de barcode) alle
	 *       informatie over de tegel voor de wip, de tegels van de wip en de
	 *       tegel achter de wip is toegevoegd aan de observedMaze. geen van die
	 *       tegels staat nog in de queue
	 */
	protected void resumeExploring() {
		// TODO Implement seesaw action
		stop(); // stops this seesaw action
	}

	public enum SeesawState implements State<SeesawAction, SeesawAction.SeesawState> {

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

	private class FinishFuture extends StateMachine.FinishFuture<SeesawState> {

		@Override
		public boolean isFinished() {
			return getGameRunner().isRunning();
		}

	}

}
