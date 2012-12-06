package mazestormer.controller;

import mazestormer.barcode.ActionType;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.Threshold;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerListener;

public class BarcodeController extends SubController implements
		IBarcodeController {

	private ActionRunner actionRunner;
	private BarcodeRunner barcodeRunner;

	private double scanTravelSpeed = 2; // [cm/sec]

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
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
		barcodeRunner.addListener(new BarcodeListener());
		barcodeRunner.setPerformAction(false);
		barcodeRunner.setScanSpeed(getScanSpeed());

		// Start
		getRobot().getPilot().forward();
		barcodeRunner.start();
	}

	@Override
	public void stopScan() {
		if (barcodeRunner != null) {
			barcodeRunner.cancel();
			barcodeRunner = null;
		}
	}

	private void onScanStarted() {
		// Post state
		postState(BarcodeScanEvent.EventType.STARTED);
	}

	private void onScanStopped() {
		getRobot().getPilot().stop();
		// Post state
		postState(BarcodeScanEvent.EventType.STOPPED);
	}

	private void postState(BarcodeScanEvent.EventType eventType) {
		postEvent(new BarcodeScanEvent(eventType));
	}

	private class ActionRunner extends Runner {

		private final Robot robot;
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

	private class BarcodeListener implements RunnerListener {

		@Override
		public void onStarted() {
			onScanStarted();
		}

		@Override
		public void onCompleted() {
			onScanStopped();
		}

		@Override
		public void onCancelled() {
			onScanStopped();
		}

	}

}
