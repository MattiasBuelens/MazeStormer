package mazestormer.game;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.Commander;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class FindObjectControlMode extends ControlMode{

	public FindObjectControlMode(Player player, Commander commander) {
		super(player, commander);
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
	
	private class FindObjectBarcodeMapping implements BarcodeMapping{

		private static TeamTreasureTrekBarcodeMapping tttMapping = new TeamTreasureTrekBarcodeMapping(gameRunner)
		
		@Override
		public IAction getAction(Barcode barcode) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		// TODO
		return null;
	}

}
