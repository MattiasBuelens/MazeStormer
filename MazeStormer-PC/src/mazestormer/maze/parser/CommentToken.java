package mazestormer.maze.parser;

public final class CommentToken implements Token {

	private final String comment;

	private CommentToken(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public static Token parse(String comment) {
		return new CommentToken(comment);
	}

}
