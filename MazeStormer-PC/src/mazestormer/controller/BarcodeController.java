package mazestormer.controller;

import mazestormer.barcode.ActionType;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.BarcodeRunner.BarcodeState;
import mazestormer.barcode.BarcodeSpeed;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.Threshold;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Runner;
import mazestormer.state.StateListener;

public class BarcodeController extends SubController implements
		IBarcodeController {

	private ActionRunner actionRunner;
	private BarcodeRunner barcodeRunner;

	private double scanTravelSpeed = 2; // [cm/sec]

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	@Override
	public void startAction(ActionType actionType) {
		this.actionRunner = new ActionRunner(getAction(actionType));
		this.actionRunner.start();
	}

	@Override
	public void stopAction() {
		if (this.actionRunner != null) {
			this.actionRunner.shutdown();
			this.actionRunner = null;
		}
	}

	private static IAction getAction(ActionType actionType) {
		return (actionType != null) ? actionType.build() : new NoAction();
	}

	@Override
	public double getScanSpeed() {
		return this.scanTravelSpeed;
	}

	@Override
	public void setScanSpeed(double speed) {
		this.scanTravelSpeed = speed;
	}

	@Override
	public int getWBThreshold() {
		return Threshold.WHITE_BLACK.getThresholdValue();
	}

	@Override
	public void setWBThreshold(int threshold) {
		Threshold.WHITE_BLACK.setThresholdValue(threshold);
	}

	@Override
	public int getBWThreshold() {
		return Threshold.BLACK_WHITE.getThresholdValue();
	}

	@Override
	public void setBWThreshold(int threshold) {
		Threshold.BLACK_WHITE.setThresholdValue(threshold);
	}

	@Override
	public void startScan() {
		// Prepare
		barcodeRunner = new BarcodeRunner(getRobot(), getMaze()) {
			@Override
			protected void log(String message) {
				BarcodeController.this.log(message);
			}
		};
		barcodeRunner.addStateListener(new BarcodeListener());
		barcodeRunner.setPerformAction(false);
		barcodeRunner.setScanSpeed(getScanSpeed());

		// Start
		getRobot().getPilot().forward();
		barcodeRunner.start();
	}

	@Override
	public void stopScan() {
		if (barcodeRunner != null) {
			barcodeRunner.stop();
			barcodeRunner = null;
		}
	}

	private void onScanStarted() {
		// Post state
		postState(BarcodeScanEvent.EventType.STARTED);
	}

	private void onScanStopped() {
		// Stop pilot
		getRobot().getPilot().stop();
		// Post state
		postState(BarcodeScanEvent.EventType.STOPPED);
	}

	private void postState(BarcodeScanEvent.EventType eventType) {
		postEvent(new BarcodeScanEvent(eventType));
	}

	@Override
	public double getLowSpeed() {
		return BarcodeSpeed.LOW.getBarcodeSpeedValue();
	}

	@Override
	public void setLowSpeed(double speed) {
		BarcodeSpeed.LOW.setBarcodeSpeedValue(speed);
	}

	@Override
	public double getHighSpeed() {
		return BarcodeSpeed.HIGH.getBarcodeSpeedValue();
	}

	@Override
	public void setHighSpeed(double speed) {
		BarcodeSpeed.HIGH.setBarcodeSpeedValue(speed);
	}

	@Override
	public double getLowerSpeedBound() {
		return BarcodeSpeed.LOWERBOUND.getBarcodeSpeedValue();
	}

	@Override
	public double getUpperSpeedBound() {
		return BarcodeSpeed.UPPERBOUND.getBarcodeSpeedValue();
	}

	private class ActionRunner extends Runner {

		private final ControllableRobot robot;
		private IAction action;

		public ActionRunner(IAction action) {
			super(getRobot().getPilot());
			this.robot = getRobot();
			this.action = action;
		}

		@Override
		public void onStarted() {
			super.onStarted();
			// Post state
			postState(BarcodeActionEvent.EventType.STARTED);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			// Post state
			postState(BarcodeActionEvent.EventType.STOPPED);
		}

		private void postState(BarcodeActionEvent.EventType eventType) {
			postEvent(new BarcodeActionEvent(eventType));
		}

		@Override
		public void run() {
			// Perform action
			action.performAction(robot, getMaze());
			// Done
			cancel();
		}

	}

	private class BarcodeListener implements StateListener<BarcodeState> {

		@Override
		public void stateStarted() {
			onScanStarted();
		}

		@Override
		public void stateStopped() {
			onScanStopped();
		}

		@Override
		public void stateFinished() {
			stateStopped();
		}

		@Override
		public void statePaused(BarcodeState currentState, boolean onTransition) {
		}

		@Override
		public void stateResumed(BarcodeState currentState) {
		}

		@Override
		public void stateTransitioned(BarcodeState nextState) {
		}

	}

}
