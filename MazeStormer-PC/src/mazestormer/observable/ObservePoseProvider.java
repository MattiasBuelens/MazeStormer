package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class ObservePoseProvider implements PoseProvider {

	private Pose pose;

	public ObservePoseProvider() {
		setPose(new Pose());
	}

	@Override
	public synchronized Pose getPose() {
		return clone(pose);
	}

	@Override
	public synchronized void setPose(Pose pose) {
		this.pose = clone(pose);
	}

	private static Pose clone(Pose pose) {
		return new Pose(pose.getX(), pose.getY(), pose.getHeading());
	}

}
