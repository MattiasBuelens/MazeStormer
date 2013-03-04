package mazestormer.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.rabbitmq.ConnectionMode;
import peno.htttp.Callback;
import peno.htttp.Client;
import peno.htttp.Handler;

public class Game {

	private String localPlayer;

	private final List<GameListener> gls = new ArrayList<GameListener>();

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
	}

	public void addGameListener(GameListener gl) {
		this.gls.add(gl);
	}

	public void removeGameListener(GameListener gl) {
		this.gls.remove(gl);
	}

	public String getId() {
		return id;
	}

	public Set<String> getPlayers() {
		return client.getPlayers();
	}

	public void join(final Callback<Void> callback) {
		try {
			client.join(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					// Call listeners
					for (GameListener gl : gls) {
						gl.onGameJoined();
					}
					// Callback
					callback.onSuccess(result);
				}

				@Override
				public void onFailure(Throwable t) {
					callback.onFailure(t);
				}

			});
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	public void leave(Callback<Void> callback) {
		try {
			client.leave();
			// Call listeners
			for (GameListener gl : gls) {
				gl.onGameLeft();
			}
			// Callback
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
			for (GameListener gl : gls) {
				gl.onGameStarted(client.getPlayerNumber());
			}
		}

		@Override
		public void gameStopped() {
			for (GameListener gl : gls) {
				gl.onGameStopped();
			}
		}

		@Override
		public void gamePaused() {
			for (GameListener gl : gls) {
				gl.onGamePaused();
			}
		}

		@Override
		public void playerJoined(String playerID) {
			for (GameListener gl : gls) {
				gl.onPlayerJoined(playerID);
			}
		}

		@Override
		public void playerLeft(String playerID) {
			for (GameListener gl : gls) {
				gl.onPlayerLeft(playerID);
			}
		}

		@Override
		public void playerPosition(String playerID, double x, double y,
				double angle) {
			Pose p = new Pose((float) x, (float) y, (float) angle);
			for (GameListener gl : gls) {
				gl.onPositionUpdate(playerID, p);
			}
		}

		@Override
		public void playerFoundObject(String playerID) {
			for (GameListener gl : gls) {
				gl.onObjectFound(playerID);
			}
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
