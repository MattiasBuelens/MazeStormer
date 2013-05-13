package mazestormer.ui.map;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.batik.dom.svg.SVGStylableElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

public class RobotLayer extends TransformLayer {

	private static final double defaultHeight = 15d;
	private static final String outlineClass = "outline";
	private static final String outlineStrokeWidth = "20";

	private Element element;
	private String color;

	public RobotLayer(String name, double height) {
		super(name);
		setHeight(height);
		setRotationCenter(0, 0);
	}

	public RobotLayer(String name) {
		this(name, defaultHeight);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		updateColor(name);
	}

	private void updateColor(String name) {
		// Get color
		String teamColor = getTeamColor(name);
		if (teamColor != null) {
			color = teamColor;
		} else if (color == null) {
			color = getRandomColor();
		}

		// Update color
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				Element element = getTransformElement();
				applyOutlines(element);
			}
		});
	}

	@Override
	public Element getTransformElement() {
		if (getDocument() != null) {
			// Import to document
			if (element == null) {
				element = (Element) getDocument().importNode(getRobot().getDocumentElement(), true);
				applyOutlines(element);
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

	/**
	 * Apply outline styling to the direct children of the given container with
	 * the {@link #outlineClass} class name.
	 */
	private void applyOutlines(Element container) {
		NodeList nodes = container.getChildNodes();
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node instanceof SVGStylableElement) {
				SVGStylableElement element = (SVGStylableElement) node;
				if (element.getClassName().getBaseVal().contains(outlineClass)) {
					element.setAttribute(SVG_STROKE_ATTRIBUTE, color);
					element.setAttribute(SVG_STROKE_WIDTH_ATTRIBUTE, outlineStrokeWidth);
				}
			}
		}

	}

	private static final URL robotUrl = RobotLayer.class.getResource("/res/images/robot.svg");
	private static SVGDocument robot;

	public static SVGDocument getRobot() {
		if (robot == null) {
			robot = SVGUtils.loadSVGDocument(robotUrl);
		}
		return robot;
	}

	/*
	 * Team colors
	 */

	private static final Map<Pattern, String> teamColors = new HashMap<Pattern, String>();
	static {
		teamColors.put(teamPattern("paars"), CSS_VIOLET_VALUE);
		teamColors.put(teamPattern("blauw"), CSS_BLUE_VALUE);
		teamColors.put(teamPattern("rood"), CSS_RED_VALUE);
		teamColors.put(teamPattern("groen"), CSS_GREEN_VALUE);
		teamColors.put(teamPattern("wit"), CSS_WHITE_VALUE);
		teamColors.put(teamPattern("geel"), CSS_YELLOW_VALUE);
		teamColors.put(teamPattern("indigo"), CSS_INDIGO_VALUE);
		teamColors.put(teamPattern("brons"), CSS_DARKORANGE_VALUE);
		teamColors.put(teamPattern("zilver"), CSS_SILVER_VALUE);
		teamColors.put(teamPattern("goud"), CSS_GOLD_VALUE);
		teamColors.put(teamPattern("platinum"), CSS_GAINSBORO_VALUE);
	}

	private static Pattern teamPattern(String teamName) {
		return Pattern.compile(teamName, Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
	}

	private static String getTeamColor(String name) {
		for (Map.Entry<Pattern, String> entry : teamColors.entrySet()) {
			if (entry.getKey().matcher(name).find()) {
				return entry.getValue();
			}
		}
		return null;
	}

	/*
	 * Random colors
	 */

	private static final String[] randomColors = new String[] { CSS_LIME_VALUE, CSS_CYAN_VALUE, CSS_HOTPINK_VALUE,
			CSS_LIGHTSLATEGREY_VALUE, CSS_BLACK_VALUE };
	private static final AtomicInteger nextRandomColor = new AtomicInteger(0);

	private static String getRandomColor() {
		return randomColors[nextRandomColor.getAndIncrement() % randomColors.length];
	}

}
