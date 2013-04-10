package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import mazestormer.game.GameRunner;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinder;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.state.AbstractStateListener;
import mazestormer.state.State;
import mazestormer.state.StateMachine;
import mazestormer.util.Future;

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

	private IMaze getMaze() {
		return player.getMaze();
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

	protected void initial() {
		getGameRunner().setSeesawWalls();
		transition(SeesawState.SCAN);
	}

	protected void scan() {
		if (hasUnexploredTiles())
			transition(SeesawState.RESUME_EXPLORING);
		else if (isSeesawOpen())
			transition(SeesawState.ONWARDS);
		else {
			Collection<Tile> reachableSeesawTiles = reachableSeesawTiles();
			if (moreThanOneSeesaw() && !reachableSeesawTiles.isEmpty()) {
				// TODO repeatedly go to these seesaws, more states?
			} else {
				transition(SeesawState.WAIT_AND_SCAN);
			}
		}
	}

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
		if (isSeesawOpen())
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
		// TODO Implement seesaw action
		stop(); // stops this seesaw action
	}

	private boolean isSeesawOpen() {
		IRSensor ir = getControllableRobot().getIRSensor();
		return !ir.hasReading();
	}

	/**
	 * @return true if there are still tiles to explore without traversing a
	 *         seesaw.
	 */
	private boolean hasUnexploredTiles() {
		IMaze maze = getMaze();
		for (Tile tile : maze.getUnexploredTiles())
			if (!tile.belongsToSeesaw())
				return true;
		return false;
	}

	/**
	 * @return true if there are more than one seesaws discovered in the maze.
	 */
	private boolean moreThanOneSeesaw() {
		IMaze maze = getMaze();
		int nbOfSeesawTiles = 0;
		for (Tile tile : maze.getTiles())
			if (!tile.belongsToSeesaw())
				nbOfSeesawTiles++;
		return nbOfSeesawTiles / 4 > 1;
	}

	/**
	 * @return A collection with barcode tiles belonging to a seesaw, to which
	 *         you can go without crossing the seesaw you're currently standing
	 *         at.
	 */
	// Deze methode neemt nog niet in rekening dat een andere seesaw een kortste
	// pad heeft over de seesaw, maar ook een pad ernaast, hiervoor moet
	// pathfinder mogelijk een extra methode hebben.
	private Collection<Tile> reachableSeesawTiles() {

		Collection<Tile> tiles = new HashSet<>();
		Tile currentTile = getGameRunner().getCurrentTile();
		Barcode[] currentSeesaw = getGameRunner().getCurrentSeesawBarcodes();
		IMaze maze = getMaze();
		Collection<Tile> seesawTiles = maze.getSeesawTiles();
		PathFinder PF = new PathFinder(maze);

		for (Tile tile : seesawTiles) {
			if (tile.getSeesawBarcode().equals(currentSeesaw[0]) || tile.getSeesawBarcode().equals(currentSeesaw[1])) {
				continue;
			} else {
				List<Tile> path = PF.findTilePath(currentTile, tile);
				if (!containsSeesaw(path, currentSeesaw))
					tiles.add(tile);
			}
		}

		return tiles;
	}

	private boolean containsSeesaw(List<Tile> path, Barcode[] seesaw) {
		for (Tile tile : path) {
			if (tile.isSeesaw()
					&& (tile.getSeesawBarcode().equals(seesaw[0]) || tile.getSeesawBarcode().equals(seesaw[1]))) {
				return false;
			}
		}
		return true;
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
			return isRunning();
		}

	}

}
