package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.Robot;
import mazestormer.util.Future;

public class SeesawAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		// testen of de wip van deze kant op te rijden is, aan de hand daarvan een andere methode aanroepen
		return null;
	}

	/**
	 * @pre robot staat voor de wip aan de neergelaten kant, hij kijkt naar de wip
	 * @post robot staat op een tegel achter de tegel achter de wip, in het midden, en kijkt weg van de wip (tegel achter de wip bevat een
	 * 			andere barcode). alle informatie over de gepasseerde tegels staat in de observedMaze. de eerste tegel, de tegels van de
	 * 			wip en de tegel na de wip staan niet meer in de queue
	 * @param player
	 */
	private void ridableAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
//		1) rijd vooruit tot aan een bruin-zwart overgang (van de barcode aan de andere kant van de wip)
//		2) informatie over wip aan het ontdekte doolhof toevoegen
//		3) rijd vooruit tot 20 cm over een witte lijn (= eerste bruin-wit overgang)
//		4) verwijder eventueel tegels uit de queue
	}
	
	/**
	 * @pre robot staat voor de wip aan de opgelaten kant, hij kijkt naar de wip
	 * @post robot staan op de tegel voor de tegel voor de wip, in het midden, en kijkt weg van de wip (tegel voor de wip bevat de barcode)
	 * 			alle informatie over de tegel voor de wip, de tegels van de wip en de tegel achter de wip is toegevoegd aan de observedMaze. geen
	 * 			van die tegels staat nog in de queue
	 * @param player
	 */
	private void notRidableAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
//		1) informatie over wip aan het ontdekte doolhof toevoegen
//		2) 180° omdraaien
//		3) rijd vooruit tot 20 cm over een witte lijn
//		4) verwijder eventueel tegels uit de queue
	}
	
}
