package mazestormer.connect;

import java.util.ArrayList;
import java.util.List;

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

	public static List<String> getShortNames() {
		List<String> shortNames = new ArrayList<String>(values().length);
		for (ControlMode controlMode : values()) {
			shortNames.add(controlMode.getShortName());
		}
		return shortNames;
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
