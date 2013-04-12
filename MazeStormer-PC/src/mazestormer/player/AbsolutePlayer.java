package mazestormer.player;

import java.util.logging.Logger;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.robot.Robot;

public class AbsolutePlayer implements Player {

	private final CommandTools delegate;
	private PoseTransform transform = PoseTransform.getIdentity();

	private final Robot robot;
	private final PoseProvider poseProvider;

	public AbsolutePlayer(CommandTools player) {
		this.delegate = player;

		this.robot = new AbsoluteRobot();
		this.poseProvider = new AbsolutePoseProvider();
	}

	public CommandTools delegate() {
		return delegate;
	}

	public PoseTransform getTransform() {
		return transform;
	}

	public void setTransform(PoseTransform transform) {
		this.transform = transform;
	}

	@Override
	public String getPlayerID() {
		return delegate().getPlayerID();
	}

	@Override
	public void setPlayerID(String playerID) {
		delegate().setPlayerID(playerID);
	}

	@Override
	public Robot getRobot() {
		return robot;
	}

	@Override
	public IMaze getMaze() {
		return delegate().getMaze();
	}

	@Override
	public Logger getLogger() {
		return delegate().getLogger();
	}

	@Override
	public void addPlayerListener(PlayerListener listener) {
		delegate().addPlayerListener(listener);
	}

	@Override
	public void removePlayerListener(PlayerListener listener) {
		delegate().removePlayerListener(listener);
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
			if (delegate.getRobot() == null) {
				return new Pose();
			}

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
