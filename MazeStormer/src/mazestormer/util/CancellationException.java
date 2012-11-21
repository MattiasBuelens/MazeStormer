package mazestormer.util;

/**
 * Exception indicating that the result of a value-producing task, such as a
 * {@link Future}, cannot be retrieved because the task was cancelled.
 */
public class CancellationException extends IllegalStateException {

	/**
	 * Constructs a <tt>CancellationException</tt> with no detail message.
	 */
	public CancellationException() {
	}

	/**
	 * Constructs a <tt>CancellationException</tt> with the specified detail
	 * message.
	 * 
	 * @param message
	 *            the detail message
	 */
	public CancellationException(String message) {
		super(message);
	}

}