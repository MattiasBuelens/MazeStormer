package mazestormer.command.explore;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.ExplorerBarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.command.AbstractExploreControlMode;
import mazestormer.command.Commander;
import mazestormer.player.Player;

public class ExploreControlMode extends AbstractExploreControlMode {
	
	/*
	 * Data
	 */

	private static final BarcodeMapping mapping = new ExplorerBarcodeMapping();

	/*
	 * Constructor
	 */
	
	public ExploreControlMode(Player player, Commander commander) {
		super(player, commander);
	}
	
	/*
	 * Driver support
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return mapping.getAction(barcode);
	}

}
