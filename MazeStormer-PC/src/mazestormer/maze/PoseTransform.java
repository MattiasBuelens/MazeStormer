package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

public class PoseTransform {

	private final Pose referencePose;
	private final AffineTransform transform;

	public PoseTransform() {
		this(new Pose());
	}

	public PoseTransform(Pose referencePose) {
		this.referencePose = referencePose;
		this.transform = createTransform(referencePose);
	}

	/**
	 * Transform the given relative position to absolute coordinates.
	 * 
	 * @param position
	 *            The relative position.
	 * @return The absolute position.
	 */
	public Point transform(Point position) {
		checkNotNull(position);
		Point transformed = new Point(0, 0);
		transform.transform(position, transformed);
		return transformed;
	}

	/**
	 * Transform the given absolute position to relative coordinates.
	 * 
	 * @param position
	 *            The absolute position.
	 * @return The relative position.
	 */
	public Point inverseTransform(Point position) {
		checkNotNull(position);
		Point transformed = new Point(0, 0);
		try {
			transform.inverseTransform(position, transformed);
		} catch (NoninvertibleTransformException cannotHappen) {
			// Cannot happen
		}
		return transformed;
	}

	/**
	 * Transform the given relative heading to an absolute heading.
	 * 
	 * @param position
	 *            The relative heading.
	 * @return The absolute heading.
	 */
	public float transform(float heading) {
		return normalizeHeading(heading + referencePose.getHeading());
	}

	/**
	 * Transform the given absolute heading to a relative heading.
	 * 
	 * @param position
	 *            The absolute heading.
	 * @return The relative heading.
	 */
	public float inverseTransform(float heading) {
		return normalizeHeading(heading - referencePose.getHeading());
	}

	/**
	 * Transform the given relative pose to absolute coordinates.
	 * 
	 * @param position
	 *            The relative pose.
	 * @return The absolute pose.
	 */
	public Pose transform(Pose pose) {
		checkNotNull(pose);
		Pose transformed = new Pose();
		transformed.setLocation(transform(pose.getLocation()));
		transformed.setHeading(transform(pose.getHeading()));
		return transformed;
	}

	/**
	 * Transform the given absolute pose to relative coordinates.
	 * 
	 * @param position
	 *            The absolute pose.
	 * @return The relative pose.
	 */
	public Pose inverseTransform(Pose pose) {
		checkNotNull(pose);
		Pose transformed = new Pose();
		transformed.setLocation(inverseTransform(pose.getLocation()));
		transformed.setHeading(inverseTransform(pose.getHeading()));
		return transformed;
	}

	/**
	 * Create a transformation for a system with the given pose in its origin.
	 * 
	 * @param pose
	 *            The origin pose.
	 */
	private static AffineTransform createTransform(Pose pose) {
		AffineTransform transform = new AffineTransform();
		transform.translate(pose.getX(), pose.getY());
		transform.rotate(Math.toRadians(pose.getHeading()));
		return transform;
	}

	/**
	 * Normalize a given heading to ensure it is between -180 and +180 degrees.
	 * 
	 * @param heading
	 *            The heading.
	 */
	private static float normalizeHeading(float heading) {
		while (heading < 180)
			heading += 360;
		while (heading > 180)
			heading -= 360;
		return heading;
	}

}
