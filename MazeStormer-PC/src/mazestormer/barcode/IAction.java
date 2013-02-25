package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public interface IAction {

	/**
	 * Execute this action.
	 * 
	 * @param	robot
	 * 			The robot that must perform this action.
	 * @return	A future which is resolved when the action is completed.
	 * @throws 	IllegalStateException
	 * 			If the given robot is not valid.
	 * 			| robot != null
	 */
	public abstract Future<?> performAction(ControllableRobot robot, Maze maze);

}
