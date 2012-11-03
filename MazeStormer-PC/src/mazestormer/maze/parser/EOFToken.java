package mazestormer.maze.parser;

public class EOFToken implements Token {

	private static final EOFToken instance = new EOFToken();

	private EOFToken() {
	}

	public static EOFToken parse() {
		return instance;
	}

}
