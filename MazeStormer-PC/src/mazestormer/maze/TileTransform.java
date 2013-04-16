package mazestormer.maze;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import mazestormer.util.LongPoint;

public class TileTransform {

	private final AffineTransform transform;
	private final int nbCCWRotations;

	private static final TileTransform IDENTITY = new TileTransform(new LongPoint(0, 0), 0);

	public TileTransform(LongPoint translation, int nbCCWRotations) {
		this(createTransform(translation, nbCCWRotations), nbCCWRotations);
	}

	private TileTransform(AffineTransform transform, int nbCCWRotations) {
		this.transform = transform;
		this.nbCCWRotations = nbCCWRotations & 3;
	}

	/**
	 * Transform the given relative position to absolute coordinates.
	 * 
	 * @param position
	 *            The relative position.
	 * @return The absolute position.
	 */
	public LongPoint transform(LongPoint tilePosition) {
		LongPoint transformed = new LongPoint(0, 0);
		transform.transform(tilePosition, transformed);
		return transformed;
	}

	/**
	 * Transform the given absolute position to relative coordinates.
	 * 
	 * @param position
	 *            The absolute position.
	 * @return The relative position.
	 */
	public LongPoint inverseTransform(LongPoint tilePosition) {
		LongPoint transformed = new LongPoint(0, 0);
		try {
			transform.inverseTransform(tilePosition, transformed);
		} catch (NoninvertibleTransformException cannotHappen) {
			// Cannot happen
		}
		return transformed;
	}

	/**
	 * Transform the given relative orientation to an absolute orientation.
	 * 
	 * @param orientation
	 *            The relative orientation.
	 * @return The absolute orientation.
	 */
	public Orientation transform(Orientation orientation) {
		return orientation.rotateCounterClockwise(nbCCWRotations);
	}

	/**
	 * Transform the given absolute orientation to an relative orientation.
	 * 
	 * @param orientation
	 *            The absolute orientation.
	 * @return The relative orientation.
	 */
	public Orientation inverseTransform(Orientation orientation) {
		return orientation.rotateClockwise(nbCCWRotations);
	}

	/**
	 * Get the inverse tile transformation.
	 */
	public TileTransform inverse() {
		try {
			return new TileTransform(transform.createInverse(), 4 - nbCCWRotations);
		} catch (NoninvertibleTransformException cannotHappen) {
			return null;
		}
	}

	public PoseTransform toPoseTransform(){
		//TODO: Implementeren Mattias, aub
		return null;
	}
	
	/**
	 * Create a transformation for a system with the given translation and
	 * number of rotation.
	 * 
	 * @param translation
	 *            The translation.
	 * @param nbCCWRotations
	 *            The number of counter-clockwise rotations.
	 */
	private static AffineTransform createTransform(LongPoint translation, int nbCCWRotations) {
		AffineTransform transform = new AffineTransform();
		transform.translate(translation.getX(), translation.getY());
		transform.quadrantRotate(nbCCWRotations);
		return transform;
	}

	/**
	 * Get the identity tile transformation.
	 */
	public static TileTransform getIdentity() {
		return IDENTITY;
	}
}
