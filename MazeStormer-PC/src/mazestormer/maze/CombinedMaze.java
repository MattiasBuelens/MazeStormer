package mazestormer.maze;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * as the observed maze of his partner. Contains methods to puzzle the mazes
 * together and translate coordinates.
 */
public class CombinedMaze implements IMaze {

	private final IMaze ownMaze; // tilecoordinates in own relative system
	private final Map<Barcode, LongPoint> ownBarcodeMapping = new HashMap<Barcode, LongPoint>();

	private IMaze partnerMaze; // tilecoordinates in partner's relative system
	private final Map<Barcode, LongPoint> partnerBarcodeMapping = new HashMap<Barcode, LongPoint>();
	private final TeamMateMazeListener partnerListener = new TeamMateMazeListener();

	private final IMaze totalMaze; // tilecoordinates in own relative system

	private final List<Barcode> twoCommonBarcodes = new ArrayList<Barcode>(2);

	private TileTransform tileTransformation = null;

	public CombinedMaze(IMaze ownExploredMaze) {
		this.ownMaze = ownExploredMaze;

		// Copy own maze into total maze
		this.totalMaze = new Maze();
		totalMaze.importTiles(ownExploredMaze.getTiles());

		this.partnerMaze = null;
		this.tileTransformation = null;

		setOriginToDefault();
	}

	public CombinedMaze() {
		this(new Maze());
	}

	/**
	 * Put a copy of the given tile in the partner's maze (or updates it if it
	 * was already present) and creates/updates the corresponding tile in the
	 * total maze (if it wasn't already present).
	 * 
	 * @param partnerTile
	 *            A tile in the partner's coordinate system.
	 */
	private void updatePartnerTile(Tile partnerTile) {
		LongPoint position = partnerTile.getPosition();

		if (getTileTransform() != null) {
			// Copy to total maze
			importPartnerTileIntoTotalMaze(partnerTile);
		} else if (partnerTile.hasBarcode()) {
			Barcode barcode = partnerTile.getBarcode();

			partnerBarcodeMapping.put(barcode, position);

			// check op barcode en indien genoeg barcodes gevonden: voeg mazes
			// samen
			if (ownBarcodeMapping.containsKey(barcode)) {
				addCommonBarcode(barcode);
			}
		}
	}

	/**
	 * Creates/updates the corresponding tile in the total maze.
	 */
	private void importPartnerTileIntoTotalMaze(Tile partnerTile) {
		getTotalMaze().importTile(partnerTile, getTileTransform());
	}

	/**
	 * Add a common barcode and merge the mazes if possible.
	 * 
	 * @param barcode
	 *            The newly found common barcode.
	 */
	private void addCommonBarcode(Barcode barcode) {
		if (twoCommonBarcodes.contains(barcode))
			return;
		if (twoCommonBarcodes.size() >= 2)
			return;

		// Add new common barcode
		twoCommonBarcodes.add(barcode);
		// Check if merge possible
		if (twoCommonBarcodes.size() == 2) {
			// Calculate transformation
			calculateTileTransformation();
			// Merge partner maze into the total maze using transformation
			mergeTotalAndPartnerMazes();
		}
	}

	/**
	 * Calculates a tile tranformation that transforms the tilecoordinates of a
	 * point in the own system to tilecoordinates in the partner's system. Saves
	 * this transformation to the field tileTransformation.
	 */
	// TODO test: controleren of het niet juist omgekeerd is...
	private void calculateTileTransformation() {
		// genereer met behulp van de twee gemeenschappelijke barcodes vier
		// LongPoints, die elk een tegel voorstellen in co�rdinaten van het
		// eigen assenstelsel of dat van je teamgenoot (TP is kort voor
		// TilePosition)
		LongPoint ownFirstTP = ownBarcodeMapping.get(twoCommonBarcodes.get(0));
		LongPoint ownSecondTP = ownBarcodeMapping.get(twoCommonBarcodes.get(1));
		LongPoint partnerFirstTP = partnerBarcodeMapping.get(twoCommonBarcodes.get(0));
		LongPoint partnerSecondTP = partnerBarcodeMapping.get(twoCommonBarcodes.get(1));

		// bereken rotatie van ander assenstelsel in eigen assenstelsel
		// \alpha_{X'} = \alpha_{21} - \alpha'_{21}
		double rotation = Math
				.atan2((ownSecondTP.getY() - ownFirstTP.getY()), (ownSecondTP.getX() - ownFirstTP.getX()))
				- Math.atan2((partnerSecondTP.getY() - partnerFirstTP.getY()),
						(partnerSecondTP.getX() - partnerFirstTP.getX()));

		int rotationsFromOwnToPartner = (int) (rotation / Math.PI * 2);

		// rond af naar veelvoud van pi/2
		rotation = Math.PI / 2 * (double) (rotationsFromOwnToPartner);

		// bereken cosinus en sinus (behoren beide tot {-1, 0, 1}
		int cosineRotation = (int) Math.cos(rotation);
		int sineRotation = (int) Math.sin(rotation);

		// bereken aan de hand van deze rotatie de co�rdinaten van de oorsprong
		// van het assenstelsel van je teamgenoot in je eigen assenstelsel
		// x_{O'} = x_1 - x'_1*cos(\alpha_{X'}) + y'_1*sin(\alpha_{X'})
		// y_{O'} = y_1 - x'_1*sin(\alpha_{X'}) - y'_1*cos(\alpha_{X'})
		long biasX = (long) (ownFirstTP.getX() - partnerFirstTP.getX() * cosineRotation + partnerFirstTP.getY()
				* sineRotation);
		long biasY = (long) (ownFirstTP.getY() - partnerFirstTP.getX() * sineRotation - partnerFirstTP.getY()
				* cosineRotation);

		// stel met deze gegevens de TileTransform op en sla deze op
		this.tileTransformation = new TileTransform(new LongPoint(biasX, biasY), rotationsFromOwnToPartner);
	}

