package mazestormer.remote;

import java.io.DataInputStream;
import java.io.IOException;

public interface MessageReader<M extends Message> {

	public M read(DataInputStream dis) throws UnsupportedOperationException, IOException;

}
