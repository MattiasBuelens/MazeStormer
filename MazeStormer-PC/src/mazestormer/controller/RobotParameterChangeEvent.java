package mazestormer.controller;

public class RobotParameterChangeEvent {

	private final String parameter;
	private final double value;

	public RobotParameterChangeEvent(String parameter, double value) {
		this.parameter = parameter;
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public double getValue() {
		return value;
	}

}
