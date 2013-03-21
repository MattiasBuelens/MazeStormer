package mazestormer.ui.map;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetectEvent;
import mazestormer.util.CoordUtils;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGCircleElement;

import com.google.common.eventbus.Subscribe;

public class RangesLayer extends MapLayer {

	private static final float pointRadius = 1f;
	private static final Color pointStartColor = Color.GREEN;
	private static final Color pointEndColor = Color.RED;
	private static final int pointColorThreshold = 5;

	private List<Set<Element>> points = new ArrayList<Set<Element>>();

	public RangesLayer(String name) {
		super(name);
	}

	@Override
	protected Element create() {
		return createElement(SVG_G_TAG);
	}

	@Subscribe
	public void rangeFeatureDetected(RangeFeatureDetectEvent e) {
		addRangeFeature(e.getFeature());
	}

	private void addRangeFeature(RangeFeature feature) {
		final Set<Element> newPoints = new HashSet<Element>();

		// Get robot pose at time of reading
		Pose robotPose = feature.getPose();

		// Get points
		for (RangeReading reading : feature.getRangeReadings()) {
			if (reading.invalidReading() || reading.getRange() < 0f)
				continue;
			// Get reading point in robot coordinates
			Point2D robotPoint = robotPose.pointAt(reading.getRange(),
					reading.getAngle() + robotPose.getHeading());
			// Convert to map coordinates
			Point2D mapPoint = CoordUtils.toMapCoordinates(robotPoint);
			// Create point element and store
			Element pointElement = createPoint(mapPoint.getX(), mapPoint.getY());
			newPoints.add(pointElement);
		}

		// Store newly added points
		points.add(newPoints);

		// Append points
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				Element container = getElement();
				for (Element newPoint : newPoints) {
					container.appendChild(newPoint);
				}
				updatePoints();
			}
		});
	}

	private Element createPoint(double x, double y) {
		SVGCircleElement circle = (SVGCircleElement) createElement(SVG_CIRCLE_TAG);
		circle.setAttribute(SVG_CX_ATTRIBUTE, x + "");
		circle.setAttribute(SVG_CY_ATTRIBUTE, y + "");
		circle.setAttribute(SVG_R_ATTRIBUTE, pointRadius + "");
		return circle;
	}

	private void updatePoints() {
		int length = points.size();
		// Loop backwards
		ListIterator<Set<Element>> it = points.listIterator(length);
		while (it.hasPrevious()) {
			int index = it.previousIndex();
			Set<Element> set = it.previous();
			// Get color position
			int colorIndex = Math.min(pointColorThreshold,
					Math.max(0, length - index));
			float colorPosition = (float) colorIndex
					/ (float) pointColorThreshold;
			// Set color
			Color color = interpolateColor(pointStartColor, pointEndColor,
					colorPosition);
			setFillColor(set, color);
		}
	}

	private void setFillColor(Iterable<Element> points, Color color) {
		String colorString = String.format("rgb(%d,%d,%d)", color.getRed(),
				color.getGreen(), color.getBlue());
		for (Element element : points) {
			element.setAttribute(SVG_FILL_ATTRIBUTE, colorString);
		}
	}

	private Color interpolateColor(Color start, Color end, float position) {
		int red = interpolateComponent(start.getRed(), end.getRed(), position);
		int green = interpolateComponent(start.getGreen(), end.getGreen(),
				position);
		int blue = interpolateComponent(start.getBlue(), end.getBlue(),
				position);
		int alpha = interpolateComponent(start.getAlpha(), end.getAlpha(),
				position);
		return new Color(red, green, blue, alpha);
	}

	private int interpolateComponent(int start, int end, float position) {
		return start + (int) ((end - start) * position);
	}

	public void clear() {
		// Remove all child nodes
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				SVGUtils.removeChildNodes(getElement());
			}
		});
	}

	@Override
	public int getZIndex() {
		return 10;
	}
}
