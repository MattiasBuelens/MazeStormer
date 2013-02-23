package mazestormer.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

public enum ConnectionMode {
	
	LOCAL {
		@Override
		public ConnectionFactory getConnectionFactory() {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(Config.LOCAL_HOST);
		    return factory;
		}
	}, PENO {
		@Override
		public ConnectionFactory getConnectionFactory() {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(Config.USER_NAME);
			factory.setPassword(Config.PASSWORD);
			factory.setVirtualHost(Config.VIRTUAL_HOST);
			factory.setRequestedHeartbeat(0);
			factory.setHost(Config.HOST_NAME);
			factory.setPort(Config.PORT);
			return factory;
		}
	};
	
	public abstract ConnectionFactory getConnectionFactory();

}
