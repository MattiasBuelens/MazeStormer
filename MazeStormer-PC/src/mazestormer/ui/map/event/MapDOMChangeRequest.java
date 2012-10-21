package mazestormer.ui.map.event;

public class MapDOMChangeRequest {

	private final Runnable request;

	public MapDOMChangeRequest(Runnable request) {
		this.request = request;
	}

	public Runnable getRequest() {
		return request;
	}

}
