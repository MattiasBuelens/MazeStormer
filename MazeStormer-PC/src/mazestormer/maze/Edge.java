package mazestormer.maze;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.util.LongPoint;

import com.google.common.collect.ImmutableSet;

public final class Edge {

	private final LongPoint position;
	private final Orientation orientation;
	private EdgeType type;
	public enum EdgeType{WALL, OPEN, UNKNOWN};

	/**
	 * Create a new edge.
	 * 
	 * The position and orientation of the new edge are normalized to ensure
	 * every edge has only one representation. More specifically, edges on the
	 * south or west sides of a tile are changed to edges on the north side or
	 * east side of the neighboring position respectively.
	 * 
	 * @param position
	 *            The tile position of the new edge.
	 * @param orientation
	 *            The side of the new edge.
	 */
	public Edge(LongPoint position, Orientation orientation) {
		checkNotNull(position);
		checkNotNull(orientation);

		// Normalize position and orientation
		switch (orientation) {
		case SOUTH:
			position = orientation.shift(position);
			orientation = Orientation.NORTH;
			break;
		case WEST:
			position = orientation.shift(position);
			orientation = Orientation.EAST;
			break;
		default:
			break;
		}

		this.position = position;
		this.orientation = orientation;
		setType(EdgeType.UNKNOWN);
	}

	/**
	 * Get the normalized position of this edge.
	 */
	public LongPoint getPosition() {
		return position;
	}

	/**
	 * Get the normalized orientation of this edge.
	 */
	public Orientation getOrientation() {
		return orientation;
	}
	
	public EdgeType getType(){
		return type;
	}
	
	public void setType(EdgeType type){
		this.type = type;
	}

	/**
	 * Get the positions touching this edge.
	 */
	public ImmutableSet<LongPoint> getTouching() {
		return ImmutableSet.of(getPosition(), getOrientation().shift(getPosition()));
	}

	/**
	 * Check whether this edge touches the given position.
	 * 
	 * @param position
	 *            The position to check.
	 */
	public boolean touches(LongPoint position) {
		checkNotNull(position);
		return getTouching().contains(position);
	}

	/**
	 * Get the orientation of this edge as observed by the given neighboring
	 * position.
	 * 
	 * @param neighborPosition
	 *            The neighboring position.
	 */
	public Orientation getOrientationFrom(LongPoint neighborPosition) {
		checkNotNull(neighborPosition);
		checkArgument(touches(neighborPosition));

		if (getPosition().equals(neighborPosition))
			return getOrientation();

		if (getOrientation() == Orientation.NORTH) {
			return Orientation.SOUTH;
		} else {
			return Orientation.WEST;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position.hashCode();
		result = prime * result + orientation.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		return getPosition().equals(other.getPosition())
				&& getOrientation() == other.getOrientation();
	}

}
