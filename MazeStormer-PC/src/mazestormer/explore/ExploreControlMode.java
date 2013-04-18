package mazestormer.explore;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.ExplorerBarcodeMapping;
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
