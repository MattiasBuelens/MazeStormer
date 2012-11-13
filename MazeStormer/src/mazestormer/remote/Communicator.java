package mazestormer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Communicator<S extends Message, R extends Message>
		implements Runnable {

	private boolean isListening = false;
	private boolean isTerminated = false;
	private Thread thread;

	private DataInputStream dis;
	private DataOutputStream dos;

	private List<MessageListener<? super R>> listeners = new ArrayList<MessageListener<? super R>>();

	public Communicator(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
	}

	public Communicator(InputStream is, OutputStream os) {
		this(new DataInputStream(is), new DataOutputStream(os));
	}

	protected final DataInputStream dis() {
		if (dis == null) {
			throw new IllegalStateException("Input stream is closed.");
		}
		return dis;
	}

	private final DataOutputStream dos() {
		if (dos == null) {
			throw new IllegalStateException("Output stream is closed.");
		}
		return dos;
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
		thread = new Thread(this, getClass().getSimpleName());
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
				R message = receive();
				triggerListeners(message);
			} catch (IOException e) {
				stop();
				break;
			}
		}
	}

	public synchronized void send(S message) throws IOException {
		message.write(dos());
	}

	public R receive() throws IllegalStateException, IOException {
		// Read message type
		int typeId = dis().readInt();
		MessageType<? extends R> type = getType(typeId);
		if (type == null) {
			throw new IllegalStateException("Unknown message type identifier: "
					+ typeId);
		}

		// Read message
		R message = type.build();
		message.read(dis());
		return message;
	}

	public abstract MessageType<? extends R> getType(int typeId);

	public void addListener(MessageListener<? super R> listener) {
		listeners.add(listener);
	}

	public void removeListener(MessageListener<? super R> listener) {
		listeners.remove(listener);
	}

	protected void triggerListeners(R message) {
		for (MessageListener<? super R> listener : listeners) {
			listener.messageReceived(message);
		}
	}

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
