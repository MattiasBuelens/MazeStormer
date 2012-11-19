package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.GridNode;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import mazestormer.util.AbstractEventSource;
import mazestormer.util.LongPoint;

public class Maze extends AbstractEventSource {

	private static final float defaultTileSize = 40f;
	private static final float defaultEdgeSize = 2f;

	private final float tileSize;
	private final float edgeSize;
	private Pose origin = new Pose();

	private Map<LongPoint, Tile> tiles = new HashMap<LongPoint, Tile>();
	private Map<Edge, Line> lines = new HashMap<Edge, Line>();

	private List<MazeListener> listeners = new ArrayList<MazeListener>();
	
	private Rectangle boundingRectangle;

	public Maze(float tileSize, float edgeSize) {
		this.tileSize = tileSize;
		this.edgeSize = edgeSize;
		this.boundingRectangle = new Rectangle(0,0,0,0);
	}

	public Maze(float tileSize) {
		this(tileSize, defaultEdgeSize);
	}

	public Maze() {
		this(defaultTileSize);
	}

	/**
	 * Get the size of a tile in this maze.
	 */
	public float getTileSize() {
		return tileSize;
	}

	/**
	 * Get the size of an edge in this maze.
	 */
	public float getEdgeSize() {
		return edgeSize;
	}

	/**
	 * Get the pose of the robot at the bottom left corner of the origin tile,
	 * i.e. the tile at {@code (0, 0)}.
	 * 
	 * This pose is used to translate between the absolute pose of the robot and
	 * its relative pose on the maze.
	 */
	public Pose getOrigin() {
		return origin;
	}

	/**
	 * Set the pose of the robot at the bottom left corner of the origin tile.
	 * 
	 * @param origin
	 *            The new origin pose.
	 */
	public void setOrigin(Pose origin) {
		this.origin = origin;
		fireMazeOriginChanged();
	}

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(LongPoint tilePosition) {
		checkNotNull(tilePosition);
		// Try to get tile
		Tile tile = tiles.get(tilePosition);
		if (tile == null) {
			// Create and put tile
			tile = new Tile(tilePosition);
			tiles.put(tilePosition, tile);
			// Fire tile added event
			fireTileAdded(tile);
			updateBoundingRectangle(tile);
		}
		return tile;
	}
	
	private void updateBoundingRectangle(Tile tile){
		long x = (long) getBoundingRectangle().getX();
		long y = (long) getBoundingRectangle().getY();
		long width = (long) (getBoundingRectangle().getWidth()/getTileSize());
		long height = (long) (getBoundingRectangle().getHeight()/getTileSize());
			
		if(!((x <= tile.getX() && tile.getX() <= x+width) || (x >= tile.getX() && tile.getX() >= x+width))){
			if(Math.signum(x) == Math.signum(width))
				width = (int) Math.abs(tile.getX()-x);
			else
				width = (int) (Math.abs(tile.getX())+Math.abs(x));
		}
		if(!((y <= tile.getY() && tile.getY() <= y+height) || (y >= tile.getY() && tile.getY() >= y+height))){
			if(Math.signum(y) == Math.signum(height))
				width = (int) Math.abs(tile.getY()-y);
			else
				height = (int) (Math.abs(tile.getY())+Math.abs(y));
		}
		if(x > tile.getX())
				x = (int) tile.getX();
		if(y > tile.getY())
				y = (int) tile.getY();
		setBoundingRectangle(new Rectangle((int) x,(int) y,(int) (width*getTileSize()),(int) (height*getTileSize())));	
	}
	
	public Rectangle getBoundingRectangle(){
		return this.boundingRectangle;
	}
	
	private void setBoundingRectangle(Rectangle request){
		this.boundingRectangle = request;
	}

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(Point2D tilePosition) {
		checkNotNull(tilePosition);
		return getTileAt(new LongPoint(tilePosition));
	}

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles() {
		return Collections.unmodifiableCollection(tiles.values());
	}

	/**
	 * Get a collection of all edges as lines.
	 */
	public Collection<Line> getLines() {
		return Collections.unmodifiableCollection(lines.values());
	}

	/**
	 * Add an edge to this maze.
	 * 
	 * @param edge
	 *            The edge to add.
	 * @post The edge is added to the maze tiles at its touching positions. |
	 *       for each point in edge.getTouching() : |
	 *       getTileAt(point).hasEdge(edge)
	 */
	public void addEdge(Edge edge) {
		checkNotNull(edge);

		// Fire edge added event
		fireEdgeAdded(edge);
		addLine(edge);

		// Add edge to touching tiles
		for (LongPoint touchingPosition : edge.getTouching()) {
			Tile touchingTile = getTileAt(touchingPosition);
			touchingTile.addEdge(edge);
			// Fire tile updated event
			fireTileChanged(touchingTile);
		}
	}

	private void addLine(Edge edge) {
		// Get edge points in tile coordinates
		Line line = edge.getOrientation().getLine();
		Point position = edge.getPosition().toPoint();
		Point p1 = line.getP1().add(position);
		Point p2 = line.getP2().add(position);

		// Convert to relative coordinates
		p1 = fromTile(p1);
		p2 = fromTile(p2);

		// Add line
		Line l = new Line(p1.x, p1.y, p2.x, p2.y);
		lines.put(edge, l);
	}

	/**
	 * Clear this maze, removing all tiles and edges.
	 */
	public void clear() {
		tiles.clear();
		lines.clear();
		fireMazeCleared();
	}

	/**
	 * Add a maze listener.
	 */
	public void addListener(MazeListener listener) {
		checkNotNull(listener);
		listeners.add(listener);
	}

