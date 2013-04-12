package mazestormer.command;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.ExplorerBarcodeMapping;
import mazestormer.controlMode.ControlMode;
import mazestormer.controlMode.ExploreControlMode;
import mazestormer.controlMode.FinishControlMode;
import mazestormer.player.CommandTools;

public class Explorer extends Commander {

	private final ControlMode exploreMode;
	private final ControlMode finishMode;

	public Explorer(CommandTools player) {
		super(player);

		// Modes
		BarcodeMapping mapping = new ExplorerBarcodeMapping();
		exploreMode = new ExploreControlMode(player, mapping);
		finishMode = new FinishControlMode(player, mapping);

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
