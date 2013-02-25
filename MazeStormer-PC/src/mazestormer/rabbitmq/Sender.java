package mazestormer.rabbitmq;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.Futures;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.tools.json.JSONWriter;

public class Sender {

	private final Connection connection;
	private final Channel channel;
	private final String exchange;
	private final String playerID;
	private boolean isHost;

	public Sender(ConnectionMode mode, String exchange, String playerID)
			throws IOException {
		this.connection = Connect.createConnection(ConnectionMode.LOCAL);
		this.channel = connection.createChannel();
		this.exchange = exchange;
		this.playerID = playerID;
		setup();
	}

	private void send(Map<String, Object> message, String routingKey)
			throws IOException, NullPointerException {
		if (message == null) {
			throw new NullPointerException("Message cannot be null.");
		}

		// Serialize map as JSON object
		String jsonMessage = new JSONWriter().write(message);

		// Publish message
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
				.timestamp(new Date()).contentType("text/plain")
				.deliveryMode(1).build();
		channel.basicPublish(exchange, routingKey, props,
				jsonMessage.getBytes());
	}

	private void setup() throws IOException {
		try {
			channel.exchangeDeclarePassive(exchange);
			// Exchange exists
			isHost = false;
		} catch (IOException e) {
			// Exchange does not exist
			channel.exchangeDeclare(exchange, "topic");
			isHost = true;
			// Create host handler...
		}
	}

	public Future<Boolean> join() throws IOException {
		if (isHost) {
			// Host already in game
			return Futures.immediateFuture(true);
		} else {
			Map<String, Object> message = new HashMap<String, Object>();
			message.put("playerID", playerID);
			send(message, "join");
			// TODO Wait for result in future
			return null;
		}
	}

	public boolean leave() {
		if (isHost) {
			// Host already in game
			stop();
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	private void stop() {
		// TODO Auto-generated method stub

	}

	private void shutdown() {
		try {
			this.channel.close();
			this.connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
