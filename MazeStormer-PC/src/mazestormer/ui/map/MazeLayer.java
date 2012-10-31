package mazestormer.ui.map;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.Edge;
import mazestormer.maze.Maze;
import mazestormer.maze.MazeListener;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLineElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;

public class MazeLayer extends TransformLayer implements MazeListener {

	private static final double tileSize = 1d;
	private static final double edgeStrokeWidth = 0.05d;

	private final Maze maze;
	private int zIndex = 0;

	private SVGGElement mazeElement;
	private SVGGElement tilesGroup;
	private SVGGElement edgesGroup;
	private Map<LongPoint, TileElement> tiles = new HashMap<LongPoint, TileElement>();

	public MazeLayer(String name, Maze maze) {
		super(name);
		this.maze = maze;
		maze.addListener(this);

		setScale(getMaze().getTileSize());
		setOrigin(getMaze().getOrigin());
		setRotationCenter(0, 0);
	}

	@Override
	public SVGGElement getTransformElement() {
		if (mazeElement == null) {
			mazeElement = (SVGGElement) createElement(SVG_G_TAG);
			tilesGroup = (SVGGElement) createElement(SVG_G_TAG);
			edgesGroup = (SVGGElement) createElement(SVG_G_TAG);

			mazeElement.appendChild(tilesGroup);
			mazeElement.appendChild(edgesGroup);

			for (Tile tile : maze.getTiles()) {
				tileAdded(tile);
			}
		}
		return mazeElement;
	}

	private Maze getMaze() {
		return maze;
	}

	private void setOrigin(Pose origin) {
		setPosition(origin.getX(), -origin.getY());
		setRotationAngle(-origin.getHeading());
		update();
	}

	@Override
	public void tileAdded(final Tile tile) {
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				final TileElement tileElement = new TileElement(tile);
				tiles.put(tile.getPosition(), tileElement);
			}
		});
	}

	@Override
	public void tileChanged(Tile tile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAdded(Edge edge) {
		for (LongPoint tilePosition : edge.getTouching()) {
			TileElement tileElement = tiles.get(tilePosition);
			if (tileElement != null) {
				tileElement.addEdge(edge);
			}
		}
	}

	@Override
	public void mazeOriginChanged(Pose origin) {
		setOrigin(origin);
	}

	@Override
	public void mazeCleared() {
		// Remove all child nodes
		Element element = getElement();
		while (element.hasChildNodes()) {
			element.removeChild(element.getFirstChild());
		}
		// Clear map
		tiles.clear();
	}

	@Override
	public int getZIndex() {
		return zIndex;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	private void tilePosition(SVGTransformable element, double x, double y) {
		// Shift Y by one to position bottom left tile corner
		// Negate Y coordinate to account for Y-axis orientation
		y = -(y + 1);

		// Apply translation
		final SVGTransform translate = new SVGOMTransform();
		translate.setTranslate((float) x, (float) y);

		SVGTransformList list = element.getTransform().getBaseVal();
		list.initialize(translate);
	}

	private void tilePosition(SVGTransformable element, LongPoint tilePosition) {
		tilePosition(element, tilePosition.getX(), tilePosition.getY());
	}

	private class TileElement {

		private final Tile tile;
		private final EnumMap<Orientation, EdgeElement> edges = new EnumMap<Orientation, EdgeElement>(Orientation.class);

		private final SVGRectElement rect;

		public TileElement(final Tile tile) {
			this.tile = tile;

			rect = (SVGRectElement) createElement(SVG_RECT_TAG);
			rect.setAttribute(SVG_WIDTH_ATTRIBUTE, tileSize + "");
			rect.setAttribute(SVG_HEIGHT_ATTRIBUTE, tileSize + "");
			rect.setAttribute(SVG_FILL_ATTRIBUTE, CSS_SANDYBROWN_VALUE);
			tilesGroup.appendChild(rect);
			tilePosition(rect, tile.getPosition());

			for (Orientation orientation : Orientation.values()) {
				setEdge(orientation, tile.hasEdgeAt(orientation));
			}
		}

		public LongPoint getPosition() {
			return tile.getPosition();
		}

		private void setEdge(Orientation orientation, boolean isWall) {
			checkNotNull(orientation);
			EdgeElement edgeElement = edges.get(orientation);
			if (edgeElement == null) {
				edgeElement = new EdgeElement(getPosition(), orientation);
				edges.put(orientation, edgeElement);
			}
			edgeElement.setWall(isWall);
			// Put walls above lines
			if (edgeElement.isWall()) {
				edgesGroup.appendChild(edgeElement.get());
			} else {
				edgesGroup.insertBefore(edgeElement.get(), edgesGroup.getFirstChild());
			}
		}

		public void addEdge(final Edge edge) {
			checkNotNull(edge);
			invokeDOMChange(new Runnable() {
				@Override
				public void run() {
					setEdge(edge.getOrientationFrom(getPosition()), true);
				}
			});
		}

	}

	private class EdgeElement {

		private final LongPoint position;
		private final Orientation orientation;
		private boolean isWall;

		private final SVGLineElement line;

		public EdgeElement(LongPoint position, Orientation orientation) {
			this.position = position;
			this.orientation = orientation;

			line = (SVGLineElement) createElement(SVG_LINE_TAG);
			line.setAttribute(SVG_STROKE_WIDTH_ATTRIBUTE, edgeStrokeWidth + "");
			tilePosition(line, getPosition());

			update();
			setPoints();
		}

		public Element get() {
			return line;
		}

		public LongPoint getPosition() {
			return position;
		}

		public Orientation getOrientation() {
			return orientation;
		}

		public boolean isWall() {
			return isWall;
		}

		public void setWall(boolean isWall) {
			this.isWall = isWall;
			invokeDOMChange(new Runnable() {
				@Override
				public void run() {
					update();
				}
			});
		}

		private void update() {
			// Set stroke color
			line.setAttribute(SVG_STROKE_ATTRIBUTE, isWall() ? CSS_PERU_VALUE : CSS_WHITE_VALUE);
		}

		private void setPoints() {
			int startX = 0, startY = 0, endX = 0, endY = 0;
			switch (getOrientation()) {
			case NORTH:
				endX = 1;
				break;
			case WEST:
				endY = 1;
				break;
			case EAST:
				startX = 1;
				endX = 1;
			case SOUTH:
				startY = 1;
				endY = 1;
			}

			line.setAttribute(SVG_X1_ATTRIBUTE, startX + "");
			line.setAttribute(SVG_Y1_ATTRIBUTE, startY + "");
			line.setAttribute(SVG_X2_ATTRIBUTE, endX + "");
			line.setAttribute(SVG_Y2_ATTRIBUTE, endY + "");
		}

	}

}
