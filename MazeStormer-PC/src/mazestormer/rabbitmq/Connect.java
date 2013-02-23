package mazestormer.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Connect {
	
	public static Connection createConnection(ConnectionMode mode) {
		Connection connection = null;
		try {
			ConnectionFactory factory = mode.getConnectionFactory();
		    connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return connection;
	}
	
	public static Channel createChannel(Connection conn) {
		Channel channel = null;
		try {
			channel = conn.createChannel();
			channel.exchangeDeclare(Config.EXCHANGE_NAME, "topic");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return channel;
	}
	
	public static AMQP.Queue.DeclareOk getBoundQueue(Channel channel, String monitorKey) {
		AMQP.Queue.DeclareOk queue = null;
		try {
			queue = channel.queueDeclare();
			channel.queueBind(queue.getQueue(), Config.EXCHANGE_NAME, monitorKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return queue;
	}
}
