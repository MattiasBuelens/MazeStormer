package mazestormer.report;

import lejos.util.Delay;
import mazestormer.remote.Communicator;
import mazestormer.remote.MessageSender;
import mazestormer.robot.Pilot;

public class MovementReporter extends MessageSender<Report> implements Runnable {

	private Pilot pilot;

	private Thread thread;
	private boolean isRunning = false;
	private long delay = 100;

	public MovementReporter(Communicator<? super Report, ?> communicator,
			Pilot pilot) {
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
		report(new MoveReport(ReportType.MOVEMENT, pilot.getMovement()));
	}

}
