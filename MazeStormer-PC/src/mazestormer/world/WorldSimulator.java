package mazestormer.world;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.PoseTransform;
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
		// Reset state
		clearPlayerTransforms();
		// Stop spectating
		client.stop();
	}

	private class Handler implements SpectatorHandler {

		@Override
		public void gameStarted() {
			// Reset player transforms
			clearPlayerTransforms();
		}

		@Override
		public void gameStopped() {
			// left empty
		}

		@Override
		public void gamePaused() {
			// left emptyf
		}

		@Override
		public void gameWon(int teamNumber) {
			// left empty
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
			RelativePlayer relativePlayer = new RelativePlayer(playerID, new ObservableRobot());
			getWorld().addPlayer(new AbsolutePlayer(relativePlayer));
		}

		@Override
		public void playerDisconnected(String playerID, DisconnectReason reason) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;

			// TODO Perhaps add GameListener.onPlayerTimeout() ?
			if (reason == DisconnectReason.LEAVE || reason == DisconnectReason.TIMEOUT) {
				getWorld().removePlayer(getWorld().getPlayer(playerID));
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
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;

			Pose pose = new Pose((float) x, (float) y, (float) angle);

			// Setup transformation if not set yet
			AbsolutePlayer player = getWorld().getPlayer(playerID);
			setupPlayerTransform(player, playerNumber);

			// Set relative pose
			player.setRelativePose(pose);
		}

		@Override
		public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unlockedSeesaw(String playerID, int playerNumber, int barcode) {
			// TODO Auto-generated method stub

		}

	}

}