	/**
	 * Remove a maze listener.
	 */
	public void removeListener(MazeListener listener) {
		checkNotNull(listener);
		listeners.remove(listener);
	}

	private void fireTileAdded(Tile tile) {
		checkNotNull(tile);
		for (MazeListener listener : listeners) {
			listener.tileAdded(tile);
		}
	}

	private void fireTileChanged(Tile tile) {
		checkNotNull(tile);
		for (MazeListener listener : listeners) {
			listener.tileChanged(tile);
		}
	}

	private void fireEdgeAdded(Edge edge) {
		checkNotNull(edge);
		for (MazeListener listener : listeners) {
			listener.edgeAdded(edge);
		}
	}

	private void fireMazeOriginChanged() {
		for (MazeListener listener : listeners) {
			listener.mazeOriginChanged(getOrigin());
		}
	}

	private void fireMazeCleared() {
		for (MazeListener listener : listeners) {
			listener.mazeCleared();
		}
	}

	/**
	 * Get the absolute position in robot coordinates of the given relative
	 * position in map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toAbsolute(Point relativePosition) {
		checkNotNull(relativePosition);
		return relativePosition.add(getOrigin().getLocation());
	}

	/**
	 * Get the absolute heading in robot coordinates of the given relative
	 * heading in map coordinates.
	 * 
	 * @param relativeHeading
	 *            The relative heading.
	 */
	public float toAbsolute(float relativeHeading) {
		return normalizeHeading(relativeHeading + getOrigin().getHeading());
	}

	/**
	 * Get the absolute pose in robot coordinates of the given relative pose in
	 * map coordinates.
	 * 
	 * @param relativePose
	 *            The relative pose.
	 */
	public Pose toAbsolute(Pose relativePose) {
		checkNotNull(relativePose);
		Pose pose = new Pose();
		pose.setLocation(toAbsolute(relativePose.getLocation()));
		pose.setHeading(toAbsolute(relativePose.getHeading()));
		return pose;
	}

	/**
	 * Get the relative position in map coordinates of the given absolute
	 * position in robot coordinates.
	 * 
	 * @param absolutePosition
	 *            The absolute position.
	 */
	public Point toRelative(Point absolutePosition) {
		checkNotNull(absolutePosition);
		return absolutePosition.subtract(getOrigin().getLocation());
	}

	/**
	 * Get the relative heading in map coordinates of the given absolute heading
	 * in robot coordinates.
	 * 
	 * @param absoluteHeading
	 *            The absolute heading.
	 */
	public float toRelative(float absoluteHeading) {
		return normalizeHeading(absoluteHeading - getOrigin().getHeading());
	}

	/**
	 * Get the relative pose in map coordinates of the given absolute pose in
	 * robot coordinates.
	 * 
	 * @param absolutePose
	 *            The absolute pose.
	 */
	public Pose toRelative(Pose absolutePose) {
		checkNotNull(absolutePose);
		Pose pose = new Pose();
		pose.setLocation(toRelative(absolutePose.getLocation()));
		pose.setHeading(toRelative(absolutePose.getHeading()));
		return pose;
	}

	/**
	 * Get the position in tile coordinates of the given relative position in
	 * map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toTile(Point relativePosition) {
		double x = relativePosition.getX() / getTileSize();
		double y = relativePosition.getY() / getTileSize();
		return new Point((float) x, (float) y);
	}

	/**
	 * Get the relative position in map coordinates of the bottom left corner of
	 * the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Point fromTile(Point tilePosition) {
		return tilePosition.multiply(getTileSize());
	}

	/**
	 * Normalize a given heading to ensure it is between -180 and +180 degrees.
	 * 
	 * @param heading
	 *            The heading.
	 */
	private float normalizeHeading(float heading) {
		while (heading < 180)
			heading += 360;
		while (heading > 180)
			heading -= 360;
		return heading;
	}
	
	public Path findPath(Tile startTile, Tile goalTile){
		float gridspace = getTileSize();
		float clearance = getTileSize()/2;
		Line[] lines = getLines().toArray(new Line[0]);
		LineMap map = new LineMap(lines, getBoundingRectangle());
		FourWayGridMesh mesh = new FourWayGridMesh(map, gridspace, clearance);
		
		Node startNode = getClosestNodeOfTile(mesh.getMesh(),startTile);
		Node goalNode = getClosestNodeOfTile(mesh.getMesh(),goalTile);
		
		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}
	
	private Node getClosestNodeOfTile(Collection<Node> nodes, Tile tile){
		Node closest = new GridNode(100000,100000, getTileSize());
		for(Node node : nodes)
			if(Math.sqrt(Math.pow(Math.abs(tile.getX()-node.x),2)+Math.pow(Math.abs(tile.getY()-node.y),2)) < Math.sqrt(Math.pow(Math.abs(tile.getX()-closest.x),2)+Math.pow(Math.abs(tile.getY()-closest.y),2)))
				closest = node;
		return closest;
	}
	
//	private Map<Tile, Node> getMesh(){
//		Map<Tile, Node> nodes = new HashMap<Tile, Node>();
//		for(Tile tile : getTiles())
//			nodes.put(tile, new GridNode(tile.getX()+getTileSize()/2, tile.getY()+getTileSize()/2, getTileSize()));
//		for(Tile tile : getTiles()){
//			Node node = nodes.get(tile);
//			for(int i=0; i<Orientation.values().length; i++)
//				if(tile.hasEdgeAt(Orientation.values()[i])){}
//					//node.addNeighbour(nodes.get(getTileTo(tile, Orientation.values()[i])));
//		}
//		return nodes;
//	}
}
