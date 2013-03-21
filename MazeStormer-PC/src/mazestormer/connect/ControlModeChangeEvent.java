package mazestormer.connect;

public class ControlModeChangeEvent {

	private final ControlMode controlMode;

	public ControlModeChangeEvent(ControlMode controlMode) {
		this.controlMode = controlMode;
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

}
