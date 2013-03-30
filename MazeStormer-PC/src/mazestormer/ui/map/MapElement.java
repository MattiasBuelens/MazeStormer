package mazestormer.ui.map;

public abstract class MapElement {

	private MapHandler mapHandler;

	protected MapHandler getMapHandler() {
		return this.mapHandler;
	}

	public void setMapHandler(MapHandler mapHandler) {
		this.mapHandler = mapHandler;
	}

	protected void invokeDOMChange(Runnable request) {
		if (getMapHandler() == null) {
			request.run();
		} else {
			getMapHandler().requestDOMChange(request);
		}
	}

}