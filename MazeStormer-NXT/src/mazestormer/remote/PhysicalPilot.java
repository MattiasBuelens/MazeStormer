package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import mazestormer.command.Command;
import mazestormer.command.PilotParameterCommand;
import mazestormer.command.RotateCommand;
import mazestormer.command.StopCommand;
import mazestormer.command.TravelCommand;
import mazestormer.report.MoveReporter;
import mazestormer.report.MovementReporter;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class PhysicalPilot extends DifferentialPilot implements Pilot {

	private final NXTCommunicator communicator;
	// private List<MessageListener<Command>> messageListeners = new
	// ArrayList<MessageListener<Command>>();

	private MovementReporter movementReporter;

	public PhysicalPilot(NXTCommunicator communicator) {
		super(Robot.leftWheelDiameter, Robot.rightWheelDiameter,
				Robot.trackWidth, Motor.A, Motor.B, false);

		this.communicator = communicator;
		setup();
	}

	private void setup() {
		// Command listeners
		addMessageListener(new TravelCommandListener());
		addMessageListener(new RotateCommandListener());
		addMessageListener(new StopCommandListener());
		addMessageListener(new ParameterCommandListener());

		// Move listener
		addMoveListener(new MoveReporter(communicator));

		// Start reporting movements
		movementReporter = new MovementReporter(communicator, this);
		movementReporter.start();
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		// messageListeners.add(listener);
		communicator.addListener(listener);
	}

	@Override
	public void terminate() {
		// Stop the pilot
		stop();

		// Stop reporting movements
		movementReporter.stop();

		// Remove registered message listeners
		// for (MessageListener<Command> listener : messageListeners) {
		// communicator.removeListener(listener);
		// }
	}

	private class TravelCommandListener implements MessageListener<Command> {
		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof TravelCommand))
				return;

			double distance = ((TravelCommand) command).getDistance();
			travel(distance, true);
		}
	}

	private class RotateCommandListener implements MessageListener<Command> {
		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof RotateCommand))
				return;

			double angle = ((RotateCommand) command).getAngle();

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
	}

	private class StopCommandListener implements MessageListener<Command> {
		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof StopCommand))
				return;

			stop();
		}
	}

	private class ParameterCommandListener implements MessageListener<Command> {
		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof PilotParameterCommand))
				return;

			double value = ((PilotParameterCommand) command).getValue();

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
	}

}
