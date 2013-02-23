package mazestormer.controller;

import java.io.IOException;
import java.util.Date;

import mazestormer.player.Game;
import mazestormer.rabbitmq.Config;
import mazestormer.rabbitmq.Connect;
import mazestormer.rabbitmq.ConnectionMode;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class GameSetUpController extends SubController implements IGameSetUpController {

	private Game game;
	
	private final Channel channel;
	
	public GameSetUpController(MainController mainController) {
		super(mainController);
		Connection connection = Connect.createConnection(ConnectionMode.LOCAL);
		channel = Connect.createChannel(connection);
	}

	@Override
	public void createGame(String gameID) {
		game = new Game(gameID);
		subscribeToGameList();
		subscribeToJoin();
		onJoin();
	}

	@Override
	public void joinGame(String gameID) {
		sendMessageTo("request", "race."+gameID+".join");
		// TODO Auto-generated method stub
		onJoin();
	}

	@Override
	public String[] refreshLobby() {
		// TODO Auto-generated method stub
		String[] lobby = {"one", "two"};
		return lobby;
	}

	@Override
	public void leaveGame() {
		// TODO Auto-generated method stub
		onLeave();
	}
	
	private boolean isReady() {
		// TODO
		if(getMainController().getPlayer().getRobot() == null) {
			return false;
		} else if(mazestormer.simulator.VirtualRobot.class.isInstance(getMainController().getPlayer().getRobot())
				&& getMainController().getSourceMaze().getNumberOfTiles() == 0) {
			return false;
		}
		return true;
	}
	
	private String getGameID() {
		return this.game.getId();
	}
	
	private void onJoin() {
		if(isReady())
			postState(GameSetUpEvent.EventType.JOINED);
		else
			onNotReady();
	}
	
	private void onLeave() {
		postState(GameSetUpEvent.EventType.LEFT);
	}
	
	private void onDisconnect() {
		postState(GameSetUpEvent.EventType.DISCONNECTED);
	}
	
	private void onNotReady() {
		postState(GameSetUpEvent.EventType.NOT_READY);
	}
	
	private void postState(GameSetUpEvent.EventType eventType) {
		postEvent(new GameSetUpEvent(eventType));
	}
	
	private void sendMessageTo(String message, String routingKey) {
		if (message != null) {
			try {
				channel.basicPublish(Config.EXCHANGE_NAME, routingKey, 
						null, message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void subscribeToGameList() {
		AMQP.Queue.DeclareOk listQueue = Connect.getBoundQueue(channel, "game."+getGameID()+".join");
		boolean noAck = false;
		try {
			channel.basicConsume(listQueue.getQueue(), noAck, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, 
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					long deliveryTag = envelope.getDeliveryTag();
					String replyToQueue = new String(body);
					sendMessageTo(getGameID(), replyToQueue);
					channel.basicAck(deliveryTag, false);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void subscribeToJoin() {
		AMQP.Queue.DeclareOk joinQueue = Connect.getBoundQueue(channel, Config.GAME_LIST);
		boolean noAck = false;
		try {
			channel.basicConsume(joinQueue.getQueue(), noAck, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, 
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					long deliveryTag = envelope.getDeliveryTag();
					String message = new String(body);
					if(message.equals("ready") && game.isJoinable())
						//TODO sendMessageTo(getGameID(), replyToQueue);
					channel.basicAck(deliveryTag, false);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
