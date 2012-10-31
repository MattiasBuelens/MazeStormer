package mazestormer.ui.map;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class RobotLayer extends TransformLayer {

	private static final double defaultHeight = 20d;

	public RobotLayer(String name, double height) {
		super(name);
		setHeight(height);
		setRotationCenter(0.5f, 0.5f);
	}

	public RobotLayer(String name) {
		this(name, defaultHeight);
	}

	@Override
	public Element getTransformElement() {
		Element element = getRobot().getDocumentElement();
		if (getDocument() != null) {
			return (Element) getDocument().importNode(element, true);
		} else {
			return element;
		}
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	private static final URL robotUrl = RobotLayer.class.getResource("/res/images/robot.svg");

	private static SVGDocument robot;

	public static SVGDocument getRobot() {
		if (robot == null) {
			robot = SVGUtils.loadSVGDocument(robotUrl);
		}
		return robot;
	}

}
