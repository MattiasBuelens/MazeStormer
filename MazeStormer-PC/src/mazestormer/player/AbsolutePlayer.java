package mazestormer.player;

import java.util.logging.Logger;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.PoseTransform;
import mazestormer.robot.Robot;

public class AbsolutePlayer implements Player {

	private final RelativePlayer delegate;
	private PoseTransform transform = new PoseTransform();

	private final Robot robot;
	private final PoseProvider poseProvider;

	public AbsolutePlayer(RelativePlayer player) {
		this.delegate = player;

		this.robot = new AbsoluteRobot();
		this.poseProvider = new AbsolutePoseProvider();
	}

	public RelativePlayer delegate() {
		return delegate;
	}

	public PoseTransform getTransform() {
		return transform;
	}

	public void setTransform(PoseTransform transform) {
		this.transform = transform;
	}

	public String getPlayerID() {
		return delegate().getPlayerID();
	}

	public void setPlayerID(String playerID) {
		delegate().setPlayerID(playerID);
	}

	public Robot getRobot() {
		return robot;
	}

	public Maze getMaze() {
		return delegate().getMaze();
	}

	public Logger getLogger() {
		return delegate().getLogger();
	}

	public void setRelativePose(Pose pose) {
		delegate().getRobot().getPoseProvider().setPose(pose);
	}

	private class AbsoluteRobot implements Robot {

		@Override
		public PoseProvider getPoseProvider() {
			return poseProvider;
		}

	}

	private class AbsolutePoseProvider implements PoseProvider {

		@Override
		public Pose getPose() {
			Pose relativePose = delegate.getRobot().getPoseProvider().getPose();
			return transform.transform(relativePose);
		}

		@Override
		public void setPose(Pose pose) {
			Pose relativePose = transform.inverseTransform(pose);
			delegate.getRobot().getPoseProvider().setPose(relativePose);
		}

	}

}
