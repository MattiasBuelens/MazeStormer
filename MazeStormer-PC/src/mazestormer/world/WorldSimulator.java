package mazestormer.world;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.PoseTransform;
import mazestormer.maze.Seesaw;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.AbsolutePlayer;
import mazestormer.player.Player;
import mazestormer.player.RelativePlayer;
import peno.htttp.DisconnectReason;
import peno.htttp.SpectatorClient;
import peno.htttp.SpectatorHandler;

import com.rabbitmq.client.Connection;

public class WorldSimulator {

	private final SpectatorClient client;
	private final SpectatorHandler handler;

	private final World world;
	private final Player localPlayer;
	private final String id;

	private final Set<Integer> transformedPlayers = new HashSet<Integer>();

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

	public Player getLocalPlayer() {
		return localPlayer;
	}

	private boolean isLocalPlayer(String playerID) {
		return getLocalPlayer().getPlayerID().equals(playerID);
	}

	private synchronized AbsolutePlayer getOrAddPlayer(String playerID) {
		AbsolutePlayer player = getWorld().getPlayer(playerID);
		if (player == null) {
			RelativePlayer relativePlayer = new RelativePlayer(playerID, new ObservableRobot(), null);
			player = new AbsolutePlayer(relativePlayer);
			getWorld().addPlayer(player);
		}
		return player;
	}

	private void setupPlayerTransform(AbsolutePlayer player, int playerNumber) {
		// Ignore if already set
		if (transformedPlayers.contains(playerNumber))
			return;

		// Create transformation from start pose
		Pose startPose = getWorld().getMaze().getStartPose(playerNumber);
		player.setTransform(new PoseTransform(startPose));
		// Set as transformed
		transformedPlayers.add(playerNumber);
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
			// left empty
		}

		@Override
		public void gameWon(int teamNumber) {
			// left empty
		}

		@Override
		public void playerRolled(String playerID, int playerNumber) {
			// Setup transformation if not set yet
			AbsolutePlayer player = getOrAddPlayer(playerID);
			setupPlayerTransform(player, playerNumber);
		}

		@Override
		public void playerJoining(String playerID) {
			// left empty
		}

		@Override
		public void playerJoined(String playerID) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;
			// Store player
			getOrAddPlayer(playerID);
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
			// left empty
		}

		@Override
		public void playerFoundObject(String playerID, int playerNumber) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;
		}

		@Override
		public void playerUpdate(String playerID, int playerNumber, double x, double y, double angle,
				boolean foundObject) {
			AbsolutePlayer player = getOrAddPlayer(playerID);

			// Setup transformation if not set yet
			playerRolled(playerID, playerNumber);

			// Set pose of non-local player
			if (!isLocalPlayer(playerID)) {
				Pose pose = new Pose((float) x, (float) y, (float) angle);

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
