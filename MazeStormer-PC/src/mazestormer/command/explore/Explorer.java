package mazestormer.command.explore;

import mazestormer.command.Commander;
import mazestormer.player.Player;

public class Explorer extends Commander {

	private final ExploreControlMode exploreMode;
	private final FinishControlMode finishMode;

	public Explorer(Player player) {
		super(player);

		// Modes
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
