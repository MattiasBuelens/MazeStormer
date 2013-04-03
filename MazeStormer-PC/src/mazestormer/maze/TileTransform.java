package mazestormer.maze;

import mazestormer.util.LongPoint;

public class TileTransform {

	private final LongPoint translation;
	private final int nbCCWRotations;

	private static final TileTransform IDENTIFY = new TileTransform(new LongPoint(), 0);

	public TileTransform(LongPoint translation, int nbCCWRotations) {
		this.translation = translation;
		this.nbCCWRotations = nbCCWRotations;
	}

	public LongPoint transform(LongPoint tilePosition) {
		return null;
	}

	public LongPoint inverseTransform(LongPoint tilePosition) {
		return null;
	}

	public Orientation transform(Orientation orientation) {
		return orientation.rotateCounterClockwise(nbCCWRotations);
	}

	public Orientation inverseTransform(Orientation orientation) {
		return orientation.rotateClockwise(nbCCWRotations);
	}

	public static TileTransform getIdentity() {
		return IDENTIFY;
	}

}
