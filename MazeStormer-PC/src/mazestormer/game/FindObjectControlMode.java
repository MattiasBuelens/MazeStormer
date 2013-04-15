package mazestormer.game;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class FindObjectControlMode extends ControlMode{

	public FindObjectControlMode(Player player, BarcodeMapping barcodeMapping) {
		super(player, barcodeMapping);
		// TODO Auto-generated constructor stub
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

}
