package mazestormer.command;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.IAction;
import mazestormer.maze.Tile;

/**
 * Each objective can be devided in a number of phases. These are implemented in ControleMode. ControlModes
 * differ from each other in two ways: the logic to find the next tile and the actions that have
 * to be executed when the robot goes over a certain barcode.
 */
public interface IControlMode {

	public Tile nextTile();
	
	public IAction getAction(Barcode barcode);
	
}
