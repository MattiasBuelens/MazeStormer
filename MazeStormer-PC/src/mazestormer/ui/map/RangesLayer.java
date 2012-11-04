package mazestormer.ui.map;

import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetectEvent;
import mazestormer.util.MapUtils;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGCircleElement;

import com.google.common.eventbus.Subscribe;

public class RangesLayer extends MapLayer {

	private static final float pointRadius = 0.01f;
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
		Pose pose = MapUtils.toMapCoordinates(feature.getPose());
		Element point = createPoint(pose.getX(), pose.getY());
		getElement().appendChild(point);
	}

	private Element createPoint(float x, float y) {
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
