package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Robot;
import mazestormer.util.Future;

public class ObjectFoundAction implements IAction {
	
	private int foundObjectNumber;
	private int foundTeamNumber;
	
	public ObjectFoundAction(int fon, int ftn) {
		setFoundObjectNumber(fon);
		setFoundTeamNumber(ftn);
	}
	
	public int getFoundObjectNumber() {
		return foundObjectNumber;
	}

	private void setFoundObjectNumber(int foundObjectNumber) {
		this.foundObjectNumber = foundObjectNumber;
	}

	public int getFoundTeamNumber() {
		return foundTeamNumber;
	}

	private void setFoundTeamNumber(int foundTeamNumber) {
		this.foundTeamNumber = foundTeamNumber;
	}

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		// testen of dit het eigen voorwerp of andermans voorwerp is, aan de hand daarvan een andere methode aanroepen
		
		return null;
	}
	
	/**
	 * @pre Robot staat op de barcode en kijkt naar het voorwerp
	 * @post Robot staat in het midden van deze tegel en kijkt weg van de voorwerp tegel. Hij heeft het voorwerp mee. De huidige tegel en de
	 * 			voorwerp-tegel staan meer in de queue. de observedMaze is aangevuld met de info over deze twee tegels.
	 * @param player
	 */
	private void ownObjectAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
//		1) vooruit rijden tot de robot met zijn draai-as boven de volgende witte lijn hangt
//		2) 180° draaien
//		3) een bepaalde afstand achteruit rijden (22 cm = zijde tegel - diameter wc-rolletje - afstand klitteband-wielas
//																	+ extra voor aanduwen wc-rolletje = 40 - 4.5 - 13.7 + 0.3) 
//		4) de huidige tegel (dus die waar het rolletje op staat) is een U-tegel met de opening vóór de robot. deze info
//					toevoegen aan doolhof en de tegel verwijderen uit de queue
//		5) vooruit rijden tot 20 cm over de witte lijn
//		6) voeg teaminformatie toe
	}
	
	/**
	 * @pre Robot staat op de barcode en kijkt naar het voorwerp (van iemand anders)
	 * @post Robot staat in het midden van deze tegel en kijkt weg van de voorwerp tegel. De huidige tegel en de
	 * 			voorwerp-tegel staan niet in de queue. de observedMaze is aangevuld met de info over deze twee tegels.
	 * @param player
	 */
	private void otherObjectAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
//		1) 180° draaien
//		2) de tegel achter de robot is een U-tegel met de opening naar de robot toe. deze info toevoegen aan doolhof en de tegel
//				eventueel verwijderen uit de queue
//		3) vooruit rijden tot 20 cm over een witte lijn
	}
	
	

}
