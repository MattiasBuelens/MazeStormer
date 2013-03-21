package mazestormer.controller;

public class CalibrationChangeEvent {

	private final String parameter;
	private final int value;

	public CalibrationChangeEvent(String parameter, int value) {
		this.parameter = parameter;
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public int getValue() {
		return value;
	}

}
