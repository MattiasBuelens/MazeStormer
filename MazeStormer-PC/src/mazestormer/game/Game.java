package mazestormer.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.player.Player;
import mazestormer.world.World;
import peno.htttp.Callback;
import peno.htttp.DisconnectReason;
import peno.htttp.PlayerClient;
import peno.htttp.PlayerHandler;
import peno.htttp.Tile;

import com.rabbitmq.client.Connection;

public class Game {

	private final String id;
	private final Player localPlayer;
	private final World world;

	private final PlayerClient client;

	private final Handler handler;
	private final List<GameListener> gls = new ArrayList<GameListener>();

	public Game(Connection connection, String id, Player localPlayer, World world) throws IOException,
			IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer;
		this.world = world;

		this.handler = new Handler();
		this.client = new PlayerClient(connection, this.handler, id, localPlayer.getPlayerID());
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

	public World getWorld() {
		return this.world;
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

	public void pause() throws IllegalStateException, IOException {
		client.pause();
	}

	public void stop() throws IllegalStateException, IOException {
		client.stop();
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

	public void updatePosition(Pose pose) {
		try {
			// Publish
			client.updatePosition(pose.getX(), pose.getY(), pose.getHeading());
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not report position update");
			e.printStackTrace();
		}
	}

	private class Handler implements PlayerHandler {

		@Override
		public void gameRolled(int playerNumber, int objectNumber) {
			for (GameListener gl : gls) {
				gl.onGameRolled(playerNumber, objectNumber);
			}
		}

		@Override
		public void gameStarted() {
			for (GameListener gl : gls) {
				gl.onGameStarted();
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
		public void gameWon(int teamNumber) {
			for (GameListener gl : gls) {
				gl.onGameWon(teamNumber);
			}
		}

		@Override
		public void playerJoining(String playerID) {
			// TODO Perhaps add GameListener.onPlayerJoining() ?
		}

		@Override
		public void playerJoined(String playerID) {
		}

		@Override
		public void playerDisconnected(String playerID, DisconnectReason reason) {
		}

		@Override
		public void playerReady(String playerID, boolean isReady) {
			for (GameListener gl : gls) {
				gl.onPlayerReady(playerID, isReady);
			}
		}

		@Override
		public void playerFoundObject(String playerID, int playerNumber) {
			for (GameListener gl : gls) {
				gl.onObjectFound(playerID);
			}
		}

		@Override
		public void teamConnected(String partnerID) {
			// TODO Auto-generated method stub

		}

		@Override
		public void teamTilesReceived(List<Tile> tiles) {
			// TODO Auto-generated method stub

		}

		@Override
		public void teamPosition(double x, double y, double angle) {
			// TODO Auto-generated method stub
			
		}

	}

}