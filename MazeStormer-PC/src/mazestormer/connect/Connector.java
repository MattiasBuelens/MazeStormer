package mazestormer.connect;

import mazestormer.robot.Robot;

public interface Connector {

	public Robot getRobot();

	public boolean isConnected();

	public void connect(ConnectionContext context);

	public void disconnect();

}
