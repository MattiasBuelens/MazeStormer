package mazestormer.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public enum ConnectionMode {

	LOCAL {
		@Override
		public ConnectionFactory createConnectionFactory() {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			return factory;
		}
	},
	PENO {
		@Override
		public ConnectionFactory createConnectionFactory() {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(PenoConfig.USER_NAME);
			factory.setPassword(PenoConfig.PASSWORD);
			factory.setVirtualHost(PenoConfig.VIRTUAL_HOST);
			factory.setRequestedHeartbeat(0);
			factory.setHost(PenoConfig.HOST_NAME);
			factory.setPort(PenoConfig.PORT);
			return factory;
		}
	};

	private final ConnectionFactory connectionFactory;

	private ConnectionMode() {
		this.connectionFactory = createConnectionFactory();
	}

	public Connection newConnection() throws IOException {
		return connectionFactory.newConnection();
	}

	protected abstract ConnectionFactory createConnectionFactory();

}
