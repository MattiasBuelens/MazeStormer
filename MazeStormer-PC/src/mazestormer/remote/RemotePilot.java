package mazestormer.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.command.RotateCommand;
import mazestormer.command.StopCommand;
import mazestormer.command.TravelCommand;
import mazestormer.report.Report;
import mazestormer.robot.Pilot;

public class RemotePilot implements Pilot {

	private double travelSpeed;
	private double rotateSpeed;

	private final Communicator<Command, ? super Report> communicator;

	private List<MoveListener> moveListeners = new ArrayList<MoveListener>();

	public RemotePilot(Communicator<Command, ? super Report> communicator) {
		this.communicator = communicator;
	}

	private void send(Command command) {
		try {
			communicator.send(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setTravelSpeed(double speed) {
		travelSpeed = speed;
		// TODO Auto-generated method stub
	}

	@Override
	public double getTravelSpeed() {
		return travelSpeed;
	}

	@Override
	public double getMaxTravelSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRotateSpeed(double speed) {
		rotateSpeed = speed;
		// TODO Auto-generated method stub
	}

	@Override
	public double getRotateSpeed() {
		return rotateSpeed;
	}

	@Override
	public double getRotateMaxSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void forward() {
		send(new TravelCommand(CommandType.TRAVEL, Double.POSITIVE_INFINITY));
	}

	@Override
	public void backward() {
		send(new TravelCommand(CommandType.TRAVEL, Double.NEGATIVE_INFINITY));
	}

	@Override
	public void stop() {
		send(new StopCommand(CommandType.STOP));
	}

	@Override
	public void rotateLeft() {
		send(new RotateCommand(CommandType.ROTATE, Double.POSITIVE_INFINITY));
	}

	@Override
	public void rotateRight() {
		send(new RotateCommand(CommandType.ROTATE, Double.NEGATIVE_INFINITY));
	}

	@Override
	public void rotate(double angle) {
		rotate(angle, false);
	}

	@Override
	public void rotate(double angle, boolean immediateReturn) {
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
		send(new TravelCommand(CommandType.TRAVEL, distance));

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}

	private void waitComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	@Override
	public Move getMovement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void terminate() {
	}

}
