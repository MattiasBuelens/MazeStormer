import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mazestormer.barcode.Barcode;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

/**
 * A class that contains the observed maze of the player that uses it, as well as the observed maze of his teammate. Contains methods to puzzle
 * the mazes together and translate coördinates.
 *
 */
public class MazeManager {
	
	private final Maze ownExploredMaze;
	private Map<Barcode, LongPoint> ownBarcodeMapping;
	
	private Maze teamMatesExploredMaze; //coordinates in others relative system
	//TODO: misschien loont het om ook dit doolhof in eigen coordinaten op te slaan, maar dan klopt het niet meer met zijn eigen player (dubbel bijhouden?)
	private Map<Barcode, LongPoint> teamMatesBarcodeMapping;
	
	private Maze totalExploredMaze; //coordinates in own relative system
	
	private Barcode[] commonBarcodes = {null, null};
	
	private AffineTransform affineTransformation;
		
	public MazeManager(Maze ownMaze){
		this.ownExploredMaze = ownMaze;
		this.teamMatesExploredMaze = null;
		this.affineTransformation = null;
	}
	
	/**
	 * Method that should be called when the own player has discovered a new tile.
	 * @param tile Tile that was discovered, with location in coordinates from the own relative system.
	 */
	public void ownPlayerDiscoveredTile(Tile tile){
		LongPoint position = tile.getPosition();
		Barcode barcode;
		
		//add tile to own explored maze
		//add tile to total explored maze, if not yet present
		
		if(tile.hasBarcode()){
			barcode = tile.getBarcode();
			
			//add barcode and location to ownBarcodeMapping
			ownBarcodeMapping.put(barcode, position);
			
			//act depending on the number of yet identified common barcodes
			if(teamMatesBarcodeMapping.containsKey(barcode)){
				if(commonBarcodes[0] == null) commonBarcodes[0] = barcode;
				else if(commonBarcodes[1] == null){
					commonBarcodes[1] = barcode;
					calculatePointTransformation();
				}
			}
		}
	}
	
	//TODO: methode voor als de teamgenoot een tegel die hij heeft ontdekt doorstuurt (oppassen voor coordinaten)
	
	/**
	 * Calculates an affine tranformation that transforms the coordinates of a point in the own system to coordinates in the others system. Saves
	 * this transformation to affineTransformation.
	 */
	// TODO: controleren of het niet juist omgekeerd is...
	private void calculatePointTransformation() {
		// genereer met behulp van de twee gemeenschappelijke barcodes vier LongPoints, die elk een tegel voorstellen in coördinaten van het
		// eigen assenstelsel of dat van je teamgenoot (TP is kort voor TilePosition)
		// Voor nauwkeurigste berekening: neem koppel barcodes met grootste afstand ertussen. Nauwkeurigheid is echter niet belangrijk: straks
		// afronden naar veelvoud van pi/2.
		LongPoint ownFirstTP = ownBarcodeMapping.get(commonBarcodes[0]);
		LongPoint ownSecondTP = ownBarcodeMapping.get(commonBarcodes[1]);
		LongPoint othersFirstTP = teamMatesBarcodeMapping.get(commonBarcodes[0]);
		LongPoint othersSecondTP = teamMatesBarcodeMapping.get(commonBarcodes[1]);

		// bereken rotatie van ander assenstelsel in eigen assenstelsel
		// \alpha_{X'} = \alpha_{21} - \alpha'_{21}
		double rotation = Math.atan2((ownSecondTP.getY() - ownFirstTP.getY()),
				(ownSecondTP.getX() - ownFirstTP.getX()))
				- Math.atan2((othersSecondTP.getY() - othersFirstTP.getY()),
						(othersSecondTP.getX() - othersFirstTP.getX()));

		// rond af naar veelvoud van pi/2
		rotation = Math.PI / 2 * (double) ((int) (rotation / Math.PI * 2));

		// bereken cosinus en sinus (behoren beide tot {-1, 0, 1}, maar AffineTransform vraag doubles als input voor zijn constuctor)
		double cosineRotation = Math.cos(rotation);
		double sineRotation = Math.sin(rotation);

		// bereken aan de hand van deze rotatie de coördinaten van de oorsprong van het assenstelsel van je teamgenoot in je eigen assenstelsel
		// x_{O'} = x_1 - x'_1*cos(\alpha_{X'}) + y'_1*sin(\alpha_{X'})
		// y_{O'} = y_1 - x'_1*sin(\alpha_{X'}) - y'_1*cos(\alpha_{X'})
		double biasX = ownFirstTP.getX() - othersFirstTP.getX()
				* cosineRotation + othersFirstTP.getY() * sineRotation;
		double biasY = ownFirstTP.getY() - othersFirstTP.getX() * sineRotation
				- othersFirstTP.getY() * cosineRotation;

		// stel met deze gegevens de matrix voor de AffineTransform op
		// [ x ] [ cos(\alpha_{X'}) - sin(\alpha_{X'}) ] [ x' ] [ x_{O'} ]
		// | | = | | * | | + | |
		// [ y ] [ sin(\alpha_{X'}) cos(\alpha_{X'}) ] [ y' ] [ y_{O'} ]
		double[] flatMatrix = { cosineRotation, sineRotation, -sineRotation,
				cosineRotation, biasX, biasY };
		// TODO: betere implementatie: eerst de identieke AffineTransform opstellen, daarna roteren en verschuiven

		// stel de affine transformatie op en sla ze op
		this.affineTransformation = new AffineTransform(flatMatrix);
	}
	
