package mazestormer.maze;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

/**
 * A class that contains the observed maze of the player that uses it, as well
 * as the observed maze of his teammate. Contains methods to puzzle the mazes
 * together and translate coördinates.
 * 
 */
public class CombinedMaze implements IMaze {

	private final Maze ownExploredMaze;
	private Map<Barcode, LongPoint> ownBarcodeMapping;

	private Maze teamMatesExploredMaze; // coordinates in others relative system

	private Map<Barcode, LongPoint> teamMatesBarcodeMapping;

	private Maze totalExploredMaze; // coordinates in own relative system

	private Barcode[] twoCommonBarcodes = { null, null };

	private AffineTransform affineTransformation = null;
	private int rotationsFromOwnToOther;

	public CombinedMaze() {
		this.ownExploredMaze = new Maze();
		this.totalExploredMaze = new Maze();
		// TODO verbetering: lijn hierboven: totalExploredMaze initialiseren op
		// een kopie van ownExploredMaze
		this.teamMatesExploredMaze = new Maze();
		this.affineTransformation = null;
	}

	/**
	 * Put a copy of the given tile in the teamMatesExploredMaze (or updates it
	 * if it was already present) and creates/updates the corresponding tile in
	 * the totalExploredMaze (if it wasn't already present).
	 * 
	 * @param othersTile
	 *            A tile in the others coordinate system.
	 */
	public void addTeamMateTile(Tile othersSentTile) {
		LongPoint position = othersSentTile.getPosition();

		// copy to teamMatesExploredMaze
		teamMatesExploredMaze.updateTile(othersSentTile);

		if (affineTransformation != null) {
			addTeamMateTileToTotalMaze(othersSentTile);
		} else if (othersSentTile.hasBarcode()) {
			Barcode barcode = othersSentTile.getBarcode();

			teamMatesBarcodeMapping.put(barcode, position);

			// check op barcode en eventueel: voeg mazes samen
			if (ownBarcodeMapping.containsKey(barcode)) {
				if (twoCommonBarcodes[0] == null)
					twoCommonBarcodes[0] = barcode;
				else if (twoCommonBarcodes[1] == null) {
					twoCommonBarcodes[1] = barcode;
					calculatePointTransformation();
					// using the transformation, put all tiles of the
					// othersExploredMaze in the totalExploredMaze
					mergeTotalAndOthersExploredMaze();
				}
			}
		}
	}

	/**
	 * Creates/updates the corresponding tile in the totalExploredMaze.
	 */
	private void addTeamMateTileToTotalMaze(Tile othersSentTile) {
		// Transform position to totalExploredMaze
		LongPoint position = getCorrespondingPositionFromOtherToTotal(othersSentTile.getPosition());

		// Place the rotated tile in the totalExploredMaze
		getTotalExploredMaze().updateTile(position, -rotationsFromOwnToOther, othersSentTile);
	}

