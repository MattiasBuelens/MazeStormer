package mazestormer.ui.map;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Edge;
import mazestormer.maze.Maze;
import mazestormer.maze.MazeListener;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;
import mazestormer.util.CoordUtils;

import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLineElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;

public class MazeLayer extends TransformLayer implements MazeListener {

	private static final String tileColor = CSS_SANDYBROWN_VALUE;
	private static final String wallColor = CSS_PERU_VALUE;
	private static final String lineColor = CSS_WHITE_VALUE;
	private static final String unknownColor = CSS_LIGHTGRAY_VALUE;

	private static final double tileSize = 1d;

	private final double edgeStrokeWidth;
	private static final int edgeDashSize = 4;

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

		this.edgeStrokeWidth = maze.getEdgeSize() / maze.getTileSize();

		setScale(maze.getTileSize());
		setOrigin(maze.getOrigin());
		setRotationCenter(0, 0);
	}

	@Override
	public SVGGElement getTransformElement() {
		if (mazeElement == null) {
			mazeElement = (SVGGElement) createElement(SVG_G_TAG);
			tilesGroup = (SVGGElement) createElement(SVG_G_TAG);
			edgesGroup = (SVGGElement) createElement(SVG_G_TAG);
			tilesGroup.setAttribute(SVG_ID_ATTRIBUTE, "tiles");
			edgesGroup.setAttribute(SVG_ID_ATTRIBUTE, "edges");

			mazeElement.appendChild(tilesGroup);
			mazeElement.appendChild(edgesGroup);

			for (Tile tile : maze.getTiles()) {
				tileAdded(tile);
			}
		}
		return mazeElement;
	}

	public Maze getMaze() {
		return maze;
	}

	private void addTile(Tile tile) {
		final TileElement tileElement = new TileElement(tile);
		tilesGroup.appendChild(tileElement.get());
		tiles.put(tile.getPosition(), tileElement);
	}

	private void setEdge(Edge edge) {
		for (LongPoint tilePosition : edge.getTouching()) {
			TileElement tileElement = tiles.get(tilePosition);
			if (tileElement != null) {
				tileElement.setEdge(edge.getOrientationFrom(tilePosition),
						edge.getType());
			}
		}
	}

	private void setOrigin(Pose origin) {
		setPosition(CoordUtils.toMapCoordinates(origin.getLocation()));
		setRotationAngle(-origin.getHeading());
		update();
	}

	@Override
	public void tileAdded(final Tile tile) {
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				addTile(tile);
			}
		});
	}

	@Override
	public void tileChanged(final Tile tile) {
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				tiles.get(tile.getPosition()).update();
			}
		});
	}

	@Override
	public void edgeChanged(final Edge edge) {
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				setEdge(edge);
			}
		});
	}

	@Override
	public void mazeOriginChanged(Pose origin) {
		setOrigin(origin);
	}

	@Override
	public void mazeCleared() {
		// Clear map
		tiles.clear();
		// Remove all child nodes
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				SVGUtils.removeChildNodes(tilesGroup);
				SVGUtils.removeChildNodes(edgesGroup);
			}
		});
	}

	@Override
	public int getZIndex() {
		return zIndex;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	private void tilePosition(SVGTransformable element, double tileX,
			double tileY) {
		// tileX runs from left to right
		double x = tileX;

		// tileY runs from bottom to top
		// Shift Y by one to position bottom left tile corner
		// Negate Y coordinate to account for Y-axis orientation
		double y = -(tileY + 1);

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
		private final EnumMap<Orientation, EdgeElement> edges = new EnumMap<Orientation, EdgeElement>(
				Orientation.class);

		private final SVGGElement tileGroup;
		private final SVGRectElement rect;
		private final BarcodeElement barcode;

		public TileElement(final Tile tile) {
			this.tile = tile;

			tileGroup = (SVGGElement) createElement(SVG_G_TAG);
			tilePosition(tileGroup, tile.getPosition());

			rect = (SVGRectElement) createElement(SVG_RECT_TAG);
			rect.setAttribute(SVG_WIDTH_ATTRIBUTE, tileSize + "");
			rect.setAttribute(SVG_HEIGHT_ATTRIBUTE, tileSize + "");
			rect.setAttribute(SVG_FILL_ATTRIBUTE, tileColor);
			tileGroup.appendChild(rect);

			barcode = new BarcodeElement(tile);
			tileGroup.appendChild(barcode.get());

			for (Orientation orientation : Orientation.values()) {
				setEdge(orientation, tile.getEdgeAt(orientation).getType());
			}
		}

		public Element get() {
			return tileGroup;
		}

		public LongPoint getPosition() {
			return tile.getPosition();
		}

		public void update() {
			// Update barcode
			barcode.update();
		}

		private void setEdge(Orientation orientation, Edge.EdgeType type) {
			checkNotNull(orientation);

			// Get or create edge element
			EdgeElement edgeElement = edges.get(orientation);
			if (edgeElement == null) {
				edgeElement = new EdgeElement(getPosition(), orientation);
				edges.put(orientation, edgeElement);
			}

			// Set type
			edgeElement.setType(type);

			// Detach edge element
			Element element = edgeElement.get();
			if (element.getParentNode() != null) {
				element.getParentNode().removeChild(element);
			}

			// Put walls above lines
			if (type == Edge.EdgeType.WALL) {
				edgesGroup.appendChild(edgeElement.get());
			} else {
				edgesGroup.insertBefore(edgeElement.get(),
						edgesGroup.getFirstChild());
			}
		}

	}

	private class BarcodeElement {

		private final Tile tile;
		private final SVGGElement barGroup;

		public BarcodeElement(final Tile tile) {
			this.tile = tile;
			barGroup = (SVGGElement) createElement(SVG_G_TAG);
			update();
		}

		public Element get() {
			return barGroup;
		}

		public void update() {
			// Remove previous bars
			SVGUtils.removeChildNodes(barGroup);
			// Start with black bar
			boolean isBlack = true;
			for (Rectangle2D bar : getMaze().getBarcodeBars(tile)) {
				// Create and add bar
				Element barRect = createElement(SVG_RECT_TAG);
				barRect.setAttribute(SVG_X_ATTRIBUTE, bar.getX() + "");
				barRect.setAttribute(SVG_Y_ATTRIBUTE, (1d - bar.getY() - bar.getHeight()) + "");
				barRect.setAttribute(SVG_WIDTH_ATTRIBUTE, bar.getWidth() + "");
				barRect.setAttribute(SVG_HEIGHT_ATTRIBUTE, bar.getHeight() + "");
				barRect.setAttribute(SVG_FILL_ATTRIBUTE,
						isBlack ? CSS_BLACK_VALUE : CSS_WHITE_VALUE);
				barGroup.appendChild(barRect);
				isBlack = !isBlack;
			}
		}

	}

	private class EdgeElement {

		private final LongPoint position;
		private final Orientation orientation;
		private Edge.EdgeType type;

		private final SVGLineElement line;

		public EdgeElement(LongPoint position, Orientation orientation) {
			this.position = position;
			this.orientation = orientation;
			this.type = Edge.EdgeType.UNKNOWN;

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

		public Edge.EdgeType getType() {
			return type;
		}

		public void setType(Edge.EdgeType type) {
			this.type = type;
			invokeDOMChange(new Runnable() {
				@Override
				public void run() {
					update();
				}
			});
		}

		private void update() {
			String color = unknownColor;
			boolean dashed = false;
			switch (getType()) {
			case WALL:
				color = wallColor;
				break;
			case OPEN:
				color = lineColor;
				break;
			case UNKNOWN:
			default:
				color = unknownColor;
				dashed = true;
			}
			// Stroke color
			line.setAttribute(SVG_STROKE_ATTRIBUTE, color);
			// Dashes
			line.setAttribute(SVG_STROKE_DASHARRAY_ATTRIBUTE,
					dashed ? (edgeDashSize * edgeStrokeWidth) + ""
							: SVG_NONE_VALUE);
		}

		private void setPoints() {
			Line edgeLine = getOrientation().getLine();
			// Convert Y-coordinates to top-to-bottom
			line.setAttribute(SVG_X1_ATTRIBUTE, edgeLine.getX1() + "");
			line.setAttribute(SVG_Y1_ATTRIBUTE, (1d - edgeLine.getY1()) + "");
			line.setAttribute(SVG_X2_ATTRIBUTE, edgeLine.getX2() + "");
			line.setAttribute(SVG_Y2_ATTRIBUTE, (1d - edgeLine.getY2()) + "");
		}

	}

}
