package mazestormer.ui.map;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Edge;
import mazestormer.maze.IMaze;
import mazestormer.maze.MazeListener;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.util.CoordUtils;
import mazestormer.util.LongPoint;

import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDefsElement;
import org.w3c.dom.svg.SVGDescElement;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLineElement;
import org.w3c.dom.svg.SVGLinearGradientElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;

public class MazeLayer extends TransformLayer implements MazeListener {

	private static final String tileColor = CSS_SANDYBROWN_VALUE;
	private static final String wallColor = CSS_PERU_VALUE;
	private static final String lineColor = CSS_WHITE_VALUE;
	private static final String unknownColor = CSS_LIGHTGRAY_VALUE;

	private static final String infraredColor = CSS_RED_VALUE;

	// low = hsl(28, 87.1%, 66.7%) = tileColor
	private static final String seesawLowColor = CSS_SANDYBROWN_VALUE;
	// mid: hsl(28, 87.1%, 69.7%) = rgb(245, 173, 110)
	private static final String seesawMidColor = "rgb(245,173,110)";
	// hi: hsl(28, 87.1%, 72.7%) = rgb(246, 181, 125)
	private static final String seesawHighColor = "rgb(246,181,125)";

	private static final double tileSize = 1d;

	private final double edgeStrokeWidth;
	private static final int edgeDashSize = 4;

	private final IMaze maze;
	private int zIndex = 0;

	private SVGGElement mazeElement;
	private SVGGElement tilesGroup;
	private SVGGElement edgesGroup;
	private SVGGElement overlayGroup;
	private Map<LongPoint, TileElement> tiles = new HashMap<LongPoint, TileElement>();

	public MazeLayer(String name, IMaze maze) {
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
		if (mazeElement != null)
			return mazeElement;

		mazeElement = (SVGGElement) createElement(SVG_G_TAG);
		tilesGroup = (SVGGElement) createElement(SVG_G_TAG);
		edgesGroup = (SVGGElement) createElement(SVG_G_TAG);
		overlayGroup = (SVGGElement) createElement(SVG_G_TAG);
		tilesGroup.setAttribute(SVG_ID_ATTRIBUTE, "tiles");
		edgesGroup.setAttribute(SVG_ID_ATTRIBUTE, "edges");
		overlayGroup.setAttribute(SVG_ID_ATTRIBUTE, "overlay");

		mazeElement.appendChild(defineGradients());
		mazeElement.appendChild(tilesGroup);
		mazeElement.appendChild(edgesGroup);
		mazeElement.appendChild(overlayGroup);

		for (Tile tile : maze.getTiles()) {
			tileAdded(tile);
		}

		return mazeElement;
	}

	private Node defineGradients() {
		SVGDefsElement defs = (SVGDefsElement) createElement(SVG_DEFS_TAG);

		SVGLinearGradientElement seesawOpen = new SVGUtils.LinearGradientBuilder(getDocument(), "seesawOpen")
				.vertical().add(0, seesawLowColor).add(1, seesawMidColor).build();
		defs.appendChild(seesawOpen);

		SVGLinearGradientElement seesawClosed = new SVGUtils.LinearGradientBuilder(getDocument(), "seesawClosed")
				.vertical().add(0, seesawHighColor).add(1, seesawMidColor).build();
		defs.appendChild(seesawClosed);

		SVGLinearGradientElement seesawInfrared = new SVGUtils.LinearGradientBuilder(getDocument(), "seesawInfrared")
				.vertical().add(0, infraredColor, 0).add(1, infraredColor, 0.75f).build();
		defs.appendChild(seesawInfrared);

		return defs;
	}

