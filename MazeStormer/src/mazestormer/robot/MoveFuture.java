package mazestormer.robot;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Move.MoveType;
import mazestormer.util.AbstractFuture;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

/**
 * A future which resolves when a move of the given type is fully completed.
 * 
 * <p>
 * A move is considered as completed when a move with that type is stopped and
 * the pilot is not moving at that time. If it is still moving, it would
 * indicate that the move was partially completed and interrupted by a different
 * move.
 * </p>
 */
public class MoveFuture extends AbstractFuture<Boolean> implements
		MoveListener, FutureListener<Boolean> {

	private final Pilot pilot;
	private final MoveType moveType;
	private volatile boolean isStarted = false;

	public MoveFuture(Pilot pilot, MoveType move) {
		this.pilot = pilot;
		this.moveType = move;
		pilot.addMoveListener(this);
	}

	private boolean matchesMove(Move move) {
		return this.moveType == move.getMoveType();
	}

	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		if (!isDone()) {
			if (matchesMove(event)) {
				// Move started
				isStarted = true;
			} else if (isStarted) {
				// Started a different move
				resolve(false);
			}
		}
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		if (!isDone() && isStarted) {
			// Check if the move matches
			// and the pilot is no longer moving
			boolean success = matchesMove(event) && !event.isMoving();
			resolve(success);
		}
	}

	private void unregister() {
		// Unregister
		pilot.removeMoveListener(this);
	}

	@Override
	public void futureResolved(Future<? extends Boolean> future) {
		unregister();
	}

	@Override
	public void futureCancelled(Future<? extends Boolean> future) {
		unregister();
	}

}