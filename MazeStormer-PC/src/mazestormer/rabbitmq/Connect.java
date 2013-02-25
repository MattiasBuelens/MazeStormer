package mazestormer.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Connect {

	public static Connection createConnection(ConnectionMode mode)
			throws IOException {
		ConnectionFactory factory = mode.getConnectionFactory();
		return factory.newConnection();
	}

}
