package mazestormer.ui.map.event;

public abstract class MapEvent {
	
	private String playerID;
	
	protected MapEvent(String playerID) {
		this.playerID = playerID;
	}
	
	public String getPlayerID() {
		return this.playerID;
	}

}
