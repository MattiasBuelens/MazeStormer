package mazestormer.connect;

import mazestormer.robot.ControllableRobot;

public interface Connector {

	public ControllableRobot getRobot();

	public boolean isConnected();

	public void connect(ConnectionContext context);

	public void disconnect();

}
