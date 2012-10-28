package mazestormer.ui.map;

import java.awt.geom.Point2D;

import lejos.geom.Rectangle;

import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

public abstract class TransformLayer extends MapLayer {

	private final Element transformElement;

	private Point2D scale = new Point2D.Float(1, 1);
	private Point2D position = new Point2D.Float();
	private float rotationAngle = 0;
	private Point2D rotationCenter = new Point2D.Float();

	public TransformLayer(String name, Element transformElement) {
		super(name);
		this.transformElement = transformElement;
	}

	/**
	 * Get the element being transformed by this layer.
	 */
	public Element getTransformElement() {
		return transformElement;
	}

	/**
	 * Get the view box of an element.
	 * 
	 * @param element
	 *            The element.
	 * @return The view box rectangle, or null if the element has no valid view
	 *         box.
	 */
	private static Rectangle getViewBox(Element element) {
		String attr = element.getAttributeNS(null, SVG_VIEW_BOX_ATTRIBUTE);
		try {
			float[] viewBox = ViewBox.parseViewBoxAttribute(element, attr, null);
			return new Rectangle(viewBox[0], viewBox[1], viewBox[2], viewBox[3]);
		} catch (BridgeException e) {
			return null;
		}
	}

	/**
	 * Get the X-coordinate of this layer's position.
	 */
	public float getX() {
		return (float) position.getX();
	}

	/**
	 * Get the Y-coordinate of this layer's position.
	 */
	public float getY() {
		return (float) position.getY();
	}

	/**
	 * Get this layer's position.
	 */
	public Point2D getPosition() {
		return new Point2D.Float(getX(), getY());
	}

	/**
	 * Set this layer's position.
	 * 
	 * @param x
	 *            The new X-coordinate.
	 * @param y
	 *            The new Y-coordinate.
	 */
	public void setPosition(float x, float y) {
		this.position.setLocation(x, y);
		update();
	}

	/**
	 * Set this layer's position.
	 * 
	 * @param position
	 *            The new position.
	 */
	public void setPosition(Point2D position) {
		setPosition((float) position.getX(), (float) position.getY());
	}

	/**
	 * Get the rotation angle of this layer.
	 */
	public float getRotationAngle() {
		return rotationAngle;
	}

	/**
	 * Set the rotation angle of this layer.
	 * 
	 * @param angle
	 *            The new angle.
	 */
	public void setRotationAngle(float angle) {
		this.rotationAngle = angle;
		update();
	}

	/**
	 * Get the X-coordinate of this layer's rotation center.
	 */
	public float getRotationCenterX() {
		return (float) rotationCenter.getX();
	}

	/**
	 * Get the Y-coordinate of this layer's rotation center.
	 */
	public float getRotationCenterY() {
		return (float) rotationCenter.getY();
	}

	/**
	 * Get this layer's rotation center around which the transformed element is
	 * rotated.
	 * 
	 * <p>
	 * The rotation center's coordinates are expressed in relative units ranging
	 * from {@code 0.0f} to {@code 1.0f}. For example, {@code (0.5f, 0.5f)}
	 * rotates around the center of the element.
	 */
	public Point2D getRotationCenter() {
		return new Point2D.Float(getRotationCenterX(), getRotationCenterY());
	}

	/**
	 * Set this layer's rotation center.
	 * 
	 * @param x
	 *            The X-coordinate of the new rotation center.
	 * @param y
	 *            The Y-coordinate of the new rotation center.
	 */
	public void setRotationCenter(float x, float y) {
		rotationCenter.setLocation(x, y);
		update();
	}

	/**
	 * Set this layer's rotation center.
	 * 
	 * @param center
	 *            The new rotation center.
	 */
	public void setRotationCenter(Point2D center) {
		setRotationCenter((float) center.getX(), (float) center.getY());
	}

	/**
	 * Get the scale factors as a point.
	 * 
	 * The X-component denotes the scale factor along the X-axis, whereas the
	 * Y-component denotes the Y-axis scaling.
	 * 
	 * @return The scale factors as a point.
	 */
	public Point2D getScale() {
		return scale;
	}

	/**
	 * Get the scale factor along the X-axis.
	 */
	public float getScaleX() {
		return (float) scale.getX();
	}

