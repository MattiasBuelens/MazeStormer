package mazestormer.connect;

public enum ControlMode {
	/**
	 * Manual control
	 */
	Manual("Manual control", "manual"),

	/**
	 * Polygon driver
	 */
	Polygon("Polygon driver", "polygon"),

	/**
	 * Perpendicular on line
	 */
	PerpendicularOnLine("Perpendicular on line", "line"),

	/**
	 * Explore the maze
	 */
	Explorer("Explore the maze", "explore"),

	/**
	 * Barcode action tester
	 */
	Barcode("Barcode actions", "barcode"),

	/**
	 * Path finder tester
	 */
	PathFinder("Find path", "findpath"),

	/**
	 * Team Treasure Trek
	 */
	TeamTreasureTrek("Team Treasure Trek", "ttt");

	private final String name;
	private final String shortName;

	private ControlMode(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

	public String toString() {
		return name;
	}

	public static ControlMode byShortName(String shortName) {
		if (shortName != null) {
			for (ControlMode controlMode : values()) {
				if (controlMode.getShortName().equalsIgnoreCase(shortName)) {
					return controlMode;
				}
			}
		}
		return null;
	}

}
