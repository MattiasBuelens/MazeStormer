package mazestormer.player;

import java.util.logging.Logger;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.IRRobot;
import mazestormer.infrared.PhysicalIRRobot;
import mazestormer.infrared.VirtualIRRobot;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.robot.Robot;

public class AbsolutePlayer extends Player {

	private final RelativePlayer delegate;
	private PoseTransform transform = new PoseTransform();

	private final Robot robot;
	private final IRRobot irRobot;
	private final PoseProvider poseProvider;

	public AbsolutePlayer(RelativePlayer player) {
		this.delegate = player;

		this.robot = new AbsoluteRobot();
		this.irRobot = getAnIRRobot(this.robot);
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
	
	private static IRRobot getAnIRRobot(Robot robot) {
		// TODO: Physical <> virtual
		// dummy if test without yellow remarks :D
		if(robot.hashCode()!=0) {
			return (new VirtualIRRobot(robot));
		} else {
			return (new PhysicalIRRobot(robot));
		}
	}
	
	public IRRobot getIRRobot() {
		return this.irRobot;
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
