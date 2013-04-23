package mazestormer.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

		@Override
		public String toString() {
			return "Localhost";
		}
	},
	PENO {
		@Override
		public ConnectionFactory createConnectionFactory() {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("guest");
			factory.setPassword("guest");
			factory.setVirtualHost("/");
			factory.setHost("leuven.cs.kotnet.kuleuven.be");
			factory.setPort(5672);
			factory.setRequestedHeartbeat(0);
			return factory;
		}

		@Override
		public String toString() {
			return "P&O";
		}
	};

	private final ConnectionFactory connectionFactory;
	private Connection connection;

	private ConnectionMode() {
		this.connectionFactory = createConnectionFactory();
	}

	public Connection getConnection() throws IOException {
		if (connection == null || !connection.isOpen()) {
			connection = connectionFactory.newConnection();
		}
		return connection;
	}

	protected abstract ConnectionFactory createConnectionFactory();

	public static List<String> getNames() {
		List<String> names = new ArrayList<>();
		for (ConnectionMode mode : values()) {
			names.add(mode.name());
		}
		return names;
	}

}
