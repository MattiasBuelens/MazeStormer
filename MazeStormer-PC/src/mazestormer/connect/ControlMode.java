package mazestormer.connect;

public enum ControlMode {
	/**
	 * Manual control
	 */
	Manual("Manual control"),

	/**
	 * Polygon driver
	 */
	Polygon("Polygon driver"),

	/**
	 * Perpendicular on line
	 */
	PerpendicularOnLine("Perpendicular on line"),
	
	/**
	 * Explore the maze
	 */
	Explorer("Explore the maze"),

	/**
	 * Barcode action tester
	 */
	Barcode("Barcode actions"),
	
	/**
	 * Path finder tester
	 */
	PathFinder("Find path"),
	
	/**
	 * Team Treasure Trek
	 */
	TeamTreasureTrek("Team Treasure Trek");

	private final String name;

	private ControlMode(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
