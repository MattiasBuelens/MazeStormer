package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;

public interface IAction{
	
	/**
	 * 
	 * @param	robot
	 * 			The robot that must perform this action.
	 * @throws 	IllegalStateException
	 * 			The given robot must be a valid robot.
	 * 			| robot != null
	 */
	public abstract void performAction(ControllableRobot robot, Maze maze);
}
