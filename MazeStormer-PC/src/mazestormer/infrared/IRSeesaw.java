package mazestormer.infrared;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.world.ModelType;

public class IRSeesaw implements IRSource {

	private final IMaze maze;
	private final Seesaw seesaw;
	private final Envelope envelope;

	public static final double DETECTION_RADIUS = 80; // cm

	public IRSeesaw(IMaze maze, Seesaw seesaw) {
		this.maze = maze;
		this.seesaw = seesaw;
		this.envelope = new RectangularEnvelope(maze.getEdgeSize(), maze.getTileSize(), DETECTION_RADIUS);
	}

	public IMaze getMaze() {
		return maze;
	}

	public Seesaw getSeesaw() {
		return seesaw;
	}

	private Pose getPose() {
		Tile seesawTile = getMaze().getSeesawTile(getSeesaw().getClosedBarcode());
		Tile barcodeTile = getMaze().getBarcodeTile(getSeesaw().getClosedBarcode());
		Orientation orientation = seesawTile.orientationTo(barcodeTile);
		// Get center of infrared source
		Point position = getMaze().getTileCenter(seesawTile.getPosition());
		position = getMaze().toAbsolute(position);
		position = orientation.shift(position, getShiftFromCenter());
		// Get heading
		float heading = orientation.getAngle();
		// Create pose
		Pose pose = new Pose();
		pose.setLocation(position);
		pose.setHeading(heading);
		return pose;
	}

	private float getShiftFromCenter() {
		// Place behind virtual wall
		return getMaze().getTileSize() / 2f + getMaze().getEdgeSize();
	}

	@Override
	public PoseProvider getPoseProvider() {
		return new StaticPoseProvider(getPose());
	}

	@Override
	public ModelType getModelType() {
		return ModelType.VIRTUAL;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public Envelope getEnvelope() {
		return envelope;
	}

}
