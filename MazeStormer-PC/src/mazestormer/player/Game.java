package mazestormer.player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mazestormer.rabbitmq.ConnectionMode;
import peno.htttp.Callback;
import peno.htttp.Client;
import peno.htttp.Handler;

public class Game {

	private String localPlayer;
	private final Map<String, Player> players = new HashMap<String, Player>();

	/**
	 * Used to identify on the server
	 */
	private final String id;

	private final Client client;
	private final GameHandler handler;

	public Game(String id, Player localPlayer) throws IOException, IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer.getPlayerID();

		// TODO Implement handler!
		this.handler = new GameHandler();
		this.client = new Client(ConnectionMode.LOCAL.newConnection(), this.handler, id, this.localPlayer);

		addPlayer(localPlayer);
	}

	public Player getLocalPlayer() {
		return players.get(localPlayer);
	}

	public String getId() {
		return id;
	}

	public int getNbOfPlayers() {
		return players.size();
	}
	
	public Player getPlayer(String playerID) {
		return players.get(playerID);
	}

	protected void addPlayer(Player player) {
		players.put(player.getPlayerID(), player);
	}

	protected void removePlayer(Player player) {
		players.remove(player.getPlayerID());
	}

	public void join(Callback<Void> callback) {
		try {
			client.join(callback);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	public void leave(Callback<Void> callback) {
		try {
			client.leave();
			callback.onSuccess(null);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	public void setReady(boolean isReady, Callback<Void> callback) {
		try {
			client.setReady(isReady);
			callback.onSuccess(null);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	public void terminate() {
		client.shutdown();
	}
	
	private void logToSpecific(String playerID, String message) {
		players.get(playerID).getLogger().info(message);
	}
	
	private void logToAll(String message) {
		for(Player p : players.values()) {
			p.getLogger().info(message);
		}
	}

	private class GameHandler implements Handler {

		@Override
		public void gameStarted() {
			logToAll("Game started, player number: " + client.getPlayerNumber());
		}

		@Override
		public void gameStopped() {
			logToAll("Game stopped");
		}

		@Override
		public void gamePaused() {
			logToAll("Game paused");
		}

		@Override
		public void playerJoined(String playerID) {
			logToSpecific(playerID, "Player " + playerID + " joined");
			// Call addPlayer()
		}

		@Override
		public void playerLeft(String playerID) {
			logToSpecific(playerID, "Player " + playerID + " left");
			// Call removePlayer()
		}

		@Override
		public void playerPosition(String playerID, double x, double y, double angle) {
			logToSpecific(playerID, " position: " + x + ", " + y + " @ " + angle + "°");
		}

		@Override
		public void playerFoundObject(String playerID) {
			logToSpecific(playerID, "Player " + playerID + " found their object");
		}
	}
}
