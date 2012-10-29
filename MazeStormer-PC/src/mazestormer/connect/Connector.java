package mazestormer.connect;

import mazestormer.robot.Robot;

public interface Connector {

	public Robot getRobot();

	public boolean isConnected();

	public String getDeviceName();

	public void setDeviceName(String name);

	public void connect();

	public void disconnect();

}
