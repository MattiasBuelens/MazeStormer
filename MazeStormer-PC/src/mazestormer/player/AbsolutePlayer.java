package mazestormer.player;

import java.util.logging.Logger;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.IRRobot;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.robot.Robot;
import mazestormer.world.IRDelegate;

public class AbsolutePlayer extends Player {

	private final RelativePlayer delegate;
	private PoseTransform transform = new PoseTransform();

	private final Robot robot;
	private final IRDelegate irDelegate;
	private final PoseProvider poseProvider;

	public AbsolutePlayer(RelativePlayer player) {
		this.delegate = player;

		this.robot = new AbsoluteRobot();
		this.irDelegate = new IRDelegate(this.delegate);
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
	
	public IRRobot getIRRobot() {
		return this.irDelegate.getIRRobot();
	}

	@Override
	public IMaze getMaze() {
		return delegate().getMaze();
	}

	@Override
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
