package mazestormer.controller;

import java.util.logging.Logger;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import mazestormer.connect.ConnectEvent;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.RangeScannerListener;
import mazestormer.robot.RobotUpdate;
import mazestormer.robot.RobotUpdateListener;

import com.google.common.eventbus.Subscribe;

public class StateController extends SubController implements IStateController {

	private final MovePublisher movePublisher = new MovePublisher();
	private final UpdatePublisher updatePublisher = new UpdatePublisher();
	private final RangeReadingPublisher rangeReadingPublisher = new RangeReadingPublisher();

	public StateController(MainController mainController) {
		super(mainController);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	public Logger getLogger() {
		return getMainController().getPlayer().getLogger();
	}

	@Subscribe
	public void registerRobotListeners(ConnectEvent e) {
		if (e.isConnected()) {
			getRobot().getPilot().addMoveListener(movePublisher);
			getRobot().addUpdateListener(updatePublisher);
			getRobot().getRangeScanner().addListener(rangeReadingPublisher);
		}
	}

	public float getXPosition() {
		return getMainController().getPose().getX();
	}

	public float getYPosition() {
		return getMainController().getPose().getY();
	}

	public float getHeading() {
		return getMainController().getPose().getHeading();
	}

	@Subscribe
	protected void logMove(MoveEvent event) {
		if (event.getEventType() == MoveEvent.EventType.STARTED) {
			getLogger().fine("Move started: " + event.getMove().toString());
		} else {
			getLogger().fine("Move stopped: " + event.getMove().toString());
		}
	}

	private class MovePublisher implements MoveListener {

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			postEvent(new MoveEvent(MoveEvent.EventType.STARTED, event));
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
			postEvent(new MoveEvent(MoveEvent.EventType.STOPPED, event));
		}

	}

	private class UpdatePublisher implements RobotUpdateListener {

		@Override
		public void updateReceived(RobotUpdate update) {
			postEvent(update);
		}

	}

	private class RangeReadingPublisher implements RangeScannerListener {

		@Override
		public void readingReceived(RangeReading reading) {
			postEvent(reading);
		}

	}

}
