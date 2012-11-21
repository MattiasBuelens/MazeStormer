package mazestormer.controller;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.IAction;
import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class BarcodeController extends SubController implements
		IBarcodeController {
	private static final double TRAVEL_SPEED = 10; 		// [cm/sec]
	private static final double SLOW_TRAVEL_SPEED = 2; 	// [cm/sec]

	private static final double START_BAR_LENGTH = 1.8; // [cm]
	private static final double BAR_LENGTH = 1.85; 		// [cm]
	private static final int NUMBER_OF_BARS = 6; 		// without black start bars

	private static final int BLACK_THRESHOLD = 50;

	public BarcodeController(MainController mainController) {
		super(mainController);
	}

	private ActionRunner runner;

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	@Override
	public void startAction(String action) {
		this.runner = new ActionRunner(getAction(action));
		this.runner.start();
	}

	@Override
	public void stopAction() {
		if (this.runner != null) {
			this.runner.stop();
			this.runner = null;
		}
	}

	private static IAction getAction(String action) {
		if (ACTIONS[0].equals(action))
			return new mazestormer.barcode.SoundAction();
		if (ACTIONS[1].equals(action))
			return new mazestormer.barcode.RotateClockwiseAction();
		if (ACTIONS[2].equals(action))
			return new mazestormer.barcode.RotateCounterClockwiseAction();
		if (ACTIONS[3].equals(action))
			return new mazestormer.barcode.HighSpeedAction();
		if (ACTIONS[4].equals(action))
			return new mazestormer.barcode.LowSpeedAction();
		if (ACTIONS[5].equals(action))
			return new mazestormer.barcode.WaitAction();
		return mazestormer.barcode.NoAction.getInstance();
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	private void postState(EventType eventType) {
		postEvent(new ActionEvent(eventType));
	}

	private class ActionRunner implements Runnable {

		private final Robot robot;
		private boolean isRunning = false;
		private IAction action;

		public ActionRunner(IAction action) {
			this.robot = getRobot();
			this.action = action;
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				this.isRunning = false;
				this.robot.getPilot().stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		@Override
		public void run() {
			this.action.performAction(this.robot);
			stop();
		}

	}

	@Override
	public void scanAction() {
		new BarcodeRunner().run();
	}

	private class BarcodeRunner implements Runnable {

		private final Pilot pilot;
		private final CalibratedLightSensor light;
		private boolean isRunning = false;
		private CommandHandle handle;

		private double originalTravelSpeed;

		public BarcodeRunner() {
			this.pilot = getRobot().getPilot();
			this.light = getRobot().getLightSensor();
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				this.isRunning = false;
				this.pilot.stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		private void onBlack(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN, BLACK_THRESHOLD);
			this.handle = getRobot().when(condition).stop().run(action).build();
		}

		@Override
		public void run() {
			this.originalTravelSpeed = this.pilot.getTravelSpeed();
			this.pilot.setTravelSpeed(TRAVEL_SPEED);
			this.light.setFloodlight(true);
			this.pilot.forward();
			log("Start looking for black line.");
			onBlack(new Runnable() {
				@Override
				public void run() {
					onBlackBackward();
				}
			});
		}

		private int oldValue;
		private Pose oldPose;
		private int newValue;
		private Pose newPose;
		private boolean blackToWhite;
		private List<Float> distances = new ArrayList<Float>();
		private byte barcode;

		private void onBlackBackward() {
			log("Go to the begin of the barcode zone.");
			this.pilot.setTravelSpeed(SLOW_TRAVEL_SPEED);
			this.pilot.travel(-START_BAR_LENGTH / 2, false);
			this.oldValue = this.light.getLightValue();
			this.oldPose = getRobot().getPoseProvider().getPose();
			this.blackToWhite = true;

			this.pilot.forward();
			loop();
		}

		private void loop() {
			this.newValue = this.light.getLightValue();
			this.newPose = getRobot().getPoseProvider().getPose();

			if (this.blackToWhite) {
				onTrespassOldBlack(new Runnable() {
					@Override
					public void run() {
						onTrespassBW();
					}
				});
			} else {
				onTrespassOldWhite(new Runnable() {
					@Override
					public void run() {
						onTrespassWB();
					}
				});
			}
		}

		private void onTrespassBW() {
			onTrespassNewWhite(new Runnable() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassWB() {
			onTrespassNewBlack(new Runnable() {
				@Override
				public void run() {
					onChange();
				}
			});
		}

		private void onTrespassOldBlack(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN,
					Threshold.BLACK_WHITE.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onTrespassOldWhite(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_GREATER_THAN,
					Threshold.WHITE_BLACK.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onTrespassNewBlack(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_SMALLER_THAN,
					Threshold.WHITE_BLACK.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onTrespassNewWhite(final Runnable action) {
			Condition condition = new LightCompareCondition(
					ConditionType.LIGHT_GREATER_THAN,
					Threshold.BLACK_WHITE.getThresholdValue());
			this.handle = getRobot().when(condition).run(action).build();
		}

		private void onChange() {
			this.distances.add(getPoseDiff(oldPose, newPose));
			this.oldValue = newValue;
			this.oldPose = newPose;
			this.blackToWhite = (blackToWhite == true) ? false : true;

			if (getTotalSum(this.distances) <= (NUMBER_OF_BARS + 1)
					* BAR_LENGTH) {
				loop();
			} else {
				encodeBarcode();
				decodeBarcode();
			}
		}

		private void encodeBarcode() {
			this.barcode = convertToByte(convertToBitArray(distances));
			System.out.println(Integer.toBinaryString((int) this.barcode));
		}

		private void decodeBarcode() {
			// BarcodeDecoder.getAction(this.barcode).performAction(getRobot());
		}
	}

	private static byte convertToByte(int[] request) {
		int temp = 0;
		for (int i = request.length - 1; i > 0; i--)
			temp = (temp + request[i]) * 2;
		temp = temp + request[0];
		return ((Integer) temp).byteValue();
	}

	private static float getPoseDiff(Pose one, Pose two) {
		float diffX = Math.abs(one.getX() - two.getX());
		float diffY = Math.abs(one.getY() - two.getY());
		if (diffX > diffY)
			return diffX;
		return diffY;
	}

	private static float getTotalSum(List<Float> request) {
		float temp = 0;
		for (int i = 0; i < request.size(); i++)
			temp = temp + request.get(i);
		return temp;
	}

	private static int[] convertToBitArray(List<Float> request) {
		int[] values = new int[NUMBER_OF_BARS];
		int index = NUMBER_OF_BARS - 1;
		for (int i = 0; index >= 0 && i < request.size(); i++) {
			float d = request.get(i);
			int x = (i == 0) ? 1 : 0;
			int a = ((Double) (Math.max(
					((d - START_BAR_LENGTH * x) / BAR_LENGTH), 1 - x)))
					.intValue();
			for (int j = 0; j < a; j++) {
				if (index >= 0) {
					values[index] = Math.abs(i % 2);
					index--;
				}
			}
		}
		return values;
	}
}