	/**
	 * Calculates an affine tranformation that transforms the coordinates of a
	 * point in the own system to coordinates in the others system. Saves this
	 * transformation to affineTransformation.
	 */
	// TODO test: controleren of het niet juist omgekeerd is...
	private void calculatePointTransformation() {
		// genereer met behulp van de twee gemeenschappelijke barcodes vier
		// LongPoints, die elk een tegel voorstellen in coördinaten van het
		// eigen assenstelsel of dat van je teamgenoot (TP is kort voor
		// TilePosition)
		LongPoint ownFirstTP = ownBarcodeMapping.get(twoCommonBarcodes[0]);
		LongPoint ownSecondTP = ownBarcodeMapping.get(twoCommonBarcodes[1]);
		LongPoint othersFirstTP = teamMatesBarcodeMapping.get(twoCommonBarcodes[0]);
		LongPoint othersSecondTP = teamMatesBarcodeMapping.get(twoCommonBarcodes[1]);

		// bereken rotatie van ander assenstelsel in eigen assenstelsel
		// \alpha_{X'} = \alpha_{21} - \alpha'_{21}
		double rotation = Math
				.atan2((ownSecondTP.getY() - ownFirstTP.getY()), (ownSecondTP.getX() - ownFirstTP.getX()))
				- Math.atan2((othersSecondTP.getY() - othersFirstTP.getY()),
						(othersSecondTP.getX() - othersFirstTP.getX()));

		rotationsFromOwnToOther = (int) (rotation / Math.PI * 2);

		// rond af naar veelvoud van pi/2
		rotation = Math.PI / 2 * (double) (rotationsFromOwnToOther);

		// bereken cosinus en sinus (behoren beide tot {-1, 0, 1}, maar
		// AffineTransform vraag doubles als input voor zijn constuctor)
		double cosineRotation = Math.cos(rotation);
		double sineRotation = Math.sin(rotation);

		// bereken aan de hand van deze rotatie de coördinaten van de oorsprong
		// van het assenstelsel van je teamgenoot in je eigen assenstelsel
		// x_{O'} = x_1 - x'_1*cos(\alpha_{X'}) + y'_1*sin(\alpha_{X'})
		// y_{O'} = y_1 - x'_1*sin(\alpha_{X'}) - y'_1*cos(\alpha_{X'})
		double biasX = ownFirstTP.getX() - othersFirstTP.getX() * cosineRotation + othersFirstTP.getY() * sineRotation;
		double biasY = ownFirstTP.getY() - othersFirstTP.getX() * sineRotation - othersFirstTP.getY() * cosineRotation;

		// stel met deze gegevens de matrix voor de AffineTransform op
		// [ x ] [ cos(\alpha_{X'}) - sin(\alpha_{X'}) ] [ x' ] [ x_{O'} ]
		// | | = | | * | | + | |
		// [ y ] [ sin(\alpha_{X'}) cos(\alpha_{X'}) ] [ y' ] [ y_{O'} ]
		double[] flatMatrix = { cosineRotation, sineRotation, -sineRotation, cosineRotation, biasX, biasY };
		// TODO verbetering: eerst de identieke AffineTransform opstellen,
		// daarna roteren en verschuiven

		// stel de affine transformatie op en sla ze op
		this.affineTransformation = new AffineTransform(flatMatrix);
	}

	private void mergeTotalAndOthersExploredMaze() {
		for (Tile tile : teamMatesExploredMaze.getTiles()) {
			addTeamMateTileToTotalMaze(tile);
		}
	}

	/**
	 * Returns the affineTransform, which is null if it not yet calcultated.
	 */
	private AffineTransform getPointTransform() {
		return affineTransformation;
	}

	/**
	 * Returns the corresponding tile in the others explored maze (has
	 * coördinates in the others system).
	 * 
	 * @param ownTile
	 *            A tile in own coördinates
	 */
	@SuppressWarnings("unused")
	private Tile getCorrespondingTileFromOwnToOther(Tile ownTile) {
		// van ownTile naar ownLongPoint
		LongPoint ownPosition = ownTile.getPosition();
		// affineTransform toepassen
		LongPoint othersPosition = null;
		getPointTransform().transform(ownPosition, othersPosition);
		// van othersLongPoint naar othersTile
		return teamMatesExploredMaze.getTileAt(othersPosition);
	}

