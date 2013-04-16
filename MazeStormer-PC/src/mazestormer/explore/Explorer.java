package mazestormer.explore;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.ExplorerBarcodeMapping;
import mazestormer.player.Player;

public class Explorer extends Commander {

	private final ControlMode exploreMode;
	private final ControlMode finishMode;

	public Explorer(Player player) {
		super(player);

		// Modes
		BarcodeMapping mapping = new ExplorerBarcodeMapping();
		exploreMode = new ExploreControlMode(player, this);
		finishMode = new FinishControlMode(player, this);

		setStartMode(exploreMode);
		bind(exploreMode, finishMode);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
	}

}
