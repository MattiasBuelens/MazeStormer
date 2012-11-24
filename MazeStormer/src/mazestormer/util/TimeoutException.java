package mazestormer.util;

/**
 * Exception thrown when a blocking operation times out. Blocking operations for
 * which a timeout is specified need a means to indicate that the timeout has
 * occurred. For many such operations it is possible to return a value that
 * indicates timeout; when that is not possible or desirable then
 * <tt>TimeoutException</tt> should be declared and thrown.
 */
public class TimeoutException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <tt>TimeoutException</tt> with no specified detail message.
	 */
	public TimeoutException() {
	}

	/**
	 * Constructs a <tt>TimeoutException</tt> with the specified detail message.
	 * 
	 * @param message
	 *            the detail message
	 */
	public TimeoutException(String message) {
		super(message);
	}

}