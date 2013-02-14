package mazestormer.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.MoveListener;
import mazestormer.robot.MoveFuture;
import mazestormer.robot.Pilot;
import mazestormer.util.Future;

public class VirtualPilot implements Pilot {

	private final float trackWidth;

	private double travelSpeed;
	private double rotateSpeed;
	private final double maxTravelSpeed;
	private final double maxRotateSpeed;
	private double minRadius;

	private Move move;
	private boolean isMoving = false;
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> moveEndHandle;

	private List<MoveListener> moveListeners = new ArrayList<MoveListener>();

	private static final ThreadFactory factory = new ThreadFactoryBuilder()
			.setNameFormat("VirtualPilot-%d").build();

	public VirtualPilot(double trackWidth, double maxTravelSpeed,
			double maxRotateSpeed) {
		this.maxTravelSpeed = maxTravelSpeed;
		this.maxRotateSpeed = maxRotateSpeed;
		this.trackWidth = (float) trackWidth;

		executor = Executors.newSingleThreadScheduledExecutor(factory);

		// Initial speeds
		setTravelSpeed(.8f * getMaxTravelSpeed());
		setRotateSpeed(.8f * getRotateMaxSpeed());
	}

	public VirtualPilot(double trackWidth) {
		this(trackWidth, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
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
	public void setAcceleration(int acceleration) {
		// TODO Implement simulated acceleration?
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
		movementStart(MoveType.TRAVEL, Float.POSITIVE_INFINITY, 0);
	}

	@Override
	public void backward() {
		movementStart(MoveType.TRAVEL, Float.NEGATIVE_INFINITY, 0);
	}

	@Override
	public void rotateLeft() {
		movementStart(MoveType.ROTATE, 0, Float.POSITIVE_INFINITY);
	}

	@Override
	public void rotateRight() {
		movementStart(MoveType.ROTATE, 0, Float.NEGATIVE_INFINITY);
	}

	@Override
	public void rotate(double angle) {
		rotate(angle, false);
	}

	@Override
	public void rotate(double angle, boolean immediateReturn) {
		movementStart(MoveType.ROTATE, 0, (float) angle);

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public void stop() {
		movementStop();
		waitComplete();
	}

	@Override
	public void travel(double distance) {
		travel(distance, false);
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		movementStart(MoveType.TRAVEL, (float) distance, 0);

		if (!immediateReturn)
			waitComplete();
	}

	@Override
	public Future<Boolean> travelComplete(double distance) {
		MoveFuture future = new MoveFuture(this, MoveType.TRAVEL);
		travel(distance, true);
		return future;
	}

	@Override
	public Future<Boolean> rotateComplete(double angle) {
		MoveFuture future = new MoveFuture(this, MoveType.ROTATE);
		rotate(angle, true);
		return future;
	}

	// @Override
	public void arcForward(double radius) {
		float angle, distance;
		if (radius > 0) {
			angle = Float.POSITIVE_INFINITY;
			distance = Float.POSITIVE_INFINITY;
		} else {
			angle = Float.NEGATIVE_INFINITY;
			distance = Float.NEGATIVE_INFINITY;
		}
		movementStart(MoveType.ARC, distance, angle, (float) radius);
	}

	// @Override
	public void arcBackward(double radius) {
		float angle, distance;
		if (radius > 0) {
			angle = Float.POSITIVE_INFINITY;
			distance = Float.NEGATIVE_INFINITY;
		} else {
			angle = Float.NEGATIVE_INFINITY;
			distance = Float.POSITIVE_INFINITY;
		}
		movementStart(MoveType.ARC, distance, angle, (float) radius);
	}

	// @Override
	public void arc(double radius, double angle) {
		arc(radius, angle, false);
	}

	// @Override
	public void arc(double radius, double angle, boolean immediateReturn) {
		if (radius == Double.POSITIVE_INFINITY
				|| radius == Double.NEGATIVE_INFINITY) {
			forward();
			return;
		}
		steer(turnRate(radius), angle, immediateReturn);
	}

	// @Override
	public void travelArc(double radius, double distance) {
		travelArc(radius, distance, false);
	}

	// @Override
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
		float distance = (float) (2 * Math.toRadians(angle) * radius(turnRate));
		movementStart(MoveType.ARC, (float) angle, distance);

		if (!immediateReturn)
			waitComplete();
	}

	protected void movementStart(MoveType moveType, float distance,
			float angle, float radius) {

		boolean wasMoving = isMoving();

		if (wasMoving)
			movementStop();

		// Set current move
		move = new Move(moveType, distance, angle, (float) getTravelSpeed(),
				(float) getRotateSpeed(), wasMoving);
		isMoving = true;

		// Publish the *targeted* move distance and angle
		for (MoveListener ml : moveListeners) {
			ml.moveStarted(move, this);
		}

		// Start timer
		float delay = getMoveDuration(move);
		if (!Float.isInfinite(delay)) {
			moveEndHandle = executor.schedule(new MoveEndRunner(move),
					(long) delay, TimeUnit.MILLISECONDS);
		}
	}

	protected void movementStart(MoveType moveType, float distance, float angle) {
		movementStart(moveType, distance, angle, 0);
	}

	private synchronized void movementStop() {
		if (!isMoving())
			return;

		// Publish the *travelled* move distance and angle
		Move travelledMove = new Move(move.getMoveType(),
				getMovementIncrement(), getAngleIncrement(),
				move.getTravelSpeed(), move.getRotateSpeed(), false);

		for (MoveListener ml : moveListeners) {
			ml.moveStopped(travelledMove, this);
		}

		// Reset current move
		resetMove();
	}

	/**
	 * Reset the current move.
	 * 
	 * Called when the current move is stopped.
	 */
	private void resetMove() {
		if (moveEndHandle != null) {
			moveEndHandle.cancel(false);
		}
		isMoving = false;
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

	/*
	 * Implementation note: the minimum turning circle radius is not actually
	 * used in the PC version of DifferentialPilot.
	 */
	// @Override
	public void setMinRadius(double radius) {
		minRadius = radius;
	}

	// @Override
	public double getMinRadius() {
		return minRadius;
	}

	/**
	 * @return The move distance since it last started moving
	 */
	public float getMovementIncrement() {
		// Time spent travelling so far
		long currentTime = System.currentTimeMillis();
		float duration = (currentTime - move.getTimeStamp()) / 1000f;

		// Currently travelled distance
		float travelled = duration * move.getTravelSpeed();

		// Target distance
		float target = move.getDistanceTraveled();
		if (isInfiniteArc(move)) {
			// Implementation: Use radius instead
			throw new IllegalStateException(
					"Simulator does not fully support arcs.");
		}

		// Compare absolute values, since target may be negative
		if (Math.abs(travelled) < Math.abs(target)) {
			// Make travelled same sign as target
			return Math.copySign(travelled, target);
		} else {
			// Target reached
			return target;
		}
	}

	/**
	 * @return The angle rotated since rotation began.
	 */
	public float getAngleIncrement() {
		// Time spent rotating so far
		long currentTime = System.currentTimeMillis();
		float duration = (currentTime - move.getTimeStamp()) / 1000f;

		// Currently rotated angle
		float rotated = duration * move.getRotateSpeed();

		// Target angle
		float target = move.getAngleTurned();
		if (isInfiniteArc(move)) {
			// Implementation: Use radius instead
			throw new IllegalStateException(
					"Simulator does not fully support arcs.");
		}

		// Compare absolute values, since target may be negative
		if (Math.abs(rotated) < Math.abs(target)) {
			// Make travelled same sign as target
			return Math.copySign(rotated, target);
		} else {
			// Target reached
			return target;
		}
	}

	/**
	 * Get the expected duration of a move, in milliseconds.
	 */
	private static float getMoveDuration(Move move) {
		float duration = 0;
		if (move.getMoveType() == MoveType.TRAVEL) {
			// Translation duration
			duration = move.getDistanceTraveled() / move.getTravelSpeed();
		} else if (move.getMoveType() == MoveType.ROTATE) {
			// Rotation duration
			duration = move.getAngleTurned() / move.getRotateSpeed();
		} else if (move.getMoveType() == MoveType.ARC) {
			// Arc travel duration
			if (isInfiniteArc(move)) {
				return Float.POSITIVE_INFINITY;
			} else {
				throw new IllegalStateException(
						"Simulator does not fully support arcs.");
			}
		}
		return Math.abs(duration) * 1000f;
	}

	/**
	 * Check if the given move is an infinite arc move.
	 * 
	 * Infinite arc moves have an infinite target distance and rotation and thus
	 * increments needs to be calculated based on the finite arc radius.
	 */
	private static boolean isInfiniteArc(Move move) {
		return move.getMoveType() == MoveType.ARC
				&& Float.isInfinite(move.getDistanceTraveled())
				&& Float.isInfinite(move.getAngleTurned());
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	@Override
	public void removeMoveListener(MoveListener listener) {
		moveListeners.remove(listener);
	}

	@Override
	public Move getMovement() {
		return new Move(move.getMoveType(), getMovementIncrement(),
				getAngleIncrement(), isMoving());
	}

	@Override
	public void terminate() {
		stop();
		if (moveEndHandle != null) {
			moveEndHandle.cancel(true);
		}
		// executor.shutdownNow();
	}

	private class MoveEndRunner implements Runnable {

		private final Move move;

		public MoveEndRunner(Move move) {
			this.move = move;
		}

		@Override
		public void run() {
			// Current move has ended
			if (isMoving() && VirtualPilot.this.move == move) {
				movementStop();
			}
		}

	}

}
