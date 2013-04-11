package mazestormer.maze;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.util.LongPoint;

public interface IMaze {

	/**
	 * Get the size of a tile in this maze.
	 */
	public float getTileSize();

	/**
	 * Get the size of an edge in this maze.
	 */
	public float getEdgeSize();

	/**
	 * Get the length of a bar in this maze.
	 */
	public float getBarLength();

	/**
	 * Get the pose of the robot at the bottom left corner of the origin tile,
	 * i.e. the tile at {@code (0, 0)}.
	 * 
	 * <p>
	 * This pose is used to translate between the absolute pose of the robot and
	 * its relative pose on the maze.
	 * </p>
	 */
	public Pose getOrigin();

	/**
	 * Get the default origin.
	 * 
	 * <p>
	 * This corresponds to an origin such that the center of the origin tile
	 * maps to the origin of the relative coordinate system, i.e.
	 * {@code getTileCenter(new LongPoint(0, 0))} maps to {@code (0, 0)}.
	 * </p>
	 */
	public Pose getDefaultOrigin();

	/**
	 * Set the pose of the robot at the bottom left corner of the origin tile.
	 * 
	 * @param origin
	 *            The new origin pose.
	 */
	public void setOrigin(Pose origin);

	/**
	 * Set the origin to the default origin.
	 * 
	 * <p>
	 * This is equivalent to {@code setOrigin(getDefaultOrigin())}.
	 * </p>
	 */
	public void setOriginToDefault();

	/**
	 * Get the lowest X-coordinate of all tiles on this maze.
	 */
	public long getMinX();

	/**
	 * Get the highest X-coordinate of all tiles on this maze.
	 */
	public long getMaxX();

	/**
	 * Get the lowest Y-coordinate of all tiles on this maze.
	 */
	public long getMinY();

	/**
	 * Get the highest Y-coordinate of all tiles on this maze.
	 */
	public long getMaxY();

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(LongPoint tilePosition);

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(Point2D tilePosition);

	/**
	 * Get a neighbor tile of the given tile.
	 * 
	 * @param tile
	 *            The tile.
	 * @param direction
	 *            The direction in which to find the neighbor.
	 * @return The neighbor tile, or null if no neighboring tile found.
	 */
	public Tile getNeighbor(Tile tile, Orientation direction);

	/**
	 * Get or create a neighbor tile of the given tile.
	 * 
	 * @param tile
	 *            The tile.
	 * @param direction
	 *            The direction in which to find the neighbor.
	 * @return The neighbor tile.
	 */
	public Tile getOrCreateNeighbor(Tile tile, Orientation direction);

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles();

	/**
	 * Get the number of tiles on this maze.
	 */
	public int getNumberOfTiles();

	/**
	 * Get all explored tiles on this maze.
	 */
	public Collection<Tile> getExploredTiles();

	/**
	 * Get all unexplored tiles on this maze.
	 */
	public Collection<Tile> getUnexploredTiles();

	/**
	 * Import the given tile into this maze.
	 * 
	 * <p>
	 * This is equivalent to
	 * {@code importTile(tile, TileTransform.getIdentity())}.
	 * </p>
	 * 
	 * @param tile
	 *            The tile to import.
	 * @see #importTile(Tile, TileTransform)
	 */
	public void importTile(Tile tile);

	/**
	 * Import the given tile into this maze after transforming it.
	 * 
	 * @param tile
	 *            The tile to import.
	 * @param tileTransform
	 *            The transformation from the system used by the given tiles to
	 *            this maze's system. This transformation will be applied to the
	 *            given tiles when importing.
	 */
	public void importTile(Tile tile, TileTransform tileTransform);

	/**
	 * Import the given tiles into this maze.
	 * 
	 * <p>
	 * This is equivalent to
	 * {@code importTiles(tiles, TileTransform.getIdentity())}.
	 * </p>
	 * 
	 * @param tiles
	 *            The tiles to import.
	 * @see #importTiles(Iterable, TileTransform)
	 */
	public void importTiles(Iterable<Tile> tiles);

