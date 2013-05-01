package mazestormer.barcode.action;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinder;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.state.AbstractStateListener;
import mazestormer.state.State;
import mazestormer.state.StateMachine;
import mazestormer.util.Future;

public class DriveOverSeesawAction extends
		StateMachine<DriveOverSeesawAction, DriveOverSeesawAction.SeesawState> implements IAction {

	private Player player;

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
		transition(SeesawState.ONWARDS);

		return future;
	}

	private ControllableRobot getControllableRobot() {
		return (ControllableRobot) player.getRobot();
	}

	private Pilot getPilot() {
		return getControllableRobot().getPilot();
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
		bindTransition(getPilot().travelComplete(122), // TODO 122 juist?
				SeesawState.FIND_LINE);
	}

	protected void findLine() {
		LineFinder lineFinder = new LineFinder(player) {
			@Override
			protected void log(String message) {
				super.log(message);
			}
		};

		LineAdjuster lineAdjuster = new LineAdjuster(player);
		lineAdjuster.bind(lineFinder);
		lineFinder.addStateListener(new LineFinderListener());
	}

	private class LineFinderListener extends
			AbstractStateListener<LineFinder.LineFinderState> {
		@Override
		public void stateFinished() {
			transition(SeesawState.RESUME_EXPLORING);
		}
	}

	protected void resumeExploring() {
		finish();
	}

	public enum SeesawState implements
			State<DriveOverSeesawAction, DriveOverSeesawAction.SeesawState> {

		ONWARDS {

			@Override
			public void execute(DriveOverSeesawAction input) {
				input.onwards();
			}
		},

		FIND_LINE {

			@Override
			public void execute(DriveOverSeesawAction input) {
				input.findLine();
			}

		},

		RESUME_EXPLORING {

			@Override
			public void execute(DriveOverSeesawAction input) {
				input.resumeExploring();
			}

		};

	}

	private class FinishFuture extends StateMachine.FinishFuture<SeesawState> {

		@Override
		public boolean isFinished() {
			return true;
		}
	}

}
