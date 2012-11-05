package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IPolygonControlController extends EventSource {

	public void startPolygon(int nbSides, double sideLength, Direction direction);

	public void stopPolygon();

	public IParametersController parameters();

	public enum Direction {
		ClockWise("Clockwise"), CounterClockWise("Counter-clockwise");

		private final String name;

		private Direction(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
