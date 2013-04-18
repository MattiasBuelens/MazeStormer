package mazestormer.game;

import java.util.LinkedList;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.explore.Commander;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class LeaveIslandControlMode extends ControlMode {

private final ControlMode superControlMode;
private LinkedList<Tile> reachableSeesawQueue;
	
	public LeaveIslandControlMode(Player player, ControlMode superControlMode) {
		super(player, superControlMode.getCommander());
		this.superControlMode = superControlMode;
	}

	private ControlMode getSuperControlMode(){
		return superControlMode;
	}

	@Override
	public void takeControl(Driver driver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseControl(Driver driver) {
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

	@Override
	public BarcodeMapping getBarcodeMapping() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class LeaveIslandBarcodeMapping implements BarcodeMapping {

		@Override
		public IAction getAction(Barcode barcode) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
