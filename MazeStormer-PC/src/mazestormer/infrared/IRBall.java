package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class IRBall implements IRSource {
	
	private final PoseProvider staticPoseProvider;
	private final Envelope envelope;
	
	public IRBall(Pose pose) {
		this.staticPoseProvider = new StaticPoseProvider(pose);
		//TODO: cm's?
		this.envelope = new Envelope(this.staticPoseProvider, 7.5/2);
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public Envelope getEnvelope() {
		return this.envelope;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return this.staticPoseProvider;
	}
	
	private class StaticPoseProvider implements PoseProvider {
		
		private Pose pose;
		
		private StaticPoseProvider(Pose pose) {
			this.pose = pose;
		}

		@Override
		public Pose getPose() {
			return this.pose;
		}

		@Override
		public void setPose(Pose aPose) {
			// disabled
		}
	}
}
