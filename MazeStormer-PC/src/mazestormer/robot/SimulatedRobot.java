package mazestormer.robot;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;

public class SimulatedRobot implements Robot {

	private final double leftWheelDiameter;
	private final float leftTurnRatio;
	private final float leftDegPerDistance;

	private final double rightWheelDiameter;
	private final float rightTurnRatio;
	private final float rightDegPerDistance;

	private final float trackWidth;

	private double travelSpeed;
	private double rotateSpeed;
	private final double maxTravelSpeed;
	private final double maxRotateSpeed;
	private double minRadius;

	private Move.MoveType moveType;
	private double moveAngle;
	private double moveDistance;
	private List<MoveListener> moveListeners = new ArrayList<MoveListener>();

	private Simulator simulator;

	public SimulatedRobot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth, double maxTravelSpeed, double maxRotateSpeed) {
		this.maxTravelSpeed = maxTravelSpeed;
		this.maxRotateSpeed = maxRotateSpeed;

		this.trackWidth = (float) trackWidth;

		// Left wheel
		this.leftWheelDiameter = (float) leftWheelDiameter;
		this.leftTurnRatio = (float) (trackWidth / leftWheelDiameter);
		this.leftDegPerDistance = (float) (360 / (Math.PI * leftWheelDiameter));

		// Right wheel
		this.rightWheelDiameter = (float) rightWheelDiameter;
		this.rightTurnRatio = (float) (trackWidth / rightWheelDiameter);
		this.rightDegPerDistance = (float) (360 / (Math.PI * rightWheelDiameter));

		setTravelSpeed(.8f * getMaxTravelSpeed());
		setRotateSpeed(.8f * getRotateMaxSpeed());
	}

	public SimulatedRobot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth) {
		this(leftWheelDiameter, rightWheelDiameter, trackWidth,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	@Override
	public void connect() {
		startSimulation();
	}

	@Override
	public void disconnect() {
		stopSimulation();
	}

	private void startSimulation() {
		simulator = new Simulator();
		simulator.start();
	}

	private void stopSimulation() {
		if (simulator != null) {
			simulator.stop();
		}
	}

	@Override
	public void setTravelSpeed(double speed) {
		this.travelSpeed = speed;
	}

	@Override
	public double getTravelSpeed() {
		return travelSpeed;
	}

	@Override
	public double getMaxTravelSpeed() {
		return maxTravelSpeed;
	}

	@Override
	public void setRotateSpeed(double speed) {
		this.rotateSpeed = speed;
	}

	@Override
	public double getRotateSpeed() {
		return rotateSpeed;
	}

	@Override
	public double getRotateMaxSpeed() {
		return maxRotateSpeed;
	}

	@Override
	public void forward() {
		moveType = Move.MoveType.TRAVEL;
		moveAngle = 0;
		moveDistance = Double.POSITIVE_INFINITY;
		movementStart();
		// TODO Auto-generated method stub

	}

	@Override
	public void backward() {
		moveType = Move.MoveType.TRAVEL;
		moveAngle = 0;
		moveDistance = Double.NEGATIVE_INFINITY;
		movementStart();
		// TODO Auto-generated method stub

	}

	@Override
	public void rotateLeft() {
		moveType = Move.MoveType.TRAVEL;
		moveAngle = Double.POSITIVE_INFINITY;
		moveDistance = 0;
		movementStart();
		// TODO Auto-generated method stub

	}

	@Override
	public void rotateRight() {
		moveType = Move.MoveType.TRAVEL;
		moveAngle = Double.NEGATIVE_INFINITY;
		moveDistance = 0;
		movementStart();
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(double angle) {
		rotate(angle, false);
	}

	@Override
	public void rotate(double angle, boolean immediateReturn) {
		moveType = Move.MoveType.ROTATE;
		moveDistance = 0;
		moveAngle = angle;
		movementStart();

		// TODO Auto-generated method stub

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void travel(double distance) {
		travel(distance, false);
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		moveType = Move.MoveType.TRAVEL;
		moveDistance = distance;
		moveAngle = 0;
		if (distance == Double.POSITIVE_INFINITY) {
			forward();
			return;
		}
		if (distance == Double.NEGATIVE_INFINITY) {
			backward();
			return;
		}
		movementStart();

		// TODO Auto-generated method stub

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public void arcForward(double radius) {
		moveType = Move.MoveType.ARC;
		if (radius > 0) {
			moveAngle = Double.POSITIVE_INFINITY;
			moveDistance = Double.POSITIVE_INFINITY;
		} else {
			moveAngle = Double.NEGATIVE_INFINITY;
			moveDistance = Double.NEGATIVE_INFINITY;
		}
		movementStart();

		// TODO Auto-generated method stub

	}

	@Override
	public void arcBackward(double radius) {
		moveType = Move.MoveType.ARC;
		if (radius < 0) {
			moveAngle = Double.POSITIVE_INFINITY;
			moveDistance = Double.NEGATIVE_INFINITY;
		} else {
			moveAngle = Double.NEGATIVE_INFINITY;
			moveDistance = Double.POSITIVE_INFINITY;
		}
		movementStart();

		// TODO Auto-generated method stub

	}

	@Override
	public void arc(double radius, double angle) {
		arc(radius, angle, false);
	}

	@Override
	public void arc(double radius, double angle, boolean immediateReturn) {
		if (radius == Double.POSITIVE_INFINITY
				|| radius == Double.NEGATIVE_INFINITY) {
			forward();
			return;
		}
		steer(turnRate(radius), angle, immediateReturn);
	}

	@Override
	public void travelArc(double radius, double distance) {
		travelArc(radius, distance, false);
	}

	@Override
	public void travelArc(double radius, double distance,
			boolean immediateReturn) {
		if (radius == Double.POSITIVE_INFINITY
				|| radius == Double.NEGATIVE_INFINITY) {
			travel(distance, immediateReturn);
			return;
		}
		if (radius == 0) {
			throw new IllegalArgumentException("Zero arc radius");
		}
		double angle = (distance * 180) / ((float) Math.PI * radius);
		arc(radius, angle, immediateReturn);
	}

	private void reset() {
		// TODO Reset tacho counts
	}

	/**
	 * Calculates the turn rate corresponding to the turn radius; use as the
	 * parameter for steer().
	 * 
	 * Negative argument means center of turn is on right, so angle of turn is
	 * negative.
	 * 
	 * @param radius
	 * @return turnRate to be used in steer()
	 */
	private double turnRate(final double radius) {
		int direction = (radius < 0) ? -1 : 1;
		double radiusToUse = direction * radius;
		double ratio = (2 * radiusToUse - trackWidth)
				/ (2 * radiusToUse + trackWidth);
		return (direction * 100 * (1 - ratio));
	}

	/**
	 * Returns the radius of the turn made by steer(turnRate).
	 * 
	 * Used for planned distance at start of arc and steer moves.
	 * 
	 * @param turnRate
	 * @return radius of the turn.
	 */
	private double radius(double turnRate) {
		double radius = 100 * trackWidth / turnRate;
		if (turnRate > 0)
			radius -= trackWidth / 2;
		else
			radius += trackWidth / 2;
		return radius;
	}

	public void steer(final double turnRate, final double angle,
			final boolean immediateReturn) {
		if (angle == 0)
			return;
		if (turnRate == 0) {
			forward();
			return;
		}
		moveType = Move.MoveType.ARC;
		moveAngle = angle;
		moveDistance = 2 * Math.toRadians(angle) * radius(turnRate);
		movementStart();

		// TODO Auto-generated method stub

		waitComplete();
	}

	protected void movementStart() {
		if (isMoving())
			movementStop();
		reset();

		for (MoveListener ml : moveListeners) {
			ml.moveStarted(new Move(moveType, (float) moveDistance,
					(float) moveAngle, (float) getTravelSpeed(),
					(float) getRotateSpeed(), isMoving()), this);
		}
	}

	private synchronized void movementStop() {
		for (MoveListener ml : moveListeners) {
			ml.moveStopped(new Move(moveType, (float) moveDistance,
					(float) moveAngle, (float) getTravelSpeed(),
					(float) getRotateSpeed(), isMoving()), this);
		}
	}

	@Override
	public boolean isMoving() {
		return (simulator == null) ? false : simulator.isMoving();
	}

	/**
	 * Waits for the current move to complete.
	 */
	private void waitComplete() {
		while (isMoving()) {
			Thread.yield();
		}
	}

	/*
	 * Implementation note: the minimum turning circle radius is not actually
	 * used in the PC version of DifferentialPilot.
	 */
	@Override
	public void setMinRadius(double radius) {
		minRadius = radius;
	}

	@Override
	public double getMinRadius() {
		return minRadius;
	}

	/**
	 * @return The move distance since it last started moving
	 */
	public float getMovementIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return The angle rotated since rotation began.
	 */
	public float getAngleIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	@Override
	public Move getMovement() {
		return new Move(moveType, getMovementIncrement(), getAngleIncrement(),
				isMoving());
	}

	private class Simulator implements Runnable {

		private Thread thread;
		private boolean isRunning = true;

		private boolean isMoving;

		public void start() {
			if (thread != null) {
				waitForStop();
			}
			isRunning = true;
			thread = new Thread(this);
			thread.start();
		}

		public void stop() {
			isRunning = false;
			thread = null;
		}

		public void waitForStop() {
			isRunning = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread = null;
		}

		public synchronized boolean isMoving() {
			return isMoving;
		}

		@Override
		public void run() {
			while (isRunning) {
				// TODO Run simulation
			}
		}
	}

}
