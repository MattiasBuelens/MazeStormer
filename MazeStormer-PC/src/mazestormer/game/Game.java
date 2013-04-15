package mazestormer.game;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.CombinedMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.maze.parser.Parser;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.Player;
import mazestormer.player.RelativePlayer;
import mazestormer.robot.Robot;
import peno.htttp.Callback;
import peno.htttp.DisconnectReason;
import peno.htttp.PlayerClient;
import peno.htttp.PlayerHandler;
import peno.htttp.PlayerType;

import com.rabbitmq.client.Connection;

public class Game {

	private final String id;

	private final Player localPlayer;
	private Player partnerPlayer;

	private final PlayerClient client;
	private final Handler handler;
	private final List<GameListener> listeners = new ArrayList<GameListener>();

	public Game(Connection connection, String id, Player localPlayer) throws IOException, IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer;
		this.handler = new Handler();

		// Gather player details
		String playerID = localPlayer.getPlayerID();
		// TODO Determine robot type
		PlayerType playerType = PlayerType.PHYSICAL;
		double width = localPlayer.getRobot().getWidth();
		double height = localPlayer.getRobot().getHeight();
		peno.htttp.PlayerDetails player = new peno.htttp.PlayerDetails(playerID, playerType, width, height);

		this.client = new PlayerClient(connection, this.handler, id, player);
	}

	public void addGameListener(GameListener listener) {
		listeners.add(listener);
	}

	public void removeGameListener(GameListener listener) {
		listeners.remove(listener);
	}

	public String getId() {
		return id;
	}

	public Set<String> getPlayers() {
		return client.getPlayers();
	}

	private CombinedMaze getLocalMaze() {
		return (CombinedMaze) localPlayer.getMaze();
	}

	public void join(final Callback<Void> callback) {
		try {
			client.join(new Callback<Void>() {
				@Override
				public void onSuccess(Void result) {
					// Call listeners
					for (GameListener listener : listeners) {
						listener.onGameJoined();
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
			for (GameListener listener : listeners) {
				listener.onGameLeft();
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

	public void joinTeam(int teamNumber) {
		try {
			client.joinTeam(teamNumber);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not join team #" + teamNumber);
			e.printStackTrace();
		}
	}

	public void lockSeesaw(int barcode) {
		try {
			client.lockSeesaw(barcode);
		} catch (IllegalStateException | IOException e) {
			System.err.println("Could not report seesaw-lock");
			e.printStackTrace();
		}
	}

	public void unlockSeesaw() {
		try {
			client.unlockSeesaw();
		} catch (IllegalStateException | IOException e) {
			System.err.println("Could not report seesaw-unlock");
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

	/**
	 * Send a set of tiles to the partner.
	 * 
	 * @param tiles
	 */
	public void sendTiles(Tile... tiles) {
		sendTiles(Arrays.asList(tiles));
	}

	/**
	 * Send a set of tiles to the partner.
	 * 
	 * @param tiles
	 */
	public void sendTiles(Collection<Tile> tiles) {
		if (!hasPartner()) {
			// Partner not connected yet
			return;
		}

		List<peno.htttp.Tile> tilesToSend = new ArrayList<>(tiles.size());
		for (Tile tile : tiles) {
			// TODO Is this conform with maze coordinate specification?
			long x = tile.getX();
			long y = tile.getY();
			String token = Parser.stringify(localPlayer.getMaze(), tile.getPosition());
			tilesToSend.add(new peno.htttp.Tile(x, y, token));
		}

		try {
			if (client.hasTeamPartner()) {
				client.sendTiles(tilesToSend);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send all own explored tiles to the partner.
	 */
	public void sendOwnTiles() {
		sendTiles(getLocalMaze().getOwnMaze().getExploredTiles());
	}

	public boolean hasPartner() {
		return partnerPlayer != null;
	}

	public Player getPartner() {
		if (!hasPartner()) {
			throw new IllegalStateException("Partner still unknown.");
		}
		return partnerPlayer;
	}

	public boolean isPartner(String playerID) {
		return hasPartner() && getPartner().getPlayerID().equals(playerID);
	}

	private void createPartner(String partnerID) {
		if (hasPartner())
			return;

		// Create robot to track partner's position
		// Note: the size of the partner's robot are not important
		Robot partnerRobot = new ObservableRobot(0, 0);
		// Create partner
		RelativePlayer partner = new RelativePlayer(partnerID, partnerRobot, new Maze());
		partnerPlayer = partner;
		// Set partner maze
		getLocalMaze().setPartnerMaze(partner.getMaze());

		// Call listeners
		for (GameListener listener : listeners) {
			listener.onPartnerConnected(getPartner());
		}
	}

	private void removePartner() {
		if (!hasPartner())
			return;

		Player partner = getPartner();
		// Clear maze
		partner.getMaze().clear();
		// TODO Clear partner maze in total maze?
		// Unset partner
		partnerPlayer = null;

		// Call listeners
		for (GameListener listener : listeners) {
			listener.onPartnerDisconnected(partner);
		}
	}

	/**
	 * Resets the game.
	 */
	private void reset() {
		// Remove partner
		removePartner();
		// Clear own maze
		getLocalMaze().clear();
	}

	private class Handler implements PlayerHandler {

		@Override
		public void gameRolled(int playerNumber, int objectNumber) {
			for (GameListener listener : listeners) {
				listener.onGameRolled(playerNumber, objectNumber);
			}
		}

		@Override
		public void gameStarted() {
			// Reset game
			reset();
			// Call listeners
			for (GameListener listener : listeners) {
				listener.onGameStarted();
			}
		}

		@Override
		public void gameStopped() {
			for (GameListener listener : listeners) {
				listener.onGameStopped();
			}
		}

		@Override
		public void gamePaused() {
			for (GameListener listener : listeners) {
				listener.onGamePaused();
			}
		}

		@Override
		public void gameWon(int teamNumber) {
			for (GameListener listener : listeners) {
				listener.onGameWon(teamNumber);
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
			if (isPartner(playerID)) {
				// Partner disconnected
				removePartner();
			}
		}

		@Override
		public void playerReady(String playerID, boolean isReady) {
			for (GameListener listener : listeners) {
				listener.onPlayerReady(playerID, isReady);
			}
		}

		@Override
		public void playerFoundObject(String playerID, int playerNumber) {
			for (GameListener listener : listeners) {
				listener.onObjectFound(playerID);
			}
		}

		@Override
		public void teamConnected(String partnerID) {
			createPartner(partnerID);
		}

		@Override
		public void teamTilesReceived(List<peno.htttp.Tile> tiles) {
			for (peno.htttp.Tile tile : tiles) {
				try {
					// Parse tile
					Tile parsedTile = Parser.parseTile(tile.getX(), tile.getY(), tile.getToken());
					// Store in partner maze
					getPartner().getMaze().importTile(parsedTile);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void teamPosition(double x, double y, double angle) {
			if (hasPartner()) {
				// Update partner pose
				Pose pose = new Pose((float) x, (float) y, (float) angle);
				getPartner().getRobot().getPoseProvider().setPose(pose);
			}
		}

	}

}
