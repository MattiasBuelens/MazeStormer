package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionReader;
import mazestormer.remote.Message;
import mazestormer.remote.MessageReader;

public class ConditionalCommand extends RequestCommand<Void> {

	private Condition condition;
	private List<Command> commands = new ArrayList<Command>();

	public ConditionalCommand(CommandType type) {
		super(type);
	}

	public ConditionalCommand(CommandType type, Condition condition) {
		this(type);
		setCondition(condition);
	}

	public ConditionalCommand(CommandType type, Condition condition,
			Collection<? extends Command> commands) {
		this(type, condition);
		addCommands(commands);
	}

	public ConditionalCommand(CommandType type, Condition condition,
			Command... commands) {
		this(type, condition);
		addCommands(commands);
	}

	public Condition getCondition() {
		return condition;
	}

	private void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	private void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public void addCommand(Command command) {
		this.commands.add(command);
	}

	public void addCommands(Collection<? extends Command> commands) {
		this.commands.addAll(commands);
	}

	public void addCommands(Command... commands) {
		addCommands(Arrays.asList(commands));
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setCondition(new ConditionReader().read(dis));
		setCommands(readList(dis, new CommandReader()));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		getCondition().write(dos);
		writeList(dos, getCommands());
	}

	private <M extends Message> List<M> readList(DataInputStream dis,
			MessageReader<M> reader) throws IOException {
		int count = dis.readInt();
		List<M> result = new ArrayList<M>(count);
		while (count-- > 0) {
			result.add(reader.read(dis));
		}
		return result;
	}

	private <M extends Message> void writeList(DataOutputStream dos,
			List<M> list) throws IOException {
		dos.writeInt(list.size());
		for (M message : list) {
			message.write(dos);
		}
	}

}
