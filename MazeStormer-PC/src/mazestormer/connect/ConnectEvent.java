package mazestormer.connect;

public class ConnectEvent {

	private final boolean isConnected;

	public ConnectEvent(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isConnected() {
		return isConnected;
	}

}
