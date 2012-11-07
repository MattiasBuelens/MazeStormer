package mazestormer.ui.map;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class RobotLayer extends TransformLayer {

	private static final double defaultHeight = 15d;

	private Element element;

	public RobotLayer(String name, double height) {
		super(name);
		setHeight(height);
		setRotationCenter(0, 0);
	}

	public RobotLayer(String name) {
		this(name, defaultHeight);
	}

	@Override
	public Element getTransformElement() {
		if (getDocument() != null) {
			// Import to document
			if (element == null) {
				element = (Element) getDocument().importNode(
						getRobot().getDocumentElement(), true);
			}
			// Return imported element
			return element;
		} else {
			// Return foreign element for measurements
			return getRobot().getDocumentElement();
		}
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	private static final URL robotUrl = RobotLayer.class
			.getResource("/res/images/robot.svg");

	private static SVGDocument robot;

	public static SVGDocument getRobot() {
		if (robot == null) {
			robot = SVGUtils.loadSVGDocument(robotUrl);
		}
		return robot;
	}

}
