package mazestormer.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Connect {
	
	public static Connection createConnection(ConnectionMode mode) throws IOException {
		ConnectionFactory factory = mode.getConnectionFactory();
	    Connection connection = factory.newConnection();
	    return connection;
	}
	
	public static Channel createChannel(Connection conn) throws IOException {
		Channel channel = conn.createChannel();
		channel.exchangeDeclare(Config.EXCHANGE_NAME, "topic");
		return channel;
	}
}
