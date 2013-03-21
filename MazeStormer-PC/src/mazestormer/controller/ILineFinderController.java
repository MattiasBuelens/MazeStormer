package mazestormer.controller;

import mazestormer.util.EventSource;

public interface ILineFinderController extends EventSource {

	public void startSearching();

	public void stopSearching();

}
