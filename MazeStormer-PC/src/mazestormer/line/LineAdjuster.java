package mazestormer.line;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.player.Player;
import mazestormer.state.AbstractStateListener;

public class LineAdjuster {

	private Player player;

	public LineAdjuster(Player player, LineFinder lineFinder) {
		this.player = player;

		lineFinder.addStateListener(new LineFinderListener());
	}

	protected void log(String message) {
		System.out.println(message);
	}

	private IMaze getMaze() {
		return player.getMaze();
	}

	private PoseProvider getPoseProvider() {
		return player.getRobot().getPoseProvider();
	}

	private void adjust() {
		Pose pose = getMaze().toRelative(getPoseProvider().getPose());

		// Round orientation
		float newHeading = roundAngle(pose.getHeading());

		// Fix one coordinate
		float newX, newY;
		if (newHeading == -90f || newHeading == 90f) {
			newX = pose.getX();
			newY = roundToTileSize(pose.getY());
		} else {
			newX = roundToTileSize(pose.getX());
			newY = pose.getY();
		}

		// Adjust pose
		Pose newPose = getMaze().toAbsolute(new Pose(newX, newY, newHeading));
		getPoseProvider().setPose(newPose);
		log("Adjusted pose: " + newPose);
	}

	private float roundToTileSize(float coordinate) {
		final float tileSize = getMaze().getTileSize();
		return Math.round(coordinate / tileSize) * tileSize;
	}

	/**
	 * Get the orientation corresponding to the given angle.
	 * 
	 * @param angle
	 *            The angle.
	 */
	private float roundAngle(float angle) {
		angle = normalize(angle);

		if (angle > -45 && angle <= 45) {
			return 0f;
		} else if (angle > 45 && angle <= 135) {
			return 90f;
		} else if (angle > 135 || angle <= -135) {
			return 180f;
		} else {
			return -90f;
		}
	}

	/**
	 * Normalize the given angle between -180° and +180°.
	 * 
	 * @param angle
	 *            The angle to normalize.
	 */
	private float normalize(float angle) {
		while (angle > 180)
			angle -= 360f;
		while (angle < -180)
			angle += 360f;
		return angle;
	}

	private class LineFinderListener extends AbstractStateListener<LineFinder.LineFinderState> {
		@Override
		public void stateFinished() {
			adjust();
		}
	}

}
