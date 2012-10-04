package mazestormer.nxt.comm;

import java.io.*;

import lejos.nxt.comm.*;
import mazestormer.comm.Message;

public class Communicator implements Runnable {

	private final NXTConnection connection;
	private boolean isListening = false;
	private Thread thread;

	public Communicator(int timeout) {
		// Initialize connection
		connection = Bluetooth.waitForConnection(timeout, NXTConnection.PACKET);
	}

	public Communicator() {
		this(5000);
	}

	public boolean isListening() {
		return isListening;
	}

	public void startListening() {
		isListening = true;

		// Start thread
		thread = new Thread(this);
		thread.start();
	}

	public void stopListening() {
		isListening = false;

		// Stop thread
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}

	public void send(Message message, byte[] data) throws IOException {
		DataOutputStream out = connection.openDataOutputStream();
		out.write(message.getCode());
		out.write(data);
	}

	@Override
	public void run() throws IllegalStateException {
		DataInputStream in = connection.openDataInputStream();

		while (isListening()) {
			try {
				byte code = in.readByte();
				Message message = Message.byCode(code);
				if (message == null) {
					throw new IllegalStateException("Unknown message code.");
				}
			} catch (IOException e) {
				stopListening();
			} catch (IllegalStateException e) {
				stopListening();
				throw e;
			}
		}
	}

}
