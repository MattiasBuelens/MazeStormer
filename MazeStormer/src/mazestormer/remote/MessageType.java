package mazestormer.remote;

public interface MessageType<M extends Message> {

	public M build();

}
