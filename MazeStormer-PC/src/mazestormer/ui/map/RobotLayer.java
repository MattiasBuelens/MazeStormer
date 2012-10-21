package mazestormer.ui.map;

import java.net.URL;

import org.w3c.dom.svg.SVGDocument;

public class RobotLayer extends TransformLayer {

	private static final double defaultHeight = 30d;

	public RobotLayer(String name, double height) {
		super(name, getRobot().getDocumentElement());
		setRotationCenter(0.5f, 0.5f);
		setHeight(height);
	}

	public RobotLayer(String name) {
		this(name, defaultHeight);
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
