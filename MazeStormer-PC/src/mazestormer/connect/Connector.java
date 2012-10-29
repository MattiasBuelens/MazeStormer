package mazestormer.connect;

import mazestormer.robot.Pilot;

public interface Connector {

	public Pilot getPilot();

	public boolean isConnected();

	public String getDeviceName();

	public void setDeviceName(String name);

	public void connect();

	public void disconnect();

}
