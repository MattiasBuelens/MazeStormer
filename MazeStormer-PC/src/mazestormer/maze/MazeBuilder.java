package mazestormer.maze;

import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeature;

public class MazeBuilder {

	private final Maze maze;

	private final RangeFeatureListener rangeListener = new RangeFeatureListener();
	// private final LightFeatureListener lightListener = new LightFeatureListener();

	public MazeBuilder(Maze maze) {
		this.maze = maze;
	}

	public MazeBuilder() {
		this(new Maze());
	}

	public Maze getMaze() {
		return maze;
	}

	/**
	 * Start listening to the given range feature detector.
	 * 
	 * @param detector
	 * 			A feature detector which produces {@link RangeFeature}s.
	 */
	public void addRangeDetector(FeatureDetector detector) {
		detector.addListener(rangeListener);
	}

	private class RangeFeatureListener implements FeatureListener {

		@Override
		public void featureDetected(Feature feature, FeatureDetector detector) {
			if (feature == null)
				return;
			RangeFeature rangeFeature = (RangeFeature) feature;

			// TODO Build edges
			System.out.println(rangeFeature);
		}

	}
}
