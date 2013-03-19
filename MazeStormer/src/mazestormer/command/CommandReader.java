package mazestormer.command;

import mazestormer.remote.MessageTypeReader;

public class CommandReader extends MessageTypeReader<Command> {

	@Override
	public CommandType getType(int typeId) {
		return CommandType.values()[typeId];
	}

}
