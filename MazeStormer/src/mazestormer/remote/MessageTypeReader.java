package mazestormer.remote;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class MessageTypeReader<M extends Message> implements MessageReader<M> {

	@Override
	public M read(DataInputStream dis) throws UnsupportedOperationException, IOException {
		// Read message type
		int typeId = dis.readInt();
		MessageType<? extends M> type;
		try {
			type = getType(typeId);
		} catch (Exception e) {
			type = null;
		}
		if (type == null) {
			throw new UnsupportedOperationException("Unk msg " + typeId);
		}

		// Read message
		M message = type.build();
		message.read(dis);
		return message;
	}

	public abstract MessageType<? extends M> getType(int typeId);

}