	public IMaze getMaze() {
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
				tileElement.setEdge(edge.getOrientationFrom(tilePosition), edge.getType());
			}
		}
	}

	private void setOrigin(Pose origin) {
		setPosition(CoordUtils.toMapCoordinates(origin.getLocation()));
		setRotationAngle(-origin.getHeading());
		update();
	}

	private boolean isTileClosed(Tile tile) {
		return tile.getClosedSides().size() == tile.getEdges().size();
	}

	// private boolean isEdgeBetweenClosed(Edge edge) {
	// for (LongPoint touchingPosition : edge.getTouching()) {
	// if (!isTileClosed(getMaze().getTileAt(touchingPosition))) {
	// return false;
	// }
	// }
	// return true;
	// }

	private String getTooltipText(Tile tile) {
		StringBuilder sb = new StringBuilder();
		sb.append("<strong>Tile</strong>");
		// Position
		sb.append("<br>X: ").append(tile.getX());
		sb.append("<br>Y: ").append(tile.getY());
		// Barcode
		if (tile.hasBarcode()) {
			sb.append("<br>Barcode: ").append(tile.getBarcode().getValue());
		}
		// Seesaw
		if (tile.isSeesaw()) {
			sb.append("<br>Seesaw: ").append(tile.isSeesawOpen() ? "open" : "closed");
			sb.append(" facing ").append(tile.getSeesawBarcode().getValue());
		}
		return sb.toString();
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
	public void tileExplored(Tile tile) {
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
				SVGUtils.removeChildNodes(overlayGroup);
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

	private void tilePosition(SVGTransformable element, double tileX, double tileY) {
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
		private final EnumMap<Orientation, EdgeElement> edges = new EnumMap<Orientation, EdgeElement>(Orientation.class);

		private final SVGGElement tileGroup;
		private final SVGRectElement rect;
		private final BarcodeElement barcode;
		private final SeesawElement seesaw;
		private final SVGDescElement tooltip;

		public TileElement(final Tile tile) {
			this.tile = tile;

			// Tile: rectangle
			tileGroup = (SVGGElement) createElement(SVG_G_TAG);
			tilePosition(tileGroup, tile.getPosition());

			rect = (SVGRectElement) createElement(SVG_RECT_TAG);
			rect.setAttribute(SVG_WIDTH_ATTRIBUTE, SVGUtils.doubleString(tileSize));
			rect.setAttribute(SVG_HEIGHT_ATTRIBUTE, SVGUtils.doubleString(tileSize));
			tileGroup.appendChild(rect);
			updateTile();

			// Tile: barcode
			barcode = new BarcodeElement(tile);

			// Edges
			for (Orientation orientation : Orientation.values()) {
				setEdge(orientation, tile.getEdgeAt(orientation).getType());
			}

			// Seesaw
			seesaw = new SeesawElement(tile);
			tilePosition(seesaw.get(), tile.getPosition());

			// Tooltip
			tooltip = (SVGDescElement) createElement(SVG_DESC_TAG);
			tileGroup.appendChild(tooltip);
			updateTooltip();
		}

		public Element get() {
			return tileGroup;
		}

		public LongPoint getPosition() {
			return tile.getPosition();
		}

		public void update() {
			updateTile();
			updateBarcode();
			updateSeesaw();
			updateTooltip();
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
				// Do not show walls between closed tiles
				// if (!isEdgeBetweenClosed(tile.getEdgeAt(orientation))) {
				edgesGroup.appendChild(edgeElement.get());
				// }
			} else {
				edgesGroup.insertBefore(edgeElement.get(), edgesGroup.getFirstChild());
			}
		}

		private void updateTile() {
			if (isTileClosed(tile)) {
				rect.setAttribute(SVG_FILL_ATTRIBUTE, wallColor);
				// Do not show closed tiles
				// if (rect.getParentNode() != null) {
				// tileGroup.removeChild(rect);
				// }
			} else {
				rect.setAttribute(SVG_FILL_ATTRIBUTE, tileColor);
				// Show regular tiles
				// if (rect.getParentNode() == null) {
				// tileGroup.insertBefore(rect, tileGroup.getFirstChild());
				// }
			}
		}

		private void updateBarcode() {
			if (tile.hasBarcode()) {
				barcode.update();
				if (barcode.get().getParentNode() == null) {
					tileGroup.appendChild(barcode.get());
				}
			} else {
				if (barcode.get().getParentNode() != null) {
					tileGroup.removeChild(barcode.get());
				}
			}
		}

		private void updateSeesaw() {
			if (tile.isSeesaw()) {
				seesaw.update();
				if (seesaw.get().getParentNode() == null) {
					overlayGroup.appendChild(seesaw.get());
				}
			} else {
				if (seesaw.get().getParentNode() != null) {
					overlayGroup.removeChild(seesaw.get());
				}
			}
		}

		private void updateTooltip() {
			tooltip.setTextContent(getTooltipText(tile));
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
				barRect.setAttribute(SVG_FILL_ATTRIBUTE, isBlack ? CSS_BLACK_VALUE : CSS_WHITE_VALUE);
				barGroup.appendChild(barRect);
				isBlack = !isBlack;
			}
		}

	}

	private class SeesawElement {

		private final Tile tile;
		private final SVGGElement seesawGroup;
		private final Element openSide;
		private final Element closedSide;
		private final SVGDescElement tooltip;

		private Element currentSide;
		private Element otherSide;

		public SeesawElement(final Tile tile) {
			this.tile = tile;

			seesawGroup = (SVGGElement) createElement(SVG_G_TAG);

			// Open side: tile
			openSide = createElement(SVG_RECT_TAG);
			openSide.setAttribute(SVG_X_ATTRIBUTE, SVGUtils.doubleString(-0.5 + edgeStrokeWidth / 2));
			openSide.setAttribute(SVG_Y_ATTRIBUTE, SVGUtils.doubleString(-0.5 - edgeStrokeWidth));
			openSide.setAttribute(SVG_WIDTH_ATTRIBUTE, SVGUtils.doubleString(1 - edgeStrokeWidth));
			openSide.setAttribute(SVG_HEIGHT_ATTRIBUTE, SVGUtils.doubleString(1 + edgeStrokeWidth));
			openSide.setAttribute(SVG_FILL_ATTRIBUTE, "url(#seesawOpen)");

			// Closed side: tile
			closedSide = createElement(SVG_G_TAG);
			SVGRectElement closedRect = (SVGRectElement) createElement(SVG_RECT_TAG);
			closedRect.setAttribute(SVG_X_ATTRIBUTE, SVGUtils.doubleString(-0.5 + edgeStrokeWidth / 2));
			closedRect.setAttribute(SVG_Y_ATTRIBUTE, SVGUtils.doubleString(-0.5 - edgeStrokeWidth / 2));
			closedRect.setAttribute(SVG_WIDTH_ATTRIBUTE, SVGUtils.doubleString(1 - edgeStrokeWidth));
			closedRect.setAttribute(SVG_HEIGHT_ATTRIBUTE, SVGUtils.doubleString(1 + edgeStrokeWidth));
			closedRect.setAttribute(SVG_FILL_ATTRIBUTE, "url(#seesawClosed)");
			closedSide.appendChild(closedRect);

			// Closed side: infrared glow
			SVGRectElement closedIR = (SVGRectElement) createElement(SVG_RECT_TAG);
			closedIR.setAttribute(SVG_X_ATTRIBUTE, SVGUtils.doubleString(-0.5 + edgeStrokeWidth / 2));
			closedIR.setAttribute(SVG_Y_ATTRIBUTE, SVGUtils.doubleString(-0.75 - edgeStrokeWidth / 2));
			closedIR.setAttribute(SVG_WIDTH_ATTRIBUTE, SVGUtils.doubleString(1 - edgeStrokeWidth));
			closedIR.setAttribute(SVG_HEIGHT_ATTRIBUTE, SVGUtils.doubleString(0.25));
			closedIR.setAttribute(SVG_FILL_ATTRIBUTE, "url(#seesawInfrared)");
			closedSide.appendChild(closedIR);

			// Tooltip
			tooltip = (SVGDescElement) createElement(SVG_DESC_TAG);
			seesawGroup.appendChild(tooltip);

			update();
		}

		public SVGGElement get() {
			return seesawGroup;
		}

		public void update() {
			if (!tile.isSeesaw())
				return;

			// State
			currentSide = tile.isSeesawOpen() ? openSide : closedSide;
			otherSide = tile.isSeesawOpen() ? closedSide : openSide;
			if (otherSide.getParentNode() != null) {
				seesawGroup.removeChild(otherSide);
			}
			seesawGroup.appendChild(currentSide);

			// Orientation
			Tile barcodeTile = maze.getBarcodeTile(tile.getSeesawBarcode());
			if (barcodeTile != null) {
				// Get rotation angle
				Orientation orientation = tile.orientationTo(barcodeTile);
				float angle = CoordUtils.toMapCoordinates(orientation.getAngle());
				// Set rotation on current side
				SVGTransform translate = new SVGOMTransform();
				translate.setTranslate(0.5f, 0.5f);
				SVGTransform rotate = new SVGOMTransform();
				rotate.setRotate(angle, 0, 0);
				SVGTransformList list = ((SVGTransformable) currentSide).getTransform().getBaseVal();
				list.clear();
				list.appendItem(translate);
				list.appendItem(rotate);
			}

			// Tooltip
			tooltip.setTextContent(getTooltipText(tile));
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
			line.setAttribute(SVG_STROKE_WIDTH_ATTRIBUTE, SVGUtils.doubleString(edgeStrokeWidth));
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
					dashed ? SVGUtils.doubleString(edgeDashSize * edgeStrokeWidth) + "" : SVG_NONE_VALUE);
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
