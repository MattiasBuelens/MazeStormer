package mazestormer.world;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.maze.PoseTransform;
import mazestormer.maze.Seesaw;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.Player;
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

	private final Map<Integer, PoseTransform> playerTransforms = new HashMap<Integer, PoseTransform>();

	public WorldSimulator(Connection connection, String id, Player localPlayer,
			World world) throws IOException, IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer;
		this.world = world;

		// Start spectating
		this.handler = new Handler();
		this.client = new SpectatorClient(connection, this.handler, id);
		this.client.start();
	}

	public String getId() {
		return this.id;
	}

	public World getWorld() {
		return this.world;
	}

	public Player getLocalPlayer() {
		return this.localPlayer;
	}

	private boolean isLocalPlayer(String playerID) {
		return getLocalPlayer().getPlayerID().equals(playerID);
	}

	private Pose transformPlayerPose(int playerNumber, Pose pose) {
		return getPlayerTransform(playerNumber).transform(pose);
	}

	private PoseTransform getPlayerTransform(int playerNumber) {
		// Look up cached transformation
		PoseTransform transform = playerTransforms.get(playerNumber);
		if (transform == null) {
			// Create transformation from start pose
			Pose startPose = getWorld().getMaze().getStartPose(playerNumber);
			transform = new PoseTransform(startPose);
			// Store transformation for reuse
			playerTransforms.put(playerNumber, transform);
		}
		return transform;
	}

	private void clearPlayerTransforms() {
		playerTransforms.clear();
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
			// left empty
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

			getWorld().addPlayer(new Player(playerID, new ObservableRobot()));
		}

		@Override
		public void playerDisconnected(String playerID, DisconnectReason reason) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;

			// TODO Perhaps add GameListener.onPlayerTimeout() ?
			if (reason == DisconnectReason.LEAVE
					|| reason == DisconnectReason.TIMEOUT) {
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
		public void playerUpdate(String playerID, int playerNumber, double x,
				double y, double angle, boolean foundObject) {
			// Ignore local player
			if (isLocalPlayer(playerID))
				return;

			Pose pose = new Pose((float) x, (float) y, (float) angle);
			// Transform
			pose = transformPlayerPose(playerNumber, pose);
			// Set pose
			getWorld().getPlayer(playerID).getRobot().getPoseProvider()
					.setPose(pose);
		}

		@Override
		public void lockedSeesaw(String playerID, int playerNumber, int barcode) {
			// Deze methode mag leeg blijven, keitof!

		}

		@Override
		public void unlockedSeesaw(String playerID, int playerNumber,
				int barcode) {
			Seesaw seesaw = getWorld().getMaze().getSeesaw(
					new Barcode((byte) barcode));
			seesaw.flip();
		}

		@Override
		public void playerRolled(String playerID, int playerNumber) {
			// leeg te blijven?

		}

	}

}
