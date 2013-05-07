package mazestormer.robot;

public enum RobotSound {
	MAIN("liquido.wav"), SONAR("! Sonar.wav");

	private final String fileName;

	private static boolean enable = true;

	private RobotSound(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	// TODO Do we really need this here? Perhaps move it to SoundPlayer itself?
	public static boolean isEnabled() {
		return enable;
	}

	public static void switchEnable() {
		enable = (enable == true) ? false : true;
	}

	public static void setEnable(boolean request) {
		enable = request;
	}
}