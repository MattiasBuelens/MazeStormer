package mazestormer.controller;

import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.util.EventSource;

public interface IConfigurationController extends EventSource {

	public RobotType getRobotType();

	public ControlMode getControlMode();

	public boolean isConnected();

	public void connect(RobotType robotType);

	public void setControlMode(ControlMode controlMode);

	public void disconnect();

	public void stop();

	public void loadMaze(String mazeFilePath);

}
