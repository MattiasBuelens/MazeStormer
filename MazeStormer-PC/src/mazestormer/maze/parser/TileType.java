package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;

import mazestormer.maze.Orientation;

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
			return EnumSet.of(orientation.rotateClockwise(),
					orientation.rotateCounterClockwise());
		}

		@Override
		public boolean supportsBarcode() {
			return true;
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
			return EnumSet
					.of(orientation, orientation.rotateCounterClockwise());
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
			return EnumSet.of(orientation, orientation.rotateClockwise(),
					orientation.rotateCounterClockwise());
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
