package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;
import static com.google.common.base.Preconditions.*;

public class NoAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);

		return new ImmediateFuture<Void>(null);
	}
}
