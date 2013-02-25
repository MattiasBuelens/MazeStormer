package mazestormer.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mazestormer.rabbitmq.ConnectionMode;
import mazestormer.rabbitmq.Sender;

public class Game {

	private final List<Player> players = new ArrayList<Player>();

	/**
	 * Used to identify on the server
	 */
	private final String id;

	private boolean hasStarted;

	private final Sender sender;

	public Game(String id, Player localPlayer) throws IOException {
		this.id = id;
		this.sender = new Sender(ConnectionMode.LOCAL, id,
				localPlayer.getPlayerID());
		addPlayer(localPlayer);
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
