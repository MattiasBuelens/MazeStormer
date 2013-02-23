package mazestormer.rabbitmq;

public interface Config {
	
	public static final String USER_NAME = "guest";
	public static final String PASSWORD = "guest";
	public static final String VIRTUAL_HOST = "/";
	public static final String HOST_NAME = "leuven.cs.kotnet.kuleuven.be";
	public static final int PORT = 5672;
	
	public static final String LOCAL_HOST = "localhost";
	
	// the default exchange that will be used for races during the official demo's
	public static final String EXCHANGE_NAME = "RaceExchange";
	public static final String LAUNCH_ROUTING_KEY = "race.launch";
	
	public static final String GAME_LIST = "game.list";

}
