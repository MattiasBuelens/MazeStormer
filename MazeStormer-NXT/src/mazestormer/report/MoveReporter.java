package mazestormer.report;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import mazestormer.remote.MessageSender;
import mazestormer.remote.NXTCommunicator;

public class MoveReporter extends MessageSender<Report<?>> implements
		MoveListener {

	public MoveReporter(NXTCommunicator communicator) {
		super(communicator);
	}

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		send(new MoveReport(ReportType.MOVE_STARTED, event));
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		send(new MoveReport(ReportType.MOVE_STOPPED, event));
	}

}
