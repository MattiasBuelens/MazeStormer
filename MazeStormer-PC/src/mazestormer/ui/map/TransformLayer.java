package mazestormer.ui.map;

import java.awt.geom.Point2D;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGOMGElement;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

public abstract class TransformLayer extends MapLayer {

	private final Element transformElement;

	private Point2D scale = new Point2D.Float(1, 1);
	private Point2D position = new Point2D.Float();
	private float rotationAngle = 0;
	private Point2D rotationCenter = new Point2D.Float();
	private boolean rotationCenterRelative = false;

	public TransformLayer(String name, Element transformElement) {
		super(name);
		this.transformElement = transformElement;
	}

	public Element getTransformElement() {
		return transformElement;
	}

	public Point2D getScale() {
		return scale;
	}

	public float getScaleX() {
		return (float) scale.getX();
	}

	public float getScaleY() {
		return (float) scale.getY();
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	public void setScale(float scaleX, float scaleY) {
		this.scale.setLocation(scaleX, scaleY);
		update();
	}

	public void setScale(Point2D scale) {
		setScale((float) scale.getX(), (float) scale.getY());
	}

	public float getX() {
		return (float) rotationCenter.getX();
	}

	public float getY() {
		return (float) rotationCenter.getY();
	}

	public Point2D getPosition() {
		return new Point2D.Float(getX(), getY());
	}

	public void setPosition(float x, float y) {
		this.position.setLocation(x, y);
		update();
	}

	public void setPosition(Point2D position) {
		setPosition((float) position.getX(), (float) position.getY());
	}

	public float getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(float angle) {
		this.rotationAngle = angle;
		update();
	}

	public float getRotationX() {
		return (float) position.getX();
	}

	public float getRotationY() {
		return (float) position.getY();
	}

	public Point2D getRotationCenter() {
		return new Point2D.Float(getRotationX(), getRotationY());
	}

	public void setRotationCenter(float x, float y) {
		rotationCenter.setLocation(x, y);
		update();
	}

	public void setRotationCenter(Point2D center) {
		setRotationCenter((float) center.getX(), (float) center.getY());
	}

	public void setRotationCenterRelative(boolean relative) {
		rotationCenterRelative = relative;
	}

	@Override
	protected Element create(AbstractDocument document) {
		SVGGElement group = new SVGOMGElement(null, document);

		Element transformElement = (Element) document.importNode(
				getTransformElement(), true);
		group.appendChild(transformElement);

		return group;
	}

	@Override
	protected void update() {
		super.update();
		updateTransformation();
	}

	private void updateTransformation() {
		SVGGElement group = (SVGGElement) getElement();
		if (group == null)
			return;

		// Scale
		SVGTransform scale = new SVGOMTransform();
		scale.setScale(getScaleX(), getScaleY());
		// Rotate
		SVGTransform rotate = new SVGOMTransform();
		float rotateX = getRotationX();
		float rotateY = getRotationY();
		if (rotationCenterRelative) {
			Element element = getTransformElement();
			if (element instanceof SVGLocatable) {
				SVGLocatable svgElement = (SVGLocatable) getTransformElement();
				SVGRect rect = svgElement.getBBox();
				if (rect != null) {
					rotateX *= rect.getWidth();
					rotateY *= rect.getHeight();
				}
			}
		}
		rotate.setRotate(getRotationAngle(), rotateX, rotateY);
		// Translate
		SVGTransform translate = new SVGOMTransform();
		translate.setTranslate(getX(), getY());
		// Make transform list
		SVGTransformList list = group.getTransform().getBaseVal();
		list.clear();
		list.appendItem(scale);
		list.appendItem(rotate);
		list.appendItem(translate);
	}
}
