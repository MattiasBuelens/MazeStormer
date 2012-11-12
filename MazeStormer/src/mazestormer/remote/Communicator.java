package mazestormer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.robotics.Transmittable;

public abstract class Communicator {

	private DataInputStream dis;
	private DataOutputStream dos;

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

	public Communicator(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
	}

	public Communicator(InputStream is, OutputStream os) {
		this(new DataInputStream(is), new DataOutputStream(os));
	}

	public synchronized void send(Transmittable packet) throws IOException {
		packet.dumpObject(dos());
	}

	public abstract Transmittable receive() throws IOException;

	public void terminate() throws IOException {
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