	private void mergeTotalAndPartnerMazes() {
		for (Tile tile : getPartnerMaze().getTiles()) {
			importPartnerTileIntoTotalMaze(tile);
		}

	}

	/**
	 * Returns the tileTransform, which is null if it not yet calcultated.
	 */
	private TileTransform getTileTransform() {
		return tileTransformation;
	}

	public final IMaze getOwnMaze() {
		return ownMaze;
	}

	public final boolean hasPartnerMaze() {
		return getPartnerMaze() != null;
	}

	public final IMaze getPartnerMaze() {
		return partnerMaze;
	}

	public final void setPartnerMaze(IMaze partnerMaze) {
		if (hasPartnerMaze()) {
			// Remove listener on previous partner maze
			getPartnerMaze().removeListener(partnerListener);
		}

		this.partnerMaze = partnerMaze;
		// Add listener
		partnerMaze.addListener(partnerListener);
	}

	public void resetPartnerMaze() {
		if (!hasPartnerMaze())
			return;

		// Reset barcode mapping mapping
		partnerBarcodeMapping.clear();
		twoCommonBarcodes.clear();
		// Reset transformation
		tileTransformation = null;
	}

	public final IMaze getTotalMaze() {
		return totalMaze;
	}

	@Override
	public final float getTileSize() {
		return getTotalMaze().getTileSize();
	}

	@Override
	public final float getEdgeSize() {
		return getTotalMaze().getEdgeSize();
	}

	@Override
	public final float getBarLength() {
		return getTotalMaze().getBarLength();
	}

	@Override
	public final Pose getOrigin() {
		return getTotalMaze().getOrigin();
	}

	@Override
	public final Pose getDefaultOrigin() {
		return getOwnMaze().getDefaultOrigin();
	}

	@Override
	public final void setOrigin(Pose origin) {
		getOwnMaze().setOrigin(origin);
		getTotalMaze().setOrigin(origin);
	}

	@Override
	public final void setOriginToDefault() {
		setOrigin(getDefaultOrigin());
	}

	@Override
	public final Mesh getMesh() {
		return getTotalMaze().getMesh();
	}

	@Override
	public final long getMinX() {
		return getTotalMaze().getMinX();
	}

	@Override
	public final long getMaxX() {
		return getTotalMaze().getMaxX();
	}

	@Override
	public final long getMinY() {
		return getTotalMaze().getMinY();
	}

	@Override
	public final long getMaxY() {
		return getTotalMaze().getMaxY();
	}

	@Override
	public Tile getTileAt(LongPoint tilePosition) {
		return getTotalMaze().getTileAt(tilePosition);
	}

	@Override
	public Tile getTileAt(Point2D tilePosition) {
		return getTotalMaze().getTileAt(tilePosition);
	}

	@Override
	public Tile getNeighbor(Tile tile, Orientation direction) {
		return getTotalMaze().getNeighbor(tile, direction);
	}

	@Override
	public Tile getOrCreateNeighbor(Tile tile, Orientation direction) {
		return getTotalMaze().getOrCreateNeighbor(tile, direction);
	}

	@Override
	public Collection<Tile> getTiles() {
		return getTotalMaze().getTiles();
	}

	@Override
	public int getNumberOfTiles() {
		return getTotalMaze().getNumberOfTiles();
	}

	@Override
	public Collection<Tile> getExploredTiles() {
		return getOwnMaze().getExploredTiles();
	}

	@Override
	public void importTile(Tile tile) {
		importTile(tile, TileTransform.getIdentity());
	}

	@Override
	public void importTile(Tile tile, TileTransform tileTransform) {
		getOwnMaze().importTile(tile, tileTransform);
		getTotalMaze().importTile(tile, tileTransform);
	}

