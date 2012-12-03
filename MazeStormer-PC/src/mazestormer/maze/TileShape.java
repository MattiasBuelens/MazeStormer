package mazestormer.maze;

import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

public class TileShape {

	private final TileType type;
	private final Orientation orientation;

	public TileShape(TileType type, Orientation orientation) {
		this.type = type;
		this.orientation = type.hasOrientation() ? orientation : null;
	}

	public TileType getType() {
		return type;
	}

	public boolean hasOrientation() {
		return getType().hasOrientation();
	}

	public Orientation getOrientation() {
		checkState(hasOrientation());
		return orientation;
	}

	/**
	 * Get the shape of a tile given the set of its wall orientations.
	 * 
	 * @param walls
	 *            The orientations of the walls.
	 * @return The tile shape, or null if no match.
	 */
	public static TileShape get(Set<Orientation> walls) {
		for (TileType type : TileType.values()) {
			TileShape shape = type.matches(walls);
			if (shape != null)
				return shape;
		}
		return null;
	}

}
