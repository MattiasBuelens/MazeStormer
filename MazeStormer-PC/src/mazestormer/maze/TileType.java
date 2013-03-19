package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public enum TileType {

	/**
	 * A straight piece of track.
	 * 
	 * Example: North => West and East are closed.
	 */
	STRAIGHT("Straight") {
		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			checkNotNull(orientation);
			return EnumSet.of(orientation.rotateClockwise(), orientation.rotateCounterClockwise());
		}

		@Override
		public boolean supportsBarcode() {
			return true;
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			// Check count
			if (walls.size() != 2)
				return null;
			// Check placement
			Iterator<Orientation> it = walls.iterator();
			Orientation first = it.next(), second = it.next();
			if (first.rotateClockwise(2) != second)
				return null;
			// Get direction
			Orientation direction = null;
			switch (first) {
			case WEST:
			case EAST:
				direction = Orientation.NORTH;
				break;
			case NORTH:
			case SOUTH:
			default:
				direction = Orientation.EAST;
				break;
			}
			return new TileShape(this, direction);
		}
	},

	/**
	 * A corner.
	 * 
	 * Example: North => North and West are closed.
	 */
	CORNER("Corner") {
		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			checkNotNull(orientation);
			return EnumSet.of(orientation, orientation.rotateCounterClockwise());
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			// Check count
			if (walls.size() != 2)
				return null;
			// Get direction
			Iterator<Orientation> it = walls.iterator();
			Orientation first = it.next(), second = it.next();
			if (first.rotateCounterClockwise() == second) {
				return new TileShape(this, first);
			} else if (first.rotateClockwise() == second) {
				return new TileShape(this, second);
			}
			return null;
		}
	},

	/**
	 * A T-intersection.
	 * 
	 * Example: North => North is closed; West, East and South are open.
	 */
	T("T") {
		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			checkNotNull(orientation);
			return EnumSet.of(orientation);
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			// Check count
			if (walls.size() != 1)
				return null;
			// Get side of wall
			Iterator<Orientation> it = walls.iterator();
			Orientation direction = it.next();
			return new TileShape(this, direction);
		}
	},

	/**
	 * A dead end.
	 * 
	 * Example: North => South is open; North, West and East are closed.
	 */
	DEAD_END("DeadEnd") {
		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			checkNotNull(orientation);
			return EnumSet.of(orientation, orientation.rotateClockwise(), orientation.rotateCounterClockwise());
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			// Check count
			if (walls.size() != 3)
				return null;
			// Get openings
			EnumSet<Orientation> openings = EnumSet.complementOf(EnumSet.copyOf(walls));
			Iterator<Orientation> it = openings.iterator();
			// Get opposite side of opening
			Orientation direction = it.next().rotateClockwise(2);
			return new TileShape(this, direction);
		}
	},

	/**
	 * A cross intersection.
	 * 
	 * All directions are open regardless of the orientation.
	 */
	CROSS("Cross") {
		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			return EnumSet.noneOf(Orientation.class);
		}

		@Override
		public boolean hasOrientation() {
			return false;
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			return walls.isEmpty() ? new TileShape(this, null) : null;
		}
	},

	/**
	 * A fully closed section.
	 * 
	 * All directions are closed regardless of the orientation.
	 */
	CLOSED("Closed") {

		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			return EnumSet.allOf(Orientation.class);
		}

		@Override
		public boolean hasOrientation() {
			return false;
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			return walls.equals(getWalls(null)) ? new TileShape(this, null) : null;
		}

	},

	/**
	 * A seesaw.
	 * 
	 * The direction specifies the side from which to enter.
	 */
	SEESAW("Seesaw") {

		@Override
		public EnumSet<Orientation> getWalls(Orientation orientation) {
			return STRAIGHT.getWalls(orientation);
		}

		@Override
		public TileShape matches(Set<Orientation> walls) {
			return STRAIGHT.matches(walls);
		}

	};

	private final String name;

	private TileType(String name) {
		this.name = name;
	}

	/**
	 * Get the name of this tile token.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Check whether this tile type is orientation-dependent.
	 */
	public boolean hasOrientation() {
		return true;
	}

	/**
	 * Check whether this tile type supports barcodes.
	 */
	public boolean supportsBarcode() {
		return false;
	}

	/**
	 * Get the orientations where closed edges should be placed.
	 * 
	 * @param orientation
	 *            The orientation of the tile.
	 * 
	 * @return A set of edge orientations.
	 */
	public abstract EnumSet<Orientation> getWalls(Orientation orientation);

	/**
	 * Check if this tile type matches the given set of wall orientations. If
	 * this type matches the set of walls, the tile shape is returned.
	 * 
	 * @param walls
	 *            The orientations of the walls.
	 * 
	 * @return The tile shape, or null if no match.
	 */
	public abstract TileShape matches(Set<Orientation> walls);

	/**
	 * Get the orientations where open edges should be placed.
	 * 
	 * @param orientation
	 *            The orientation of the tile.
	 * 
	 * @return A set of edge orientations.
	 */
	public EnumSet<Orientation> getOpenings(Orientation orientation) {
		return EnumSet.complementOf(getWalls(orientation));
	}

	/**
	 * Get the tile type associated with the given name.
	 * 
	 * @param name
	 *            The name of the tile type.
	 * 
	 * @return The tile type, or null if not found.
	 */
	public static TileType byName(String name) {
		if (name != null) {
			for (TileType type : values()) {
				if (type.getName().equalsIgnoreCase(name)) {
					return type;
				}
			}
		}
		return null;
	}
}
