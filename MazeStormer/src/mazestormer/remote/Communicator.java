package mazestormer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class Communicator<S extends Message, R extends Message>
		implements Runnable {

	private boolean isListening = false;
	private boolean isTerminated = false;
	private Thread thread;

	private DataInputStream dis;
	private DataOutputStream dos;
	private final MessageReader<? extends R> reader;

	public Communicator(DataInputStream dis, DataOutputStream dos,
			MessageReader<? extends R> reader) {
		this.dis = dis;
		this.dos = dos;
		this.reader = reader;
	}

	public Communicator(InputStream is, OutputStream os,
			MessageReader<? extends R> reader) {
		this(new DataInputStream(is), new DataOutputStream(os), reader);
	}

	public MessageReader<? extends R> getReader() {
		return reader;
	}

	public boolean isListening() {
		return isListening;
	}

	public synchronized void setListening(boolean isListening) {
		this.isListening = isListening;
	}

	/**
	 * Start the communicator and process the incoming messages.
	 */
	public void start() {
		if (isTerminated()) {
			throw new IllegalStateException("Communicator already terminated.");
		}

		beforeStart();
		setListening(true);
		thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
	}

	/**
	 * Stop the communicator.
	 * 
	 * <p>
	 * This method does not close the connection, and the communicator can be
	 * restarted later. To stop the communicator and shut down the connection,
	 * call {@link #terminate()}.
	 * </p>
	 */
	public void stop() {
		if (isTerminated()) {
			throw new IllegalStateException("Communicator already terminated.");
		}

		beforeStop();
		setListening(false);
	}

	/**
	 * Wait until the communicator to stop.
	 */
	public void waitComplete() {
		if (isListening()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Triggered before starting the communicator.
	 * 
	 * <p>
	 * Subclasses can override this method to implement custom startup
	 * procedures. The default implementation does nothing.
	 * </p>
	 */
	protected void beforeStart() {
	}

	/**
	 * Triggered before stopping the communicator.
	 * 
	 * <p>
	 * Subclasses can override this method to implement custom shutdown
	 * procedures. The default implementation does nothing.
	 * </p>
	 */
	protected void beforeStop() {
	}

	@Override
	public void run() {
		while (isListening()) {
			try {
				R message = getReader().read(dis);
				trigger(message);
			} catch (IOException e) {
				stop();
				break;
			}
		}
	}

	public synchronized void send(S message) throws IOException {
		if (isListening()) {
			message.write(dos);
			dos.flush();
		}
	}

	/**
	 * Adds a listener which will receive incoming messages.
	 * 
	 * @param listener
	 *            The new listener.
	 */
	public void addListener(MessageListener<? super R> listener) {
		getListeners().add(listener);
	}

	/**
	 * Removes a message listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(MessageListener<? super R> listener) {
		getListeners().remove(listener);
	}

	/**
	 * Triggers all registered message listeners.
	 * 
	 * @param message
	 *            The received message.
	 */
	public void trigger(final R message) {
		// Call listeners
		for (MessageListener<? super R> listener : getListeners()) {
			listener.messageReceived(message);
		}
	}

	protected abstract List<MessageListener<? super R>> getListeners();

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() throws IOException {
		stop();
		if (dis != null) {
			dis.close();
			dis = null;
		}
		if (dos != null) {
			dos.close();
			dos = null;
		}
	}

}
