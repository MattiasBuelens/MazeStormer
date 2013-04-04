package mazestormer.maze;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import mazestormer.util.LongPoint;

public class TileTransform {

	private final LongPoint translation;
	private final int nbCCWRotations;
	private final AffineTransform transform;

	private static final TileTransform IDENTITY = new TileTransform(new LongPoint(0, 0), 0);

	public TileTransform(LongPoint translation, int nbCCWRotations) {
		this.translation = translation;
		this.nbCCWRotations = nbCCWRotations;
		this.transform = createTransform();
	}

	public LongPoint transform(LongPoint tilePosition) {
		LongPoint transformed = new LongPoint(0, 0);
		transform.transform(tilePosition, transformed);
		return transformed;
	}

	public LongPoint inverseTransform(LongPoint tilePosition) {
		LongPoint transformed = new LongPoint(0, 0);
		try {
			transform.inverseTransform(tilePosition, transformed);
		} catch (NoninvertibleTransformException cannotHappen) {
			// Cannot happen
		}
		return transformed;
	}

	public Orientation transform(Orientation orientation) {
		return orientation.rotateCounterClockwise(nbCCWRotations);
	}

	public Orientation inverseTransform(Orientation orientation) {
		return orientation.rotateClockwise(nbCCWRotations);
	}

	private AffineTransform createTransform() {
		AffineTransform transform = new AffineTransform();
		transform.translate(translation.getX(), translation.getY());
		transform.quadrantRotate(nbCCWRotations);
		return transform;
	}

	public static TileTransform getIdentity() {
		return IDENTITY;
	}

}
