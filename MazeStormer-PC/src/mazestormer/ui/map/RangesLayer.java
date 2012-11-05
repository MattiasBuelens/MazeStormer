package mazestormer.ui.map;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetectEvent;
import mazestormer.util.MapUtils;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGCircleElement;

import com.google.common.eventbus.Subscribe;

public class RangesLayer extends MapLayer {

	private static final float pointRadius = 1f;
	private static final String pointColor = CSS_RED_VALUE;

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
		final List<Point2D> points = new ArrayList<Point2D>();

		// Get robot pose at time of reading
		Pose robotPose = feature.getPose();

		// Get points
		for (RangeReading reading : feature.getRangeReadings()) {
			if (reading.invalidReading() || reading.getRange() < 0f)
				continue;
			// Get reading point in robot coordinates
			Point2D robotPoint = robotPose.pointAt(reading.getRange(),
					reading.getAngle() + robotPose.getHeading());
			// Convert to map coordinates and store
			Point2D mapPoint = MapUtils.toMapCoordinates(robotPoint);
			points.add(mapPoint);
		}

		// Append points
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				Element container = getElement();
				for (Point2D point : points) {
					Element element = createPoint(point.getX(), point.getY());
					container.appendChild(element);
				}
			}
		});
	}

	private Element createPoint(double x, double y) {
		SVGCircleElement circle = (SVGCircleElement) createElement(SVG_CIRCLE_TAG);
		circle.setAttribute(SVG_CX_ATTRIBUTE, x + "");
		circle.setAttribute(SVG_CY_ATTRIBUTE, y + "");
		circle.setAttribute(SVG_R_ATTRIBUTE, pointRadius + "");
		circle.setAttribute(SVG_FILL_ATTRIBUTE, pointColor);
		return circle;
	}

	@Override
	public int getZIndex() {
		return 10;
	}

}
