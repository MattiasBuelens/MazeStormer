package mazestormer.world;

import java.io.IOException;

import com.rabbitmq.client.Connection;

import mazestormer.observable.ObservableRobot;
import mazestormer.player.Player;
import peno.htttp.DisconnectReason;
import peno.htttp.SpectatorClient;
import peno.htttp.SpectatorHandler;

public class WorldSimulator {
	
	private final SpectatorClient client;
	private final SpectatorHandler handler;
	
	private final World world;
	private final Player localPlayer;
	private final String id;
	
	public WorldSimulator(Connection connection, String id, Player localPlayer, World world)
			throws IOException, IllegalStateException {
		this.id = id;
		this.localPlayer = localPlayer;
		this.world = world;

		this.handler = new Handler();
		this.client = new SpectatorClient(connection, this.handler, id);
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
	
	private class Handler implements SpectatorHandler {

		@Override
		public void gameStarted() {
			// left empty
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
		public void playerJoined(String playerID) 
				throws NullPointerException {
			if(playerID == null)
				throw new NullPointerException("PlayerID may not refer the null refernce.");
			getWorld().addPlayer(new Player(playerID, new ObservableRobot()));
		}

		@Override
		public void playerJoining(String playerID) {
			// left empty
		}
		
		@Override
		public void playerDisconnected(String playerID, DisconnectReason reason) {
			// TODO Perhaps add GameListener.onPlayerTimeout() ?
			if(!getLocalPlayer().getPlayerID().equals(playerID)) {
				if (reason == DisconnectReason.LEAVE || reason == DisconnectReason.TIMEOUT) {
					getWorld().removePlayer(getWorld().getPlayer(playerID));
				}
			}
		}

		@Override
		public void playerReady(String playerID, boolean isReady) {
			// left empty
		}

		@Override
		public void playerFoundObject(String playerID, int playerNumber) {
			if(!getLocalPlayer().getPlayerID().equals(playerID)) {
				
			}
		}

		@Override
		public void playerUpdate(String playerID, int playerNumber, double x,
				double y, double angle, boolean foundObject) {
			if(!getLocalPlayer().getPlayerID().equals(playerID)) {
							
			}
		}
	}

}
