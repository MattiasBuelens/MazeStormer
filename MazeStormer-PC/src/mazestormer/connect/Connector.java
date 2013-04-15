package mazestormer.connect;

import mazestormer.robot.ControllablePCRobot;

public interface Connector {

	public ControllablePCRobot getRobot();

	public boolean isConnected();

	public void connect(ConnectionContext context);

	public void disconnect();

}
