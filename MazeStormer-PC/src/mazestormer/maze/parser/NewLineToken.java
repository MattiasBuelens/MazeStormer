package mazestormer.maze.parser;

public final class NewLineToken implements Token {

	private static final NewLineToken instance = new NewLineToken();

	private NewLineToken() {
	}

	public static NewLineToken parse() {
		return instance;
	}

}
