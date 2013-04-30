package mazestormer.world;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.IRRobot;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.maze.Seesaw;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.AbsolutePlayer;
import mazestormer.player.Player;
import mazestormer.player.RelativePlayer;
import mazestormer.util.LongPoint;
import peno.htttp.DisconnectReason;
import peno.htttp.PlayerDetails;
import peno.htttp.SpectatorClient;
import peno.htttp.SpectatorHandler;

import com.rabbitmq.client.Connection;

public class WorldSimulator {

	private final SpectatorClient client;
	private final SpectatorHandler handler;

	private final World world;
	private final Player localPlayer;
	private final String id;

	private final Set<Player> transformedPlayers = new HashSet<Player>();

	public WorldSimulator(Connection connection, String id, Player localPlayer, World world) throws IOException,
			IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer;
		this.world = world;

		// Start spectating
		this.handler = new Handler();
		this.client = new SpectatorClient(connection, this.handler, id);
		this.client.start();
	}

	public String getId() {
		return id;
	}

	public World getWorld() {
		return world;
	}

	public IMaze getMaze() {
		return getWorld().getMaze();
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}

	private boolean isLocalPlayer(String playerID) {
		return getLocalPlayer().getPlayerID().equals(playerID);
	}

	private synchronized AbsolutePlayer getOrAddPlayer(PlayerDetails playerDetails) {
		String playerID = playerDetails.getPlayerID();
		AbsolutePlayer player = getWorld().getPlayer(playerID);
		if (player == null) {
			// Create observable player
			IRRobot robot = new ObservableRobot(ModelType.fromPlayerType(playerDetails.getType()),
					playerDetails.getHeight(), playerDetails.getWidth());
			RelativePlayer relativePlayer = new RelativePlayer(playerID, robot, null);
			player = new AbsolutePlayer(relativePlayer);
			getWorld().addPlayer(player);
		}
		return player;
	}

	private void setupPlayerTransform(AbsolutePlayer player, int playerNumber) {
		// Ignore if already set
		if (transformedPlayers.contains(player))
			return;

		// Create transformation from start pose
		Pose startPose = getWorld().getMaze().getStartPose(playerNumber);
		player.setTransform(new PoseTransform(startPose));
		// Set as transformed
		transformedPlayers.add(player);
	}

	private void clearPlayerTransforms() {
		transformedPlayers.clear();
	}

	public void terminate() {
		// Stop spectating
		client.stop();
		// Reset state
		clearPlayerTransforms();
		// Reset world
		getWorld().removeOtherPlayers();
	}

	private class Handler implements SpectatorHandler {

		@Override
		public void gameStarted() {
			// TODO Reset seesaw states
		}

		@Override
		public void gameStopped() {
			// Reset player transforms
			clearPlayerTransforms();
		}

		@Override
		public void gamePaused() {
			// Nothing to do here
		}

		@Override
		public void gameWon(int teamNumber) {
			// Nothing to do here
		}

		@Override
		public void playerRolled(PlayerDetails playerDetails, int playerNumber) {
			AbsolutePlayer player = getOrAddPlayer(playerDetails);

			// Setup transformation if not set yet
			setupPlayerTransform(player, playerNumber);
		}

		@Override
		public void playerJoining(String playerID) {
			// Nothing to do here
		}

		@Override
		public void playerJoined(String playerID) {
			// Nothing to do here
		}

		@Override
		public void playerDisconnected(String playerID, DisconnectReason reason) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;

			// TODO Perhaps add GameListener.onPlayerTimeout() ?
			if (reason == DisconnectReason.LEAVE || reason == DisconnectReason.TIMEOUT) {
				AbsolutePlayer player = getWorld().getPlayer(playerID);
				if (player != null) {
					getWorld().removePlayer(player);
				}
			}
		}

		@Override
		public void playerReady(String playerID, boolean isReady) {
			// Nothing to do here
		}

		@Override
		public void playerFoundObject(String playerID, int playerNumber) {
			// Nothing to do here
		}

		@Override
		public void playerUpdate(PlayerDetails playerDetails, int playerNumber, long x, long y, double angle,
				boolean foundObject) {
			// Setup player if not done yet
			playerRolled(playerDetails, playerNumber);

			String playerID = playerDetails.getPlayerID();
			AbsolutePlayer player = getOrAddPlayer(playerDetails);

			// Set pose of non-local player
			if (!isLocalPlayer(playerID)) {
				// Transform tile position to absolute maze position
				Point relativePosition = getMaze().getTileCenter(new LongPoint(x, y));
				Point absolutePosition = getMaze().toAbsolute(relativePosition);
				Pose pose = new Pose();
				pose.setLocation(absolutePosition);
				pose.setHeading((float) angle);
				// Set relative pose
				player.setRelativePose(pose);
			}
		}

		@Override
		public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
			// Nothing to do here
		}

		public void unlockedSeesaw(String playerID, int playerNumber, int barcode) {
			// Flip this seesaw
			Seesaw seesaw = getWorld().getMaze().getOrCreateSeesaw((byte) barcode);
			seesaw.flip();
		}

	}

}
