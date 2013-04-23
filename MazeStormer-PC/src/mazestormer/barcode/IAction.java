package mazestormer.barcode;

import mazestormer.game.player.Player;
import mazestormer.util.Future;

public interface IAction {

	/**
	 * Execute this action.
	 * 
	 * @param	player
	 * 			The player that must perform this action.
	 * @return	A future which is resolved when the action is completed.
	 * @throws 	IllegalStateException
	 * 			If the given robot is not valid.
	 * 			| robot != null
	 */
	public abstract Future<?> performAction(Player player);

}
