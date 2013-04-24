package mazestormer.command.explore;

import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.player.Player;

public class Explorer extends Commander {
	
	/*
	 * ControlModes
	 */

	private final ExploreControlMode exploreMode;
	private final FinishControlMode finishMode;

	/*
	 * Constructor
	 */
	
	public Explorer(Player player) {
		super(player);

		// Modes
		exploreMode = new ExploreControlMode(player, this);
		finishMode = new FinishControlMode(player, this);

		setMode(exploreMode);
	}
	
	/*
	 * Objective Management
	 */

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
	}
	
	/*
	 * ControlMode Management
	 */

	@Override
	public ControlMode nextMode() {
		// the only transition in this objective is to finish-mode
		currentMode = finishMode;
		return finishMode;
	}

}
