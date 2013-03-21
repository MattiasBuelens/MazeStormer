package mazestormer.line;

import lejos.robotics.navigation.Pose;
import mazestormer.player.Player;
import mazestormer.state.AbstractStateListener;

public class LineAdjuster {
	private Player playa;

	public LineAdjuster(Player player, LineFinderRunner lineFinderRunner) {
		lineFinderRunner.addStateListener(new LineFinderListener());
		this.playa = player;
	}

	private void adjust() {
		Pose pose = playa.getRobot().getPoseProvider().getPose();
		
		//Rond orientatie af
		float newHeading = roundAngle(pose.getHeading());
		
		//Zet 1 coordinaat just
		float newX, newY;
		
		if(newHeading == -90f || newHeading == 90f){
			newX = pose.getX();
			newY = roundToMultipleOfForty(pose.getY());
		} else {
			newX = roundToMultipleOfForty(pose.getX());
			newY = pose.getY();
		}
		
		Pose newPose = new Pose(newX, newY, newHeading);
		System.out.println(newPose);
		playa.getRobot().getPoseProvider().setPose(newPose);
	}

	private float roundToMultipleOfForty(float coordinate){
		 return Math.round(coordinate/40f) * 40f;
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
	
	private class LineFinderListener extends
			AbstractStateListener<LineFinderRunner.LineFinderState> {
		@Override
		public void stateFinished() {
			adjust();
		}
	}

}
