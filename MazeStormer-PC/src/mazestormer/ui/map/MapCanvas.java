package mazestormer.ui.map;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.Interactor;

public class MapCanvas extends JSVGCanvas {

	private static final long serialVersionUID = 1L;

	private boolean enablePanInteractor;
	private boolean enableZoomInteractor;
	private PanInteractor panInteractor = new PanInteractor();
	private ZoomListener zoomListener = new ZoomListener();

	private double zoomScale = 1f;

	public MapCanvas() {
		setupInteractors();
	}

	public void centerOn(double x, double y, double angle) {
		AffineTransform at = new AffineTransform();

		// Transform point to view coordinates
		Point2D point = new Point2D.Double(x, y);
		getViewingTransform().transform(point, point);

		// Get center of canvas
		Dimension size = getSize();
		Point2D center = new Point2D.Double(size.getWidth() / 2, size.getHeight() / 2);

//		// Place point at center
//		at.translate(center.getX(), center.getY());
//		at.translate(-point.getX(), -point.getY());
//
//		// Rotate around point
//		at.rotate(Math.toRadians(-angle), point.getX(), point.getY());
		
		// Place point at center and rotate around it
		at.translate(center.getX(), center.getY());
		at.rotate(Math.toRadians(-angle));
		at.translate(-point.getX(), -point.getY());

		// Apply zoom
		at.concatenate(zoomOn(point, getZoomScale()));

		setRenderingTransform(at);
	}

	public double getZoomScale() {
		return zoomScale;
	}

	private AffineTransform zoomOn(Point2D center, double scale) {
		double dx = -center.getX() * (scale - 1.0);
		double dy = -center.getY() * (scale - 1.0);

		AffineTransform at = new AffineTransform();
		at.translate(dx, dy);
		at.scale(scale, scale);
		return at;
	}

	@Override
	public void resetRenderingTransform() {
		zoomScale = 1f;
		super.resetRenderingTransform();
	}

	private void setupInteractors() {
		// Disable default interactors
		super.setEnableImageZoomInteractor(false);
		super.setEnablePanInteractor(false);
		super.setEnableRotateInteractor(false);
		super.setEnableResetTransformInteractor(false);

		// Enable custom interactors
		setEnablePanInteractor(true);
		setEnableZoomInteractor(true);
	}

	@Override
	public boolean getEnablePanInteractor() {
		return enablePanInteractor;
	}

	@Override
	public void setEnablePanInteractor(boolean b) {
		enablePanInteractor = b;
		if (getEnablePanInteractor()) {
			addInteractor(panInteractor);
		} else {
			removeInteractor(panInteractor);
		}
	}

	@Override
	public boolean getEnableZoomInteractor() {
		return enableZoomInteractor;
	}

	@Override
	public void setEnableZoomInteractor(boolean b) {
		enableZoomInteractor = b;
		if (getEnableZoomInteractor()) {
			addMouseWheelListener(zoomListener);
		} else {
			removeMouseWheelListener(zoomListener);
		}
	}

	@SuppressWarnings("unchecked")
	private void addInteractor(Interactor interactor) {
		getInteractors().add(interactor);
	}

	private void removeInteractor(Interactor interactor) {
		getInteractors().remove(interactor);
	}

	private class PanInteractor extends AbstractPanInteractor {
		@Override
		public boolean startInteraction(InputEvent ie) {
			int mods = ie.getModifiers();
			boolean res = ie.getID() == MouseEvent.MOUSE_PRESSED && (mods & InputEvent.BUTTON1_MASK) != 0;
			return res;
		}
	}

	private class ZoomListener implements MouseWheelListener {

		private static final float defaultScaleFactor = 1.25f;
		private static final int scaleLimit = 1 << 10;
		private float scaleFactor;

		public ZoomListener(float scaleFactor) {
			this.scaleFactor = scaleFactor;
		}

		public ZoomListener() {
			this(defaultScaleFactor);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent ev) {
			double scale = (ev.getWheelRotation() < 0) ? scaleFactor : 1.0f / scaleFactor;

			// Zoom on mouse pointer
			AffineTransform at = zoomOn(ev.getPoint(), scale);

			// Concatenate with rendering transform
			AffineTransform rat = getRenderingTransform();
			at.concatenate(rat);

			if (at.getScaleX() <= scaleLimit && 1 <= at.getScaleX() * scaleLimit) {
				// Store zoom scale
				zoomScale *= scale;
				// Set as rendering transform
				setRenderingTransform(at);
			}
		}
	}

}
