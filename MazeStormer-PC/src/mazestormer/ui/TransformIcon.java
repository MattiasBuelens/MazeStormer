package mazestormer.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

public class TransformIcon implements Icon {

	private final Icon icon;
	private AffineTransform transform = new AffineTransform();

	private double renderWidth = 48d;
	private double renderHeight = 48d;

	public TransformIcon(Icon icon) {
		this.icon = icon;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public void reflectHorizontal() {
		AffineTransform at = getTransform();
		at.scale(1, -1);
		at.translate(0, at.getScaleY() * (getIconHeight() / 2d + renderHeight));
	}

	public void reflectVertical() {
		AffineTransform at = getTransform();
		at.scale(-1, 1);
		at.translate(at.getScaleX() * (getIconWidth() / 2d + renderWidth), 0);
	}

	public double getRenderWidth() {
		return renderWidth;
	}

	public void setRenderWidth(double renderWidth) {
		this.renderWidth = renderWidth;
	}

	public double getRenderHeight() {
		return renderHeight;
	}

	public void setRenderHeight(double renderHeight) {
		this.renderHeight = renderHeight;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		// Transform and paint
		g2d.transform(getTransform());
		icon.paintIcon(c, g2d, x, y);
	}

	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}

}
