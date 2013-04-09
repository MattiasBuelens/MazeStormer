package mazestormer.report;

import lejos.util.Delay;
import mazestormer.remote.MessageSender;
import mazestormer.remote.NXTCommunicator;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.RobotUpdate;

public class UpdateReporter extends MessageSender<Report<?>> implements
		Runnable {

	private final ControllableRobot robot;

	private Thread thread;
	private boolean isRunning = false;

	public UpdateReporter(NXTCommunicator communicator, ControllableRobot robot) {
		super(communicator);
		this.robot = robot;
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
			report();
			Delay.msDelay(ControllableRobot.updateReportDelay);
		}
	}

	public void report() {
		send(new UpdateReport(ReportType.UPDATE, RobotUpdate.create(robot)));
	}

}
