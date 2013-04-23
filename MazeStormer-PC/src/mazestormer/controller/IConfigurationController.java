package mazestormer.controller;

import mazestormer.connect.ControlMode;
import mazestormer.util.EventSource;
import mazestormer.world.ModelType;

public interface IConfigurationController extends EventSource {

	public ModelType getRobotType();

	public ControlMode getControlMode();

	public boolean isConnected();

	public void connect(ModelType robotType);

	public void setControlMode(ControlMode controlMode);

	public void disconnect();

	public void stop();

	public String getMazePath();

	public void loadMaze(String mazeFilePath);

}
