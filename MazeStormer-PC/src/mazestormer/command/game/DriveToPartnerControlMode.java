package mazestormer.command.game;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.IAction;
import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class DriveToPartnerControlMode extends ControlMode {

	/*
	 * Constructor
	 */

	public DriveToPartnerControlMode(Player player, Commander commander) {
		super(player, commander);
	}
	
	/*
	 * ControlMode Management
	 */

	@Override
	public void takeControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseControl() {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Driver support
	 */

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

	@Override
	public IAction getAction(Barcode barcode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Utilities
	 */

}
