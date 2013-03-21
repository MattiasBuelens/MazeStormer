package mazestormer.maze.parser;

import java.text.ParseException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenMatcher {

	/**
	 * New line.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>Zero or more spaces.</li>
	 * <li>Optionally: a carriage return.</li>
	 * <li>A new-line character.</li>
	 * </ol>
	 * </p>
	 */
	NEWLINE("\\s*\\r?\\n") {
		@Override
		public Token parse(MatchResult result) throws ParseException {
			return NewLineToken.parse();
		}
	},

	/**
	 * End of file.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>Zero or more spaces.</li>
	 * <li>The end of the input.</li>
	 * </ol>
	 * </p>
	 */
	EOF("\\s*$") {
		@Override
		public Token parse(MatchResult result) throws ParseException {
			return EOFToken.parse();
		}
	},

	/**
	 * Dimension.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>Preceded by one or more spaces or the start of the input.</li>
	 * <li>Optionally: a numerical sequence, denoting the value.</li>
	 * <li>Followed by a space or the end of the input.</li>
	 * </ol>
	 * </p>
	 */
	DIMENSION("(?:(?<=\\s+|^)|\\s+)(\\d+)(?=\\s|$)") {
		@Override
		public Token parse(MatchResult result) throws ParseException {
			return DimensionToken.parse(result.group(1));
		}
	},

	/**
	 * Tile.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>Preceded by one or more spaces or the start of the input.</li>
	 * <li>An alphabetical sequence, denoting the tile type.</li>
	 * <li>Optionally: an alphabetical sequence, denoting the orientation.</li>
	 * <li>Optionally: an alphanumerical sequence, denoting the option. See
	 * {@link OptionMatcher}.</li>
	 * <li>Followed by a space or the end of the input.</li>
	 * </ol>
	 * </p>
	 */
	TILE("(?:(?<=\\s+|^)|\\s+)([a-z]+)(?:\\.([a-z]+))?(?:\\.(\\w+))?(?=\\s|$)") {
		@Override
		public Token parse(MatchResult result) throws ParseException {
			int nbGroups = result.groupCount();
			String typeString = result.group(1);
			String orientationString = nbGroups > 1 ? result.group(2) : null;
			String optionString = nbGroups > 2 ? result.group(3) : null;

			return TileToken.parse(typeString, orientationString, optionString);
		}
	},

	/**
	 * Comment.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>Zero or more spaces.</li>
	 * <li>A hash symbol.</li>
	 * <li>Zero or more characters, differing from a carriage return or a
	 * new-line.</li>
	 * </ol>
	 * </p>
	 */
	COMMENT("\\s*#([^\\r\\n]*)") {
		@Override
		public Token parse(MatchResult result) throws ParseException {
			return CommentToken.parse(result.group(1));
		}
	};

	private TokenMatcher(Pattern pattern) {
		this.pattern = pattern;
	}

	private TokenMatcher(String regex) {
		this(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
	}

	private final Pattern pattern;

	/**
	 * Get a pattern matcher for the given input.
	 * 
	 * @param input
	 *            The input to match on.
	 * @return A pattern matcher for this token matcher.
	 */
	public Matcher matcher(CharSequence input) {
		return pattern.matcher(input);
	}

	/**
	 * Parse a token from a pattern match result.
	 * 
	 * @param result
	 *            The pattern match result.
	 * 
	 * @return The constructed token.
	 */
	public abstract Token parse(MatchResult result) throws ParseException;

}