	/**
	 * Get the scale factor along the Y-axis.
	 */
	public float getScaleY() {
		return (float) scale.getY();
	}

	/**
	 * Set the scale factor for both axes.
	 * 
	 * @param scale
	 *            The new scale factor.
	 */
	public void setScale(float scale) {
		setScale(scale, scale);
	}

	/**
	 * Set the scale factor for the X-axis and Y-axis.
	 * 
	 * @param scaleX
	 *            The new X-axis scale factor.
	 * @param scaleY
	 *            The new Y-axis scale factor.
	 */
	public void setScale(float scaleX, float scaleY) {
		this.scale.setLocation(scaleX, scaleY);
		update();
	}

	/**
	 * Set the scale factor for the X-axis and Y-axis.
	 * 
	 * @param scale
	 *            A point denoting the new scale factors for both axes.
	 */
	public void setScale(Point2D scale) {
		setScale((float) scale.getX(), (float) scale.getY());
	}

	/**
	 * Set the width of this element.
	 * 
	 * The X-axis scaling will be adjusted to match the given width. If
	 * {@code preserveRatio} is true, the Y-axis scaling is also adjusted to
	 * preserve the original aspect ratio.
	 * 
	 * @param width
	 *            The new width.
	 * @param preserveRatio
	 *            Whether the aspect ratio should be preserved.
	 */
	public void setWidth(double width, boolean preserveRatio) {
		double actualWidth = getViewBox(getTransformElement()).getWidth();
		float scaleX = (float) (width / actualWidth);
		if (preserveRatio) {
			setScale(scaleX, scaleX);
		} else {
			setScale(scaleX, getScaleY());
		}
	}

	/**
	 * Set the width of this element.
	 * 
	 * The X-axis and Y-axis scaling will be adjusted to match the given width.
	 * 
	 * @param width
	 *            The new width.
	 */
	public void setWidth(double width) {
		setWidth(width, true);
	}

	/**
	 * Set the height of this element.
	 * 
	 * The Y-axis scaling will be adjusted to match the given height. If
	 * {@code preserveRatio} is true, the Y-axis scaling is also adjusted to
	 * preserve the original aspect ratio.
	 * 
	 * @param height
	 *            The new height.
	 * @param preserveRatio
	 *            Whether the aspect ratio should be preserved.
	 */
	public void setHeight(double height, boolean preserveRatio) {
		double actualHeight = getViewBox(getTransformElement()).getHeight();
		float scaleY = (float) (height / actualHeight);
		if (preserveRatio) {
			setScale(scaleY, scaleY);
		} else {
			setScale(getScaleX(), scaleY);
		}
	}

	/**
	 * Set the height of this element.
	 * 
	 * The X-axis and Y-axis scaling will be adjusted to match the given height.
	 * 
	 * @param height
	 *            The new height.
	 */
	public void setHeight(double height) {
		setHeight(height, true);
	}

	@Override
	protected Element create() {
		SVGGElement group = (SVGGElement) createElementNS(null, SVG_G_TAG);

		Element transformElement = (Element) importNode(getTransformElement(), true);
		group.appendChild(transformElement);

		return group;
	}

	@Override
	protected void update() {
		super.update();
		updateTransformation();
	}

	private void updateTransformation() {
		final SVGGElement group = (SVGGElement) getElement();
		if (group == null)
			return;

		// Scale
		final SVGTransform scale = new SVGOMTransform();
		scale.setScale(getScaleX(), getScaleY());
		// Rotate
		final SVGTransform rotate = new SVGOMTransform();
		float rotateX = getRotationCenterX();
		float rotateY = getRotationCenterY();
		// Calculate absolute rotation center
		Rectangle viewBox = getViewBox(getTransformElement());
		if (viewBox != null) {
			rotateX *= viewBox.getWidth();
			rotateY *= viewBox.getWidth();
		}
		rotate.setRotate(getRotationAngle(), rotateX, rotateY);
		// Translate
		final SVGTransform translate = new SVGOMTransform();
		translate.setTranslate(getX(), getY());

		// Apply transformation
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				SVGTransformList list = group.getTransform().getBaseVal();
				list.clear();
				list.appendItem(translate);
				list.appendItem(scale);
				list.appendItem(rotate);
			}
		});
	}
}
