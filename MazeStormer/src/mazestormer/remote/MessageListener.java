package mazestormer.remote;

public interface MessageListener<M extends Message> {

	public void messageReceived(M message);

}
