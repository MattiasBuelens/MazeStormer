package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.regex.Matcher;

import com.google.common.base.Predicate;

public class Tokenizer {

	private final CharSequence source;
	private int lastIndex = 0;

	public Tokenizer(CharSequence source) {
		this.source = checkNotNull(source);
	}

	/**
	 * Skip until the next token that matches the given predicate.
	 * 
	 * @param predicate
	 * 			The predicate.
	 * 
	 * @return The next matching token, or null if none left.
	 * @throws ParseException
	 */
	public Token skipUntil(Predicate<Token> predicate) throws ParseException {
		Token token = null;

		// Skip until predicate matches
		do {
			token = nextToken();
		} while (predicate.apply(token));

		// Check if past EOF
		if (token == null)
			return throwEOF();

		// Return found token
		return token;
	}

	/**
	 * Skip until the next token that is not a comment.
	 * 
	 * @return The next token, or null if none left.
	 * @throws ParseException
	 */
	public Token skipComments() throws ParseException {
		return skipUntil(new Predicate<Token>() {
			public boolean apply(Token token) {
				return token instanceof CommentToken;
			}
		});
	}

	/**
	 * Skip until the next token that is not a comment or a new-line.
	 * 
	 * @return The next token, or null if none left.
	 * @throws ParseException
	 */
	public Token skipCommentsAndNewLines() throws ParseException {
		return skipUntil(new Predicate<Token>() {
			public boolean apply(Token token) {
				return token instanceof CommentToken || token instanceof NewLineToken;
			}
		});
	}

	/**
	 * Get the next dimension token.
	 * 
	 * @param skipCommentsAndNewLines
	 * 			Whether comments and new-lines can be skipped.
	 * 
	 * @return	The next dimension token.
	 * @throws	ParseException
	 * 			If the found token is not a dimension token.
	 */
	public DimensionToken getDimensionToken(boolean skipCommentsAndNewLines) throws ParseException {
		Token token = skipCommentsAndNewLines ? skipCommentsAndNewLines() : nextToken();
		return checkToken(DimensionToken.class, token);
	}

	/**
	 * Get the next tile token.
	 * 
	 * @param skipComments
	 * 			Whether comments can be skipped.
	 * 
	 * @return	The next tile token.
	 * @throws	ParseException
	 * 			If the found token is not a tile token.
	 */
	public TileToken getTileToken(boolean skipCommentsAndNewLines) throws ParseException {
		Token token = skipCommentsAndNewLines ? skipCommentsAndNewLines() : nextToken();
		return checkToken(TileToken.class, token);
	}

	/**
	 * Get the next new-line token, skipping comments if necessary.
	 * 
	 * @return	The next new-line token.
	 * @throws	ParseException
	 * 			If the found token is not a new-line token.
	 */
	public NewLineToken getNewLineToken(boolean skipComments) throws ParseException {
		Token token = skipComments ? skipComments() : nextToken();
		return checkToken(NewLineToken.class, token);
	}

	/**
	 * Get the next end-of-file token, skipping comments if necessary.
	 * 
	 * @return	The next end-of-file token.
	 * @throws	ParseException
	 * 			If the found token is not a end-of-file token.
	 */
	public EOFToken getEOFToken() throws ParseException {
		Token token = skipCommentsAndNewLines();
		return checkToken(EOFToken.class, token);
	}

	/**
	 * Check whether the given token matches the given type
	 * and throw a parse exception if it doesn't.
	 * 
	 * @param token
	 * 			The token to check.
	 * @param tokenType
	 * 			The expected token type.
	 * 
	 * @return The checked token.
	 * @throws ParseException
	 * 			If the given token is not of the given type.
	 */
	private <T extends Token> T checkToken(Class<T> tokenType, Token token) throws ParseException {
		// Check if past EOF
		if (token == null)
			return throwEOF();

		// Check token type
		if (tokenType.isAssignableFrom(token.getClass())) {
			return tokenType.cast(token);
		} else {
			return throwUnexpected(tokenType, token.getClass());
		}
	}

	/**
	 * Get the next token.
	 * 
	 * @return The next token, or null if none left.
	 * @throws ParseException
	 * 			If the next token could not be parsed.
	 */
	private Token nextToken() throws ParseException {
		try {
			// Check all token matchers for a match
			for (TokenMatcher tokenMatcher : TokenMatcher.values()) {
				Matcher matcher = tokenMatcher.matcher(source);
				// Find a match starting at the last index
				if (matcher.find(lastIndex) && lastIndex == matcher.start()) {
					// Parse token
					Token token = tokenMatcher.parse(matcher.toMatchResult());
					// Move index after last match
					lastIndex = matcher.end();
					return token;
				}
			}
		} catch (ParseException e) {
			return throwParseException(e);
		}

		return null;
	}

	private <T> T throwParseException(ParseException cause) throws ParseException {
		throw new ParseException(cause.getMessage(), lastIndex + cause.getErrorOffset());
	}

	private <T> T throwUnexpected(Class<?> expected, Class<?> found) throws ParseException {
		throw new ParseException("Expected " + expected.getSimpleName() + ", found: " + found.getSimpleName(),
				lastIndex);
	}

	private <T> T throwEOF() throws ParseException {
		throw new ParseException("Attempted to read past end of file.", lastIndex);
	}

}