	/**
	 * Import the given tiles into this maze after transforming them.
	 * 
	 * @param tiles
	 *            The tiles to import.
	 * @param tileTransform
	 *            The transformation from the system used by the given tiles to
	 *            this maze's system. This transformation will be applied to the
	 *            given tiles when importing.
	 */
	public void importTiles(Iterable<Tile> tiles, TileTransform tileTransform);

	/**
	 * Set an edge on this maze.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 * @param orientation
	 *            The edge orientation.
	 * @param type
	 *            The edge type.
	 */
	public void setEdge(LongPoint tilePosition, Orientation orientation,
			Edge.EdgeType type);

	/**
	 * Set the walls and openings of a tile.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 * @param tileShape
	 *            The new tile shape.
	 * 
	 * @see TileShape
	 * @see TileType#getWalls(Orientation)
	 * @see TileType#getOpenings(Orientation)
	 */
	public void setTileShape(LongPoint tilePosition, TileShape tileShape);

	/**
	 * Set the barcode of a tile.
	 * 
	 * @param position
	 *            The tile position.
	 * @param barcode
	 *            The barcode.
	 * 
	 * @throws IllegalStateException
	 *             If the tile at the given position does not accept barcodes.
	 */
	public void setBarcode(LongPoint position, Barcode barcode)
			throws IllegalStateException;

	/**
	 * Set the barcode of a tile.
	 * 
	 * @param position
	 *            The tile position.
	 * @param barcode
	 *            The barcode.
	 * 
	 * @throws IllegalStateException
	 *             If the tile at the given position does not accept barcodes.
	 */
	public void setBarcode(LongPoint position, byte barcode)
			throws IllegalStateException;

	/**
	 * Find the tile with the given barcode.
	 * 
	 * @param barcode
	 *            The barcode.
	 * @return The found tile, or null if not found.
	 */
	public Tile getBarcodeTile(Barcode barcode);

	/**
	 * Find the tile with the given barcode.
	 * 
	 * @param barcode
	 *            The barcode.
	 * @return The found tile, or null if not found.
	 */
	public Tile getBarcodeTile(byte barcode);

	/**
	 * Get the seesaw corresponding to the given seesaw barcode.
	 * 
	 * @param barcode
	 *            The seesaw barcode.
	 */
	public Seesaw getSeesaw(Barcode barcode);

	/**
	 * Get the seesaw corresponding to the given seesaw barcode.
	 * 
	 * @param barcode
	 *            The seesaw barcode.
	 */
	public Seesaw getSeesaw(byte barcode);

	/**
	 * The amount of seesaws in the maze.
	 */
	public int getAmountOfSeesaws();

	/**
	 * Get or create the seesaw corresponding to the given seesaw barcode.
	 * 
	 * @param barcode
	 *            The seesaw barcode.
	 */
	public Seesaw getOrCreateSeesaw(Barcode barcode);

	/**
	 * Get or create the seesaw corresponding to the given seesaw barcode.
	 * 
	 * @param barcode
	 *            The seesaw barcode.
	 */
	public Seesaw getOrCreateSeesaw(byte barcode);

	/**
	 * Set the seesaw of a tile.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 * @param seesawBarcode
	 *            The seesaw barcode at the side of the tile.
	 */
	public void setSeesaw(LongPoint tilePosition, Barcode seesawBarcode);

	/**
	 * Find the seesaw tile facing the given seesaw barcode.
	 * 
	 * @param barcode
	 *            The seesaw barcode.
	 * @return The found tile, or null if not found.
	 */
	public Tile getSeesawTile(Barcode barcode);

	/**
	 * Finds all the tiles associated with a seesaw.
	 * 
	 * @return A collection with all seesaw tiles.
	 */
	public Collection<Tile> getSeesawBarcodeTiles();

	/**
	 * Set a tile as explored.
	 * 
	 * @param position
	 *            The tile position.
	 */
	public void setExplored(LongPoint position);

	/**
	 * Clear this maze, removing all tiles and edges.
	 */
	public void clear();

