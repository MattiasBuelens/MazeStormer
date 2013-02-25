package mazestormer.rabbitmq;

import java.io.IOException;

import mazestormer.player.Game;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MQChannel {
	
	private final Connection connection;
	private final Channel channel;

	public MQChannel(ConnectionMode mode) {
		connection = Connect.createConnection(ConnectionMode.LOCAL);
		channel = Connect.createChannel(connection);
	}
	
	public void sendMessageTo(String message, String routingKey) {
		if (message != null) {
			try {
				channel.basicPublish(Config.EXCHANGE_NAME, routingKey, 
						null, message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addToGameList(final Game game) {
		AMQP.Queue.DeclareOk listQueue = Connect.getBoundQueue(channel, Config.GAME_LIST);
		boolean noAck = false;
		try {
			channel.basicConsume(listQueue.getQueue(), noAck, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, 
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					long deliveryTag = envelope.getDeliveryTag();
					String replyToQueue = new String(body);
					sendMessageTo(game.getId(), replyToQueue);
					channel.basicAck(deliveryTag, false);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribeGameToJoin(final Game game) {
		AMQP.Queue.DeclareOk joinQueue = Connect.getBoundQueue(channel, "game."+game.getId()+".join");
		boolean noAck = false;
		try {
			channel.basicConsume(joinQueue.getQueue(), noAck, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, 
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					long deliveryTag = envelope.getDeliveryTag();
					String message = new String(body);
					if(message.equals("request") && game.isJoinable())
						//TODO sendMessageTo(getGameID(), replyToQueue);
					channel.basicAck(deliveryTag, false);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
