package mazestormer.maze.parser;

import java.text.ParseException;

public class DimensionToken implements Token {

	private final long value;

	private DimensionToken(long value) {
		this.value = value;
	}

	/**
	 * Get the value of this dimension token.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Parse a dimension token.
	 * 
	 * @param valueString
	 * 			The string representation of the dimension's value.
	 * 
	 * @return	The parsed dimension token.
	 * @throws	ParseException
	 */
	public static DimensionToken parse(String valueString) throws ParseException {
		try {
			long value = Long.parseLong(valueString, 10);
			return new DimensionToken(value);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid dimension: " + valueString, 0);
		}
	}
}
