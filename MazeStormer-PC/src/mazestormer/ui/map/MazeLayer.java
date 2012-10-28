package mazestormer.ui.map;

import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLineElement;
import org.w3c.dom.svg.SVGRectElement;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MazeLayer extends MapLayer {

	private final Maze maze;

	private Table<Long, Long, TileElement> tiles = HashBasedTable.create();

	public MazeLayer(String name, Maze maze) {
		super(name);
		this.maze = maze;
	}

	@Override
	protected Element create() {
		SVGGElement group = (SVGGElement) createElementNS(null, SVG_G_TAG);
		return group;
	}

	@Override
	public int getZIndex() {
		return 0;
	}

	private class TileElement {

		private final SVGGElement group;
		private final SVGRectElement rect;

		public TileElement() {
			group = (SVGGElement) createElementNS(null, SVG_G_TAG);
			rect = (SVGRectElement) createElementNS(null, SVG_RECT_TAG);
			rect.setAttribute(SVG_FILL_ATTRIBUTE, CSS_SANDYBROWN_VALUE);
		}

	}

	private class EdgeElement {

		private final SVGLineElement line;
		private Orientation orientation;

		public EdgeElement(Orientation orientation) {
			line = (SVGLineElement) createElementNS(null, SVG_LINE_TAG);
			line.setAttribute(SVG_STROKE_ATTRIBUTE, CSS_PERU_VALUE);
			line.setAttribute(SVG_STROKE_WIDTH_ATTRIBUTE, 5 + "");

			setOrientation(orientation);
		}

		public Orientation getOrientation() {
			return orientation;
		}

		public void setOrientation(Orientation orientation) {
			this.orientation = orientation;
			update();
		}

		private void update() {
			int startX = 0, startY = 0, endX = 0, endY = 0;
			switch (orientation) {
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