	/**
	 * Returns a new tile that has coördinates in the others system.
	 * 
	 * @param ownTile
	 *            A tile in own coördinates
	 */
	private Tile translateTileFromOwnToOther(Tile ownTile) {
		return ownTile;
		// van ownTile naar ownLongPoint
		// TODO: implementeren
		// affineTransform toepassen
		// TODO: implementeren
		// van othersLongPoint naar othersTile
		// TODO: implementeren
	}

	/**
	 * Returns a new tile that has coördinates in the own system.
	 * 
	 * @param othersTile
	 *            A tile that has coördinates in the teammates system.
	 */
	private Tile translateTileFromOtherToOwn(Tile othersTile) {
		return othersTile;
		// van othersTile naar othersLongPoint
		// TODO: implementeren
		// inverse affineTransform toepassen
		// TODO: implementeren
		// van ownLongPoint naar ownTile
		// TODO: implementeren
	}
	
	public Maze getOwnExploredMaze() {
		return ownExploredMaze;
	}

	public Maze getTeamMatesExploredMaze() {
		return teamMatesExploredMaze;
	}

	public Maze getTotalExploredMaze() {
		return totalExploredMaze;
	}
	
	//TODO: methodes maken om tegels te vertalen (tussen eigen en ander assenstelsel)
	
	//DEPRECATED:
	
	/**
	 * Returns an array of two barcodemappings. The first is from the own player, the second belongs to his teammate. Both mappings contain two
	 * key-value-pairs, corresponding to two barcodes both robots have found (common barcodes).
	 */
	private Map<Barcode, LongPoint>[] findTwoCommonBarcodes() {
		// genereer mappings van de gevonden barcodes naar hun tegelcoördinaten in het relatieve assenstelsel adhv de geëxploreerde doolhoven
		Map<Barcode, LongPoint> ownBarcodeMapping = getBarcodeToTilePositionMapping(ownExploredMaze);
		Map<Barcode, LongPoint> othersBarcodeMapping = getBarcodeToTilePositionMapping(teamMatesExploredMaze);

		// bepaal de gemeenschappelijke barcodes
		Barcode[] commonBarcodesArray = getTwoCommonBarcodes(ownBarcodeMapping, othersBarcodeMapping);
		if(commonBarcodesArray != null){
			Barcode firstCommonBarcode = commonBarcodesArray[0];
			Barcode secondCommonBarcode = commonBarcodesArray[1];
		}
		else{
			//TODO: Wat te doen in dit geval?
		}
		

		// genereer aan de hand van de gemeenschappelijke barcodes de twee nodige mappings.
		Map<Barcode, LongPoint> firstBarcodeMapping = new Hashtable<Barcode, LongPoint>();
		Map<Barcode, LongPoint> secondBarcodeMapping = new Hashtable<Barcode, LongPoint>();
		
		
		// TODO: dit implementeren

		return null;
	}

	/**
	 * Generates a mapping from barcodes to tile-positions from a given (explored) maze
	 */
	private Map<Barcode, LongPoint> getBarcodeToTilePositionMapping(Maze maze) {
		Iterator<Tile> it = maze.getTiles().iterator();
		Map<Barcode, LongPoint> mapping = new Hashtable<Barcode, LongPoint>();

		while(it.hasNext()){
			Tile tile = it.next();
			if(tile.hasBarcode()) mapping.put(tile.getBarcode(), tile.getPosition());			
		}

		return mapping;
	}
	
	/**
	 * Returns an array of two barcodes that both occur in the given barcodemappings. If there ar no two common barcodes, this method returns null.
	 */
	private Barcode[] getTwoCommonBarcodes( Map<Barcode, LongPoint> ownBarcodeMapping, Map<Barcode, LongPoint> othersBarcodeMapping) {
		Set<Barcode> ownBarcodes = ownBarcodeMapping.keySet();
		Set<Barcode> othersBarcodes = othersBarcodeMapping.keySet();
		Set<Barcode> commonBarcodes = ownBarcodes;
		
		commonBarcodes.retainAll(othersBarcodes);
		// TODO: Dit kan efficiënter! Er zijn maar twee gemeenschappelijke barcodes nodig.
		
		Iterator<Barcode> it = commonBarcodes.iterator();
		Barcode[] commonBarcodesArray = new Barcode[2];
		if(it.hasNext()) commonBarcodesArray[0] = it.next();
		else return null;
		if(it.hasNext()) commonBarcodesArray[1] = it.next();
		else return null;
		
		return commonBarcodesArray;
	}
}
