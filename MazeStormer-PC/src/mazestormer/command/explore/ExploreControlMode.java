package mazestormer.command.explore;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.ExplorerBarcodeMapping;
import mazestormer.command.AbstractExploreControlMode;
import mazestormer.command.Commander;
import mazestormer.player.Player;

public class ExploreControlMode extends AbstractExploreControlMode {

	private static final BarcodeMapping mapping = new ExplorerBarcodeMapping();

	public ExploreControlMode(Player player, Commander commander) {
		super(player, commander);
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		return mapping;
	}

}
