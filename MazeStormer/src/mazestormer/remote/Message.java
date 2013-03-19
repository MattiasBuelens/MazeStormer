package mazestormer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Message {

	public void read(DataInputStream dis) throws IOException;

	public void write(DataOutputStream dos) throws IOException;

}
