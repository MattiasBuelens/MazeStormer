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
	 * This pose is used to translate between the absolute pose of the robot and
	 * its relative pose on the maze.
	 */
	public Pose getOrigin();

	/**
	 * Set the pose of the robot at the bottom left corner of the origin tile.
	 * 
	 * @param origin
	 *            The new origin pose.
	 */
	public void setOrigin(Pose origin);

	/**
	 * Get the mesh of this maze.
	 */
	public Mesh getMesh();

	public int getNumberOfTiles();

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(LongPoint tilePosition);

	public long getMinX();

	public long getMaxX();

	public long getMinY();

	public long getMaxY();

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(Point2D tilePosition);

	public Tile getNeighbor(Tile tile, Orientation direction);

	public Tile getOrCreateNeighbor(Tile tile, Orientation direction);

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles();

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

	public void setBarcode(LongPoint position, byte barcode)
			throws IllegalStateException;

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
	 * position in map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toAbsolute(Point relativePosition);

	/**
	 * Get the absolute heading in robot coordinates of the given relative
	 * heading in map coordinates.
	 * 
	 * @param relativeHeading
	 *            The relative heading.
	 */
	public float toAbsolute(float relativeHeading);

	/**
	 * Get the absolute pose in robot coordinates of the given relative pose in
	 * map coordinates.
	 * 
	 * @param relativePose
	 *            The relative pose.
	 */
	public Pose toAbsolute(Pose relativePose);

	/**
	 * Get the relative position in map coordinates of the given absolute
	 * position in robot coordinates.
	 * 
	 * @param absolutePosition
	 *            The absolute position.
	 */
	public Point toRelative(Point absolutePosition);

	/**
	 * Get the relative heading in map coordinates of the given absolute heading
	 * in robot coordinates.
	 * 
	 * @param absoluteHeading
	 *            The absolute heading.
	 */
	public float toRelative(float absoluteHeading);

	/**
	 * Get the relative pose in map coordinates of the given absolute pose in
	 * robot coordinates.
	 * 
	 * @param absolutePose
	 *            The absolute pose.
	 */
	public Pose toRelative(Pose absolutePose);

	/**
	 * Get the position in tile coordinates of the given relative position in
	 * map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toTile(Point relativePosition);

	/**
	 * Get the relative position in map coordinates of the bottom left corner of
	 * the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Point fromTile(Point tilePosition);

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

	public Tile getTarget(Target target);

	public void setTarget(Target target, Tile tile);

	public Pose getStartPose(int playerNumber);

	public void setStartPose(int playerNumber, Pose pose);

	public void setStartPose(int playerNumber, LongPoint tilePosition,
			Orientation orientation);

	public void setSeesawTile(LongPoint tilePosition, Seesaw seesaw,
			Barcode seesawBarcode);
}
