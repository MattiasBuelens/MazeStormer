package mazestormer.ui.map;

import java.awt.geom.Point2D;
import java.net.URL;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RobotLayer implements MapLayer {

	private java.awt.geom.Point2D position;
	private double angle;
	private Node robot;

	public RobotLayer(double x, double y, double angle) {
		this.position = new Point2D.Double(x, y);
		this.angle = angle;
	}

	public RobotLayer() {
		this(0, 0, 0);
	}

	public java.awt.geom.Point2D getPosition() {
		return new Point2D.Double(position.getX(), position.getY());
	}

	public double getAngle() {
		return angle;
	}

	@Override
	public Node build(Document document) {
		robot = getRobot();
		return robot;
	}

	@Override
	public void render(SVGGraphics2D engine) {

	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	private static final URL robotUrl = RobotLayer.class
			.getResource("/res/images/robot.svg");

	private static Element robotElement;

	public static Node getRobot() {
		if (robotElement == null) {
			robotElement = SVGUtils.loadSVGDocument(robotUrl);
		}
		return robotElement.cloneNode(true);
	}

}
