package mazestormer.report;

import lejos.util.Delay;
import mazestormer.remote.MessageSender;
import mazestormer.remote.NXTCommunicator;
import mazestormer.robot.Pilot;

public class MovementReporter extends MessageSender<Report<?>> implements
		Runnable {

	private Pilot pilot;

	private Thread thread;
	private boolean isRunning = false;
	private long delay = Pilot.movementReportFrequency;

	public MovementReporter(NXTCommunicator communicator, Pilot pilot) {
		super(communicator);
		this.pilot = pilot;
	}

	public void start() {
		if (isRunning)
			return;

		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		isRunning = false;
		thread = null;
	}

	@Override
	public void run() {
		while (isRunning) {
			if (pilot.isMoving()) {
				reportMovement();
			}
			Delay.msDelay(delay);
		}
	}

	public void reportMovement() {
		send(new MoveReport(ReportType.MOVEMENT, pilot.getMovement()));
	}

}
