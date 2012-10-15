package mazestormer.ui.event;

public class RobotParameterChangeRequest {

	private final String parameter;
	private final double value;

	public RobotParameterChangeRequest(String parameter, double value) {
		super();
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
