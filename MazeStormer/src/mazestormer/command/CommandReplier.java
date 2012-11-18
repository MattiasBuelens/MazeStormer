package mazestormer.command;

import mazestormer.remote.Communicator;
import mazestormer.remote.MessageListener;
import mazestormer.remote.MessageSender;
import mazestormer.remote.MessageType;
import mazestormer.report.Report;
import mazestormer.report.RequestReport;

public abstract class CommandReplier<V> extends MessageSender<RequestReport<V>>
		implements MessageListener<Command> {

	public CommandReplier(
			Communicator<? super RequestReport<V>, ? extends Command> communicator) {
		super(communicator);
	}

	protected void reply(RequestCommand<V> request, V value) {
		send(request.createResponse(getResponseType(request.getType()), value));
	}

	protected abstract MessageType<Report<?>> getResponseType(
			MessageType<Command> requestType);

}
