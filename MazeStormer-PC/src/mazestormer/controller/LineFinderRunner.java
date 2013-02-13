package mazestormer.controller;

import java.util.concurrent.CancellationException;

import mazestormer.command.ConditionalCommandBuilder.CommandHandle;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.condition.LightCompareCondition;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Robot;
import mazestormer.robot.Runner;
import mazestormer.robot.RunnerTask;

class LineFinderRunner extends Runner {

	private final static double slowRotateSpeed = 30;
	private final static double fastRotateSpeed = 50;
	private final static double slowTravelSpeed = 1.0;
	private final static double fastTravelSpeed = 5.0;

	private final static double maxAttackAngle = 20.0;
	private final static double safetyAngle = 10.0;
	private final static double fastRotateAngle = -(90 - maxAttackAngle - safetyAngle);

	private final static int threshold = 85;

	private final Robot robot;
	private double lineWidth;

	private CommandHandle handle;
	private double originalTravelSpeed;
	private double originalRotateSpeed;

	public LineFinderRunner(Robot robot) {
		super(robot.getPilot());
		this.robot = robot;
	}

	protected Robot getRobot() {
		return robot;
	}

	protected CalibratedLightSensor getLightSensor() {
		return getRobot().getLightSensor();
	}

	protected void log(String message) {
		System.out.println(message);
	}

	private void reset() {
		// Cancel condition handle
		if (handle != null)
			handle.cancel();

		// Restore original speeds
		getPilot().setTravelSpeed(originalTravelSpeed);
		getPilot().setRotateSpeed(originalRotateSpeed);
	}

	@Override
	public void onCompleted() {
		super.onCompleted();
		reset();
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		reset();
	}

	private void onLine(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_GREATER_THAN, threshold);
		handle = getRobot().when(condition).stop().run(prepare(task)).build();
	}

	private void offLine(final RunnerTask task) {
		Condition condition = new LightCompareCondition(
				ConditionType.LIGHT_SMALLER_THAN, threshold);
		handle = getRobot().when(condition).stop().run(prepare(task)).build();
	}

	@Override
	public void run() throws CancellationException {
		// Save original speeds
		originalTravelSpeed = getTravelSpeed();
		originalRotateSpeed = getRotateSpeed();

		// Travel forward until on line
		log("Start looking for line.");
		setTravelSpeed(fastTravelSpeed);
		forward();
		onLine(new RunnerTask() {
			@Override
			public void run() {
				onFirstLine();
			}
		});
	}

	private void onFirstLine() throws CancellationException {
		log("On line, start looking for end of line.");

		// Travel forward until off line
		setTravelSpeed(slowTravelSpeed);
		forward();
		offLine(new RunnerTask() {
			@Override
			public void run() {
				offFirstLine();
			}
		});
	}

	private void offFirstLine() throws CancellationException {
		log("Off line, positioning robot on line edge.");

		lineWidth = getPilot().getMovement().getDistanceTraveled();
		double centerOffset = Robot.sensorOffset
				- getLightSensor().getSensorRadius();
		log("Line width: " + lineWidth);
		log("Offset from center: " + centerOffset);

		// Travel forward to center robot on end of line
		setTravelSpeed(fastTravelSpeed);
		travel(centerOffset);

		// Rotate fixed angle
		setRotateSpeed(fastRotateSpeed);
		rotate(fastRotateAngle);

		// Rotate until on line
		log("Start looking for line again.");
		setRotateSpeed(slowRotateSpeed);
		rotateRight();
		onLine(new RunnerTask() {
			@Override
			public void run() {
				onSecondLine();
			}
		});
	}

	private void onSecondLine() throws CancellationException {
		log("On line, rotating robot perpendicular to line.");

		/*
		 * The sensor radius causes the robot to rotate further than the actual
		 * edge of the line. We can adjust for this by rotating by an extra
		 * angle.
		 * 
		 * This angle corresponds to the central angle (alpha) of a secant line
		 * in a circle. The circle has a radius of Robot.sensorOffset (so) and
		 * the secant line has a length of light.getSensorRadius() (sr).
		 * 
		 * The length of a secant line in a circle given its central angle is:
		 * sr = 2*so*sin(alpha/2)
		 * 
		 * Therefore: alpha = 2*asin(sr/(2*so))
		 */
		double sensorAngle = 2 * Math.asin(getLightSensor().getSensorRadius()
				/ (2 * Robot.sensorOffset));
		log("Angle adjusting for sensor radius: " + sensorAngle);

		// Position perpendicular to line
		setRotateSpeed(fastRotateSpeed);
		rotate(90.0 + sensorAngle);

		// Position robot center on center of line
		log("Positioning on center of line.");
		travel(-lineWidth / 2);

		// Done
		resolve();
	}

}