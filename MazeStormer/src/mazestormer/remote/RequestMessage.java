package mazestormer.remote;

public interface RequestMessage extends Message {

	public int getRequestId();

	public void setRequestId(int requestId);

}
