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

	public Game(String id, Player localPlayer) throws IOException,
			IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer.getPlayerID();

		// TODO Implement handler!
		this.handler = new GameHandler();
		this.client = new Client(ConnectionMode.LOCAL.newConnection(),
				this.handler, id, this.localPlayer);

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

	private class GameHandler implements Handler {

		@Override
		public void gameStarted() {
			System.out.println("Game started, player number: "
					+ client.getPlayerNumber());
		}

		@Override
		public void gameStopped() {
			System.out.println("Game stopped");
		}

		@Override
		public void gamePaused() {
			System.out.println("Game paused");
		}

		@Override
		public void playerJoined(String playerID) {
			System.out.println("Player " + playerID + " joined");
			// Call addPlayer()
		}

		@Override
		public void playerLeft(String playerID) {
			System.out.println("Player " + playerID + " left");
			// Call removePlayer()
		}

		@Override
		public void playerPosition(String playerID, double x, double y,
				double angle) {
			System.out.println("Player " + playerID + " position: " + x + ", "
					+ y + " @ " + angle + "°");
		}

		@Override
		public void playerFoundObject(String playerID) {
			System.out.println("Player " + playerID + " found their object");
		}

	}

	public int getObjectNumber() {
		return client.getPlayerNumber();
	}

	public void objectFound() {
		try {
			client.foundObject();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not report object found");
			e.printStackTrace();
		}
	}

}
