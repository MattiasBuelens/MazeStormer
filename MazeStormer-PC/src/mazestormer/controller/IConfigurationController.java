package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IConfigurationController extends EventSource {

	public RobotType getRobotType();
	
	public ControlMode getControlMode();
	
	public boolean isConnected();
	
	public void connect(RobotType robotType, ControlMode controlMode);
	
	public void disconnect();
	
	public void stop();
	
	public enum RobotType {
		Physical, Virtual;
	}
	
	public enum ControlMode {
		Manual, Polygon;
	}

}
