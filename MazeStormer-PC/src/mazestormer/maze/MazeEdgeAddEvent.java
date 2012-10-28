package mazestormer.maze;

public class MazeEdgeAddEvent {

	private final Edge edge;

	public MazeEdgeAddEvent(Edge edge) {
		this.edge = edge;
	}

	public Edge getEdge() {
		return edge;
	}

}
