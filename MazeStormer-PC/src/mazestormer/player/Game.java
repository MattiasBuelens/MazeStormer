package mazestormer.player;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	private final List<Player> players = new ArrayList<Player>();
	
	/**
	 * Used to identify on the server
	 */
	private final String id;
	
	private boolean hasStarted;
	
	public Game(String id) {
		this.id = id;
	}
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public Player getPersonalPlayer() {
		return players.get(0);
	}
	
	public String getId() {
		return id;
	}
	
	public int getNbOfPlayers() {
		return players.size();
	}
	
	public boolean hasStarted() {
		return this.hasStarted;
	}
	
	public void start() {
		this.hasStarted = true;
	}
	
	public boolean isJoinable() {
		return !hasStarted() && getNbOfPlayers() < 4;
	}
}
