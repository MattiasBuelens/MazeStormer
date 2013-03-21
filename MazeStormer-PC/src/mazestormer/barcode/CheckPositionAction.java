package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Robot;
import mazestormer.util.Future;

public class CheckPositionAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		// TODO Auto-generated method stub
		return null;
	}

}
