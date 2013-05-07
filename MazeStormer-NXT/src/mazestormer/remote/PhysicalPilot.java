package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import mazestormer.command.Command;
import mazestormer.command.PilotParameterCommand;
import mazestormer.command.RotateCommand;
import mazestormer.command.StopCommand;
import mazestormer.command.TravelCommand;
import mazestormer.report.MoveReport;
import mazestormer.report.ReportType;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Pilot;
import mazestormer.util.Future;

public class PhysicalPilot extends DifferentialPilot implements Pilot, MessageListener<Command>, MoveListener {

	private final NXTCommunicator communicator;

	public PhysicalPilot(NXTCommunicator communicator) {
		super(ControllableRobot.leftWheelDiameter, ControllableRobot.rightWheelDiameter, ControllableRobot.trackWidth,
				Motor.B, Motor.A, false);

		this.communicator = communicator;
		setup();
	}

	private void setup() {
		// Command listeners
		addMessageListener(this);

		// Move listener
		addMoveListener(this);
	}

	@Override
	public Future<Boolean> travelComplete(double distance) {
		return null;
	}

	@Override
	public Future<Boolean> rotateComplete(double angle) {
		return null;
	}

	private void addMessageListener(MessageListener<Command> listener) {
		communicator.addListener(listener);
	}

	@Override
	public void removeMoveListener(MoveListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminate() {
		// Stop the pilot
		stop();
	}

	@Override
	public void messageReceived(Command command) {
		if (command instanceof TravelCommand) {
			onTravelCommand((TravelCommand) command);
		}
		if (command instanceof RotateCommand) {
			onRotateCommand((RotateCommand) command);
		}
		if (command instanceof StopCommand) {
			onStopCommand((StopCommand) command);
		}
		if (command instanceof PilotParameterCommand) {
			onParameterCommand((PilotParameterCommand) command);
		}
	}

	private void onTravelCommand(TravelCommand command) {
		travel(command.getDistance(), true);
	}

	private void onRotateCommand(RotateCommand command) {
		double angle = command.getAngle();
		if (Double.isInfinite(angle)) {
			if (angle > 0) {
				rotateLeft();
			} else {
				rotateRight();
			}
		} else {
			rotate(angle, true);
		}
	}

	private void onStopCommand(StopCommand command) {
		stop();
	}

	private void onParameterCommand(PilotParameterCommand command) {
		double value = command.getValue();
		switch (command.getType()) {
		case SET_TRAVEL_SPEED:
			setTravelSpeed(value);
			break;
		case SET_ROTATE_SPEED:
			setRotateSpeed(value);
			break;
		case SET_ACCELERATION:
			setAcceleration((int) value);
			break;
		default:
			break;
		}
	}

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		try {
			communicator.send(new MoveReport(ReportType.MOVE_STARTED, event));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		try {
			communicator.send(new MoveReport(ReportType.MOVE_STOPPED, event));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

}
