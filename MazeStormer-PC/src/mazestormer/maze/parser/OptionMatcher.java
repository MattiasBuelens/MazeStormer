package mazestormer.maze.parser;

import java.text.ParseException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mazestormer.maze.Orientation;

public enum OptionMatcher {

	/**
	 * Barcode.
	 * 
	 * <p>
	 * Matches a sequence of digits.
	 * </p>
	 */
	BARCODE("^(\\d+)$") {
		@Override
		public Option parse(MatchResult result) throws ParseException {
			String barcodeString = result.group();
			try {
				byte barcode = Byte.parseByte(barcodeString, 10);
				return new BarcodeOption(barcode);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid barcode: " + barcodeString, 0);
			}
		}
	},

	/**
	 * Object.
	 * 
	 * <p>
	 * Matches a single literal {@code V}.
	 * </p>
	 */
	OBJECT("^V$") {
		@Override
		public Option parse(MatchResult result) throws ParseException {
			return new ObjectOption();
		}
	},

	/**
	 * Start position.
	 * 
	 * <p>
	 * Matches a sequence of:
	 * <ol>
	 * <li>a literal {@code S};</li>
	 * <li>one digit from 0 through 3, denoting the player number;</li>
	 * <li>one letter for the orientation.</li>
	 * </ol>
	 * </p>
	 */
	START_POSITION("^S(\\d)([A-Z])$") {
		@Override
		public Option parse(MatchResult result) throws ParseException {
			// Player number
			int playerNumber;
			try {
				playerNumber = Integer.parseInt(result.group(1));
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid start position: " + result.group(), 0);
			}
			// Orientation
			Orientation orientation = Orientation.byShortName(result.group(2));
			if (orientation == null) {
				throw new ParseException("Invalid start position: " + result.group(), 0);
			}
			return new StartPositionOption(playerNumber, orientation);
		}
	};

	private OptionMatcher(Pattern pattern) {
		this.pattern = pattern;
	}

	private OptionMatcher(String regex) {
		this(Pattern.compile(regex));
	}

	private final Pattern pattern;

	/**
	 * Get a pattern matcher for the given input.
	 * 
	 * @param input
	 *            The input to match on.
	 * @return A pattern matcher for this option matcher.
	 */
	public Matcher matcher(CharSequence input) {
		return pattern.matcher(input);
	}

	/**
	 * Parse an option from a pattern match result.
	 * 
	 * @param result
	 *            The pattern match result.
	 * @return The constructed option.
	 */
	public abstract Option parse(MatchResult result) throws ParseException;
}