	/**
	 * Add a maze listener.
	 */
	public void addListener(MazeListener listener);

	/**
	 * Remove a maze listener.
	 */
	public void removeListener(MazeListener listener);

	/**
	 * Get the absolute position in robot coordinates of the given relative
	 * position in maze coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toAbsolute(Point relativePosition);

	/**
	 * Get the absolute heading in robot coordinates of the given relative
	 * heading in maze coordinates.
	 * 
	 * @param relativeHeading
	 *            The relative heading.
	 */
	public float toAbsolute(float relativeHeading);

	/**
	 * Get the absolute pose in robot coordinates of the given relative pose in
	 * maze coordinates.
	 * 
	 * @param relativePose
	 *            The relative pose.
	 */
	public Pose toAbsolute(Pose relativePose);

	/**
	 * Get the relative position in maze coordinates of the given absolute
	 * position in robot coordinates.
	 * 
	 * @param absolutePosition
	 *            The absolute position.
	 */
	public Point toRelative(Point absolutePosition);

	/**
	 * Get the relative heading in maze coordinates of the given absolute
	 * heading in robot coordinates.
	 * 
	 * @param absoluteHeading
	 *            The absolute heading.
	 */
	public float toRelative(float absoluteHeading);

	/**
	 * Get the relative pose in maze coordinates of the given absolute pose in
	 * robot coordinates.
	 * 
	 * @param absolutePose
	 *            The absolute pose.
	 */
	public Pose toRelative(Pose absolutePose);

	/**
	 * Get the position in tile coordinates of the given relative position in
	 * maze coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toTile(Point relativePosition);

	/**
	 * Get the relative position in maze coordinates of the bottom left corner
	 * of the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Point fromTile(Point tilePosition);

	/**
	 * Get the relative position of the center of a tile.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Point getTileCenter(LongPoint tilePosition);

	/**
	 * Get a collection of all edges as lines, in relative coordinates.
	 */
	public Collection<Line> getEdgeLines();

	/**
	 * Get the line of an edge, in relative coordinates.
	 * 
	 * @param edge
	 *            The edge.
	 */
	public Line getEdgeLine(Edge edge);

	/**
	 * Get the boundaries of an edge, in relative coordinates.
	 * 
	 * @param edge
	 *            The edge.
	 */
	public Rectangle2D getEdgeBounds(Edge edge);

	/**
	 * Get a list of bar rectangles representing the barcode from the given
	 * tile, in tile coordinates relative to the tile position.
	 * 
	 * @param tile
	 *            The tile with barcode.
	 * @return A list of rectangles with an odd number of bars, starting and
	 *         ending with the bounds of a terminating black bar.
	 */
	public List<Rectangle2D> getBarcodeBars(Tile tile);

	public enum Target {
		GOAL, CHECKPOINT
	}

	/**
	 * Get the tile corresponding with the given target.
	 * 
	 * @param target
	 *            The target to find.
	 * @return The found tile, or null if not found.
	 */
	public Tile getTarget(Target target);

	/**
	 * Mark the given tile as a target.
	 * 
	 * @param target
	 *            The target.
	 * @param tile
	 *            The tile to mark.
	 */
	public void setTarget(Target target, Tile tile);

	/**
	 * Get the start pose for a player with the given player number.
	 * 
	 * @param playerNumber
	 *            The player number.
	 * @return The start pose, or null if no start pose found.
	 */
	public Pose getStartPose(int playerNumber);

	/**
	 * Set the start pose for a player with the given player number.
	 * 
	 * @param playerNumber
	 *            The player number.
	 * @param pose
	 *            The start pose.
	 */
	public void setStartPose(int playerNumber, Pose pose);

	/**
	 * Set the start pose for a player with the given player number.
	 * 
	 * @param playerNumber
	 *            The player number.
	 * @param tilePosition
	 *            The start position.
	 * @param orientation
	 *            The start orientation.
	 */
	public void setStartPose(int playerNumber, LongPoint tilePosition,
			Orientation orientation);

}
