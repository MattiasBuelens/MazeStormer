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
	 * Barcode action tester
	 */
	Barcode("Barcode actions");

	private final String name;

	private ControlMode(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