	/**
	 * Returns the corresponding tile in the own explored maze (has coördinates
	 * in the own system).
	 * 
	 * @param othersTile
	 *            A tile that has coördinates in the teammates system.
	 */
	@SuppressWarnings("unused")
	private Tile getCorrespondingTileFromOtherToOwn(Tile othersTile) {
		// van othersTile naar othersLongPoint
		LongPoint othersPosition = othersTile.getPosition();
		// inverse affineTransform toepassen
		LongPoint ownPosition = null;
		try {
			getPointTransform().inverseTransform(othersPosition, ownPosition);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		// van ownLongPoint naar ownTile
		return ownExploredMaze.getTileAt(ownPosition);
	}

	/**
	 * Returns the corresponding position in the total explored maze (has
	 * coördinates in the own system).
	 * 
	 * @param othersPosition
	 *            A position in the teammates system.
	 */
	private LongPoint getCorrespondingPositionFromOtherToTotal(LongPoint othersPosition) {
		// inverse affineTransform toepassen
		LongPoint ownPosition = null;
		try {
			getPointTransform().inverseTransform(othersPosition, ownPosition);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return ownPosition;
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

	@Override
	public float getTileSize() {
		return getTotalExploredMaze().getTileSize();
	}

	@Override
	public float getEdgeSize() {
		return getTotalExploredMaze().getEdgeSize();
	}

	@Override
	public float getBarLength() {
		return getTotalExploredMaze().getBarLength();
	}

	@Override
	public Pose getOrigin() {
		return getTotalExploredMaze().getOrigin();
	}

	@Override
	public void setOrigin(Pose origin) {
		getTotalExploredMaze().setOrigin(origin);
		ownExploredMaze.setOrigin(origin);
	}

	@Override
	public Mesh getMesh() {
		return getTotalExploredMaze().getMesh();
	}

	@Override
	public long getMinX() {
		return getTotalExploredMaze().getMinX();
	}

	@Override
	public long getMaxX() {
		return getTotalExploredMaze().getMaxX();
	}

	@Override
	public long getMinY() {
		return getTotalExploredMaze().getMinY();
	}

	@Override
	public long getMaxY() {
		return getTotalExploredMaze().getMaxY();
	}

	@Override
	public Tile getTileAt(LongPoint tilePosition) {
		return getTotalExploredMaze().getTileAt(tilePosition);
	}

	@Override
	public Tile getTileAt(Point2D tilePosition) {
		return getTotalExploredMaze().getTileAt(tilePosition);
	}

	@Override
	public Tile getNeighbor(Tile tile, Orientation direction) {
		return getTotalExploredMaze().getNeighbor(tile, direction);
	}

	@Override
	public Tile getOrCreateNeighbor(Tile tile, Orientation direction) {
		return getTotalExploredMaze().getOrCreateNeighbor(tile, direction);
	}

	@Override
	public Collection<Tile> getTiles() {
		return getTotalExploredMaze().getTiles();
	}

	@Override
	public int getNumberOfTiles() {
		return getTotalExploredMaze().getNumberOfTiles();
	}

	@Override
	public void updateTile(Tile tile) {
		updateTile(tile.getPosition(), 0, tile);
	}

	@Override
	public void updateTile(LongPoint tilePosition, int nbRotations, Tile tile) {
		getTotalExploredMaze().updateTile(tilePosition, nbRotations, tile);
		ownExploredMaze.updateTile(tilePosition, nbRotations, tile);
	}

	@Override
	public void updateTiles(Tile... tiles) {
		updateTiles(Arrays.asList(tiles));
	}

	@Override
	public void updateTiles(Iterable<Tile> tiles) {
		getTotalExploredMaze().updateTiles(tiles);
		ownExploredMaze.updateTiles(tiles);
	}

	@Override
	public void setEdge(LongPoint tilePosition, Orientation orientation, EdgeType type) {
		getTotalExploredMaze().setEdge(tilePosition, orientation, type);
		ownExploredMaze.setEdge(tilePosition, orientation, type);
	}

	@Override
	public void setBarcode(LongPoint position, Barcode barcode) throws IllegalStateException {
		getTotalExploredMaze().setBarcode(position, barcode);
		ownExploredMaze.setBarcode(position, barcode);
		ownBarcodeMapping.put(barcode, position);

		if (teamMatesBarcodeMapping.containsKey(barcode)) {
			if (twoCommonBarcodes[0] == null)
				twoCommonBarcodes[0] = barcode;
			else if (twoCommonBarcodes[1] == null) {
				twoCommonBarcodes[1] = barcode;
				calculatePointTransformation();
				// using the transformation, put all tiles of the
				// othersExploredMaze in the totalExploredMaze
				mergeTotalAndOthersExploredMaze();
			}
		}
	}

	@Override
	public void setBarcode(LongPoint position, byte barcode) throws IllegalStateException {
		getTotalExploredMaze().setBarcode(position, barcode);
		ownExploredMaze.setBarcode(position, barcode);
		// uses setBarcode(LongPoint position, Barcode barcode), so no need to
		// add it to the mapping
	}

	@Override
	public Tile getBarcodeTile(Barcode barcode) {
		return getTotalExploredMaze().getBarcodeTile(barcode);
	}

	@Override
	public Tile getBarcodeTile(byte barcode) {
		return getTotalExploredMaze().getBarcodeTile(barcode);
	}

	@Override
	public void setExplored(LongPoint position) {
		getTotalExploredMaze().setExplored(position);
		ownExploredMaze.setExplored(position);
	}

	@Override
	public void clear() {
		getTotalExploredMaze().clear();
		ownExploredMaze.clear();
		teamMatesExploredMaze.clear();
	}

	@Override
	public void addListener(MazeListener listener) {
		ownExploredMaze.addListener(listener);
	}

	@Override
	public void removeListener(MazeListener listener) {
		ownExploredMaze.removeListener(listener);
	}

	@Override
	public Point toAbsolute(Point relativePosition) {
		return ownExploredMaze.toAbsolute(relativePosition);
	}

	@Override
	public float toAbsolute(float relativeHeading) {
		return ownExploredMaze.toAbsolute(relativeHeading);
	}

	@Override
	public Pose toAbsolute(Pose relativePose) {
		return ownExploredMaze.toAbsolute(relativePose);
	}

	@Override
	public Point toRelative(Point absolutePosition) {
		return ownExploredMaze.toRelative(absolutePosition);
	}

	@Override
	public float toRelative(float absoluteHeading) {
		return ownExploredMaze.toRelative(absoluteHeading);
	}

	@Override
	public Pose toRelative(Pose absolutePose) {
		return ownExploredMaze.toRelative(absolutePose);
	}

	@Override
	public Point toTile(Point relativePosition) {
		return ownExploredMaze.toTile(relativePosition);
	}

	@Override
	public Point fromTile(Point tilePosition) {
		return ownExploredMaze.fromTile(tilePosition);
	}

	@Override
	public Collection<Line> getEdgeLines() {
		return getTotalExploredMaze().getEdgeLines();
	}

	@Override
	public Line getEdgeLine(Edge edge) {
		return getTotalExploredMaze().getEdgeLine(edge);
	}

	@Override
	public Rectangle2D getEdgeBounds(Edge edge) {
		return getTotalExploredMaze().getEdgeBounds(edge);
	}

	@Override
	public List<Rectangle2D> getBarcodeBars(Tile tile) {
		return getTotalExploredMaze().getBarcodeBars(tile);
	}

	@Override
	public Tile getTarget(Target target) {
		return ownExploredMaze.getTarget(target);
	}

	@Override
	public void setTarget(Target target, Tile tile) {
		ownExploredMaze.setTarget(target, tile);
	}

	@Override
	public Pose getStartPose(int playerNumber) {
		return ownExploredMaze.getStartPose(playerNumber);
	}

	@Override
	public void setStartPose(int playerNumber, Pose pose) {
		ownExploredMaze.setStartPose(playerNumber, pose);
	}

	@Override
	public void setStartPose(int playerNumber, LongPoint tilePosition, Orientation orientation) {
		ownExploredMaze.setStartPose(playerNumber, tilePosition, orientation);
	}

	@Override
	public Tile getSeesawTile(Barcode barcode) {
		return getTotalExploredMaze().getSeesawTile(barcode);
	}

}
