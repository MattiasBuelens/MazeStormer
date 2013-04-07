package mazestormer.simulator;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRSource;
import mazestormer.infrared.VirtualIRSource;
import mazestormer.maze.Maze;
import mazestormer.robot.IRSensor;
import mazestormer.world.World;

public class WorldIRDetector implements IRSensor {
	
	private double radius;
	private float range;
	
	private final PoseProvider poseProvider;
	private final World world;
	private final IRDetectionMode mode;

	public WorldIRDetector(World world, PoseProvider poseProvider, double radius, float range, IRDetectionMode mode) {
		this.world = world;
		this.poseProvider = poseProvider;
		this.mode = mode;
		setRadius(radius);
		setRange(range);
	}
	
	public IRDetectionMode getMode() {
		return this.mode;
	}

	private World getWorld() {
		return this.world;
	}

	private PoseProvider getPoseProvider() {
		return this.poseProvider;
	}

	private Maze getMaze() {
		return getWorld().getMaze();
	}
	
	private double getRadius() {
		return this.radius;
	}
	
	private void setRadius(double radius) {
		this.radius = Math.abs(radius);
	}
	
	private float getRange() {
		return this.range;
	}
	
	private void setRange(float range) {
		this.range = Math.abs(range);
	}

	@Override
	public float getAngle() {
		return detect();
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}
	
	private float detect() {
		Float[] angles = getDetectedIRAngles();
        
        if (angles.length != 0) {
			// Selecting the closest angle difference
			float bestAngle = 180;
			for (int i=0; i<angles.length; i++) {
				float f = angles[i];
				if (Math.abs(bestAngle) > Math.abs(f)) {
					bestAngle = f;
				}
			}
			return bestAngle;
		}
        return Float.NaN;
	}
	
	private Float[] getDetectedIRAngles() {
		// TODO: seesaw troubles
		// TODO: maze collection -> world collection
		
		List<Float> detectedAngles = new ArrayList<Float>();
		
		for (IRSource irs : getMaze().getAllModelsClass(getMode().getIRSourceType())) {
			Float result = (irs.isEmitting()) ? getDetectedAngle(irs.getEnvelope()) : Float.NaN;
			if (!Float.isNaN(result)) {
				detectedAngles.add(result);
			}
		}
		return detectedAngles.toArray(new Float[0]);
	}
	
	private Float getDetectedAngle(Envelope envelope) {
		Pose currentPose = getPoseProvider().getPose();
		Pose otherPose = envelope.getPoseProvider().getPose();
		
		boolean isDetected = false;
		double angle = 0;
		// Avoids self-detection
		// Avoids not-detectable joy-riding objects
		if (!((currentPose.getX() == otherPose.getX()) && (currentPose.getY() == otherPose.getY()))) {
			Point2D currentPoint = new Point2D.Double(currentPose.getX(), currentPose.getY());
			
			Point2D h_e = new Point2D.Double(Math.cos(currentPose.getHeading()), Math.sin(currentPose.getHeading()));
			Point2D[] cps = envelope.getClosestPoints(currentPoint);
			Line2D[] edgeLines = getMaze().getEdgeLines().toArray(new Line2D.Double[0]);
			for (int i=0; i<cps.length && !isDetected; i++) {
				Line2D l = new Line2D.Double(currentPoint, cps[i]);
				
				if (l.getP1().distance(l.getP2()) > getRadius()) {
					continue;
				}
				
				angle = getAngle(cps[i], currentPoint, h_e);
				if (angle > getRange() ||  angle < (-1)*getRange()) {
					continue;
				}
				
				isDetected = true;
				for (int j=0; j<edgeLines.length && isDetected; j++) {
					isDetected = !l.intersectsLine(edgeLines[j]);
				}
			}
		}
		
		// angle to heading axis
		Float result = (isDetected) ? (float) angle : Float.NaN;
		return result;
	}	
		
	private static double getAngle(Point2D p1, Point2D center, Point2D p2) {
		double cp1 = center.distance(p1);
		double cp2 = center.distance(p2);
		double p1p2 = p1.distance(p2);
		return Math.acos((cp1*cp1+cp2*cp2-p1p2*p1p2)/(2*cp1*cp2));
	}
	
	public enum IRDetectionMode {
		VIRTUAL(IRSource.class), SEMI_PHYSICAL(VirtualIRSource.class);
		
		private final Class<? extends IRSource> irSourceType;

		private <T extends IRSource> IRDetectionMode(Class<T> irSourceType) {
			this.irSourceType = irSourceType;
		}
		
		public Class<? extends IRSource> getIRSourceType(){
			return this.irSourceType;
		}
	}
}
