package mazestormer.controller;

public class LocalPlayerRenameEvent {

	private final String previousID;
	private final String newID;

	public LocalPlayerRenameEvent(String previousID, String newID) {
		this.previousID = previousID;
		this.newID = newID;
	}

	public String getPreviousID() {
		return previousID;
	}

	public String getNewID() {
		return newID;
	}

}
