package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.MoveListener;
import mazestormer.command.CommandType;
import mazestormer.command.PilotParameterCommand;
import mazestormer.command.RotateCommand;
import mazestormer.command.StopCommand;
import mazestormer.command.TravelCommand;
import mazestormer.report.MoveReport;
import mazestormer.report.Report;
import mazestormer.robot.Pilot;

public class RemotePilot extends RemoteComponent implements Pilot {

	private boolean isMoving = false;
	private double travelSpeed;
	private double rotateSpeed;
	private Move movement;

	private List<MoveListener> moveListeners = new ArrayList<MoveListener>();

	public RemotePilot(RemoteCommunicator communicator) {
		super(communicator);
		resetMovement();
		setup();
	}

	private void setup() {
		addMessageListener(new MoveReportListener());
	}

	@Override
	public void setTravelSpeed(double speed) {
		travelSpeed = speed;
		send(new PilotParameterCommand(CommandType.SET_TRAVEL_SPEED, speed));
	}

	@Override
	public double getTravelSpeed() {
		return travelSpeed;
	}

	@Override
	public void setAcceleration(int acceleration) {
		send(new PilotParameterCommand(CommandType.SET_ACCELERATION,
				acceleration));
	}

	@Override
	public double getMaxTravelSpeed() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void setRotateSpeed(double speed) {
		rotateSpeed = speed;
		send(new PilotParameterCommand(CommandType.SET_ROTATE_SPEED, speed));
	}

	@Override
	public double getRotateSpeed() {
		return rotateSpeed;
	}

	@Override
	public double getRotateMaxSpeed() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void forward() {
		travel(Double.POSITIVE_INFINITY, true);
	}

	@Override
	public void backward() {
		travel(Double.NEGATIVE_INFINITY, true);
	}

	@Override
	public void stop() {
		send(new StopCommand(CommandType.STOP));
		waitComplete();
	}

	@Override
	public void rotateLeft() {
		rotate(Double.POSITIVE_INFINITY, true);
	}

	@Override
	public void rotateRight() {
		rotate(Double.NEGATIVE_INFINITY, true);
	}

	@Override
	public void rotate(double angle) {
		rotate(angle, false);
	}

	@Override
	public void rotate(double angle, boolean immediateReturn) {
		isMoving = true;
		send(new RotateCommand(CommandType.ROTATE, angle));

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public void travel(double distance) {
		travel(distance, false);
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		isMoving = true;
		send(new TravelCommand(CommandType.TRAVEL, distance));

		if (!immediateReturn)
			waitComplete();
	}

	protected synchronized void movementStart(Move move) {
		isMoving = true;
		setMovement(new Move(move.getMoveType(), 0, 0, isMoving()));

		// Publish the *targeted* move
		for (MoveListener ml : moveListeners) {
			ml.moveStarted(move, this);
		}
	}

	protected synchronized void movementStop(Move move) {
		isMoving = false;
		setMovement(move);

		// Publish the *travelled* move
		for (MoveListener ml : moveListeners) {
			ml.moveStopped(move, this);
		}
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}

	/**
	 * Waits for the current move to complete.
	 */
	private void waitComplete() {
		while (isMoving()) {
			Thread.yield();
		}
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	@Override
	public Move getMovement() {
		return movement;
	}

	public void setMovement(Move move) {
		movement = new Move(move.getMoveType(), move.getDistanceTraveled(),
				move.getAngleTurned(), (float) getTravelSpeed(),
				(float) getRotateSpeed(), isMoving());
	}

	public void resetMovement() {
		setMovement(new Move(MoveType.STOP, 0, 0, false));
	}

	private class MoveReportListener implements MessageListener<Report<?>> {
		@Override
		public void messageReceived(Report<?> report) {
			if (!(report instanceof MoveReport))
				return;

			MoveReport moveReport = (MoveReport) report;
			switch (moveReport.getType()) {
			case MOVE_STARTED:
				movementStart(moveReport.getMove());
				break;
			case MOVE_STOPPED:
				movementStop(moveReport.getMove());
				break;
			case MOVEMENT:
				setMovement(moveReport.getMove());
				break;
			default:
				break;
			}
		}
	}

}