	@Override
	public void importTiles(Iterable<Tile> tiles) {
		importTiles(tiles, TileTransform.getIdentity());
	}

	@Override
	public void importTiles(Iterable<Tile> tiles, TileTransform tileTransform) {
		getOwnMaze().importTiles(tiles, tileTransform);
		getTotalMaze().importTiles(tiles, tileTransform);
	}

	@Override
	public void setEdge(LongPoint tilePosition, Orientation orientation, EdgeType type) {
		getOwnMaze().setEdge(tilePosition, orientation, type);
		getTotalMaze().setEdge(tilePosition, orientation, type);
	}

	@Override
	public void setBarcode(LongPoint position, Barcode barcode) throws IllegalStateException {
		getOwnMaze().setBarcode(position, barcode);
		getTotalMaze().setBarcode(position, barcode);
		ownBarcodeMapping.put(barcode, position);

		if (partnerBarcodeMapping.containsKey(barcode)) {
			addCommonBarcode(barcode);
		}
	}

	@Override
	public void setBarcode(LongPoint position, byte barcode) throws IllegalStateException {
		setBarcode(position, new Barcode(barcode));
	}

	@Override
	public Tile getBarcodeTile(Barcode barcode) {
		return getTotalMaze().getBarcodeTile(barcode);
	}

	@Override
	public Tile getBarcodeTile(byte barcode) {
		return getBarcodeTile(new Barcode(barcode));
	}

	@Override
	public void setExplored(LongPoint position) {
		getOwnMaze().setExplored(position);
		getTotalMaze().setExplored(position);
	}

	@Override
	public void clear() {
		getOwnMaze().clear();
		getTotalMaze().clear();

		// Reset own barcode mapping
		ownBarcodeMapping.clear();
		// Reset partner maze
		resetPartnerMaze();
	}

	@Override
	public void addListener(MazeListener listener) {
		getTotalMaze().addListener(listener);
	}

	@Override
	public void removeListener(MazeListener listener) {
		getTotalMaze().removeListener(listener);
	}

	@Override
	public Point toAbsolute(Point relativePosition) {
		return getOwnMaze().toAbsolute(relativePosition);
	}

	@Override
	public float toAbsolute(float relativeHeading) {
		return getOwnMaze().toAbsolute(relativeHeading);
	}

	@Override
	public Pose toAbsolute(Pose relativePose) {
		return getOwnMaze().toAbsolute(relativePose);
	}

	@Override
	public Point toRelative(Point absolutePosition) {
		return getOwnMaze().toRelative(absolutePosition);
	}

	@Override
	public float toRelative(float absoluteHeading) {
		return getOwnMaze().toRelative(absoluteHeading);
	}

	@Override
	public Pose toRelative(Pose absolutePose) {
		return getOwnMaze().toRelative(absolutePose);
	}

	@Override
	public Point toTile(Point relativePosition) {
		return getOwnMaze().toTile(relativePosition);
	}

	@Override
	public Point fromTile(Point tilePosition) {
		return getOwnMaze().fromTile(tilePosition);
	}

	@Override
	public Point getTileCenter(LongPoint tilePosition) {
		return getOwnMaze().getTileCenter(tilePosition);
	}

	@Override
	public Collection<Line> getEdgeLines() {
		return getTotalMaze().getEdgeLines();
	}

	@Override
	public Line getEdgeLine(Edge edge) {
		return getTotalMaze().getEdgeLine(edge);
	}

	@Override
	public Rectangle2D getEdgeBounds(Edge edge) {
		return getTotalMaze().getEdgeBounds(edge);
	}

	@Override
	public List<Rectangle2D> getBarcodeBars(Tile tile) {
		return getTotalMaze().getBarcodeBars(tile);
	}

	@Override
	public Tile getTarget(Target target) {
		return getOwnMaze().getTarget(target);
	}

	@Override
	public void setTarget(Target target, Tile tile) {
		getOwnMaze().setTarget(target, tile);
	}

	@Override
	public Pose getStartPose(int playerNumber) {
		return getOwnMaze().getStartPose(playerNumber);
	}

	@Override
	public void setStartPose(int playerNumber, Pose pose) {
		getOwnMaze().setStartPose(playerNumber, pose);
	}

	@Override
	public void setStartPose(int playerNumber, LongPoint tilePosition, Orientation orientation) {
		getOwnMaze().setStartPose(playerNumber, tilePosition, orientation);
	}

	@Override
	public Tile getSeesawTile(Barcode barcode) {
		return getTotalMaze().getSeesawTile(barcode);
	}

	private class TeamMateMazeListener extends DefaultMazeListener {

		@Override
		public void tileAdded(Tile tile) {
			updatePartnerTile(tile);
		}

		@Override
		public void tileChanged(Tile tile) {
			updatePartnerTile(tile);
		}

		@Override
		public void mazeCleared() {
			resetPartnerMaze();
		}

	}

}
