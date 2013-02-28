package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class ObjectFoundAction implements IAction {

	@Override
	public Future<?> performAction(ControllableRobot robot, Maze observedMaze) {
		// TODO Auto-generated method stub
		// testen of dit het eigen voorwerp of andermans voorwerp is, aan de hand daarvan een andere methode aanroepen
		return null;
	}
	
	/**
	 * @pre Robot staat op de barcode en kijkt naar het voorwerp
	 * @post Robot staat in het midden van deze tegel en kijkt weg van de voorwerp tegel. Hij heeft het voorwerp mee. De huidige tegel en de
	 * 			voorwerp-tegel staan meer in de queue. de observedMaze is aangevuld met de info over deze twee tegels.
	 * @param robot
	 * @param observedMaze
	 */
	private void ownObjectAction(ControllableRobot robot, Maze observedMaze) {
//		1) vooruit rijden tot de robot met zijn draai-as boven de volgende witte lijn hangt
//		2) 180° draaien
//		3) een bepaalde afstand achteruit rijden (22 cm = zijde tegel - diameter wc-rolletje - afstand klitteband-wielas
//																	+ extra voor aanduwen wc-rolletje = 40 - 4.5 - 13.7 + 0.3) 
//		4) de huidige tegel (dus die waar het rolletje op staat) is een U-tegel met de opening vóór de robot. deze info
//					toevoegen aan doolhof en de tegel verwijderen uit de queue
//		5) vooruit rijden tot 20 cm over de witte lijn
	}
	
	/**
	 * @pre Robot staat op de barcode en kijkt naar het voorwerp (van iemand anders)
	 * @post Robot staat in het midden van deze tegel en kijkt weg van de voorwerp tegel. De huidige tegel en de
	 * 			voorwerp-tegel staan niet in de queue. de observedMaze is aangevuld met de info over deze twee tegels.
	 * @param robot
	 * @param observedMaze
	 */
	private void otherObjectAction(ControllableRobot robot, Maze observedMaze) {
//		1) 180° draaien
//		2) de tegel achter de robot is een U-tegel met de opening naar de robot toe. deze info toevoegen aan doolhof en de tegel
//				eventueel verwijderen uit de queue
//		3) vooruit rijden tot 20 cm over een witte lijn
	}
	
	

}
