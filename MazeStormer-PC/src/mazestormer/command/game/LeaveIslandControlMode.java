package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mazestormer.barcode.AbstractSeesawAction;
import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.command.AbstractExploreControlMode.ClosestTileComparator;
import mazestormer.command.ControlMode;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllablePCRobot;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class LeaveIslandControlMode extends ControlMode {

	private final ControlMode superControlMode;
	private LinkedList<Tile> reachableSeesawQueue;
	private final BarcodeMapping leaveBarcodeMapping = new LeaveIslandBarcodeMapping();

	public LeaveIslandControlMode(Player player, ControlMode superControlMode) {
		super(player, superControlMode.getCommander());
		this.superControlMode = superControlMode;
	}

	/*
	 * Getters
	 */

	private ControlMode getSuperControlMode() {
		return superControlMode;
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getSuperControlMode().getCommander();
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	/*
	 * Methodes eigen voor deze controlmode
	 */

	@Override
	public void takeControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public Tile nextTile(Tile currentTile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean isOpen(float angle) {
		if (Float.isNaN(angle)) {
			return true;
		}
		return (Math.abs(angle) > ControllablePCRobot.STANDARD_IR_RANGE);
	}

	// TODO Call me maybe?
	protected void scan() {
		boolean seesawOpen = isOpen(getRobot().getIRSensor().getAngle());
		if (seesawOpen) {
			seesawOpen = isOpen(getRobot().getIRSensor().getAngle());
		}

		if (seesawOpen) {
			// TODO All good, cross the seesaw
		} else {
			// TODO Seesaw closed, what to do now?
		}
	}

	/*
	 * Barcode-acties en logica
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return leaveBarcodeMapping.getAction(barcode);
	}

	private class SeesawAction extends AbstractSeesawAction {

		private Barcode seesawBarcode;

		private SeesawAction(Barcode seesawBarcode) {
			super(LeaveIslandControlMode.this.getPlayer(), LeaveIslandControlMode.this.getCommander().getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// TODO Check whether we're trying to cross the seesaw?

			// Cross the seesaw if open
			if (canDriveOverSeesaw()) {
				return driveOverSeesaw();
			}

			// Try to go around seesaw
			List<Tile> pathAroundSeesaw = getPathWithoutSeesaws();
			if (!pathAroundSeesaw.isEmpty()) {
				return redirect(pathAroundSeesaw);
			}

			// Try to go over another seesaw
			Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
			List<Tile> pathWithoutSeesaw = getPathWithoutSeesaw(seesaw);
			if (!pathWithoutSeesaw.isEmpty()) {
				return redirect(pathWithoutSeesaw);
			}

			// TODO Train spotting
			return null;
		}
	}

	private class LeaveIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(11), new SeesawAction(new Barcode(11)));
				put(new Barcode(13), new SeesawAction(new Barcode(13)));
				put(new Barcode(15), new SeesawAction(new Barcode(15)));
				put(new Barcode(17), new SeesawAction(new Barcode(17)));
				put(new Barcode(19), new SeesawAction(new Barcode(19)));
				put(new Barcode(21), new SeesawAction(new Barcode(21)));
			}
		};

		@Override
		public IAction getAction(Barcode barcode) {
			if (barcodeMapping.containsKey(barcode)) {
				return barcodeMapping.get(barcode);
			}
			return new NoAction();
		}

	}
}
