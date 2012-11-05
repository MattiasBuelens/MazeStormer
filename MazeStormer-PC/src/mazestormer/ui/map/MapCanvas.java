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

	private boolean isPanInteractorEnabled;
	private boolean isZoomInteractorEnabled;
	private PanInteractor panInteractor = new PanInteractor();
	private ZoomListener zoomListener = new ZoomListener();

	private double zoomScale = 1d;
	private static final int zoomScaleLimit = 1 << 10;

	public MapCanvas() {
		setupInteractors();
	}

	public double getZoomScale() {
		return zoomScale;
	}

	public void zoom(double zoomFactor) {
		zoomOn(getCenter(), zoomFactor);
	}

	public void resetZoom() {
		zoom(1d / getZoomScale());
		zoomScale = 1d;
	}

	@Override
	public void resetRenderingTransform() {
		zoomScale = 1f;
		super.resetRenderingTransform();
	}

	private Point2D getCenter() {
		Dimension size = getSize();
		return new Point2D.Double(size.getWidth() / 2d, size.getHeight() / 2d);
	}

	public void centerOn(double x, double y, double angle) {
		AffineTransform at = new AffineTransform();

		// Transform point to view coordinates
		Point2D point = new Point2D.Double(x, y);
		getViewingTransform().transform(point, point);

		// Get center of canvas
		Point2D center = getCenter();

		// Place point at center
		// at.translate(center.getX(), center.getY());
		// at.translate(-point.getX(), -point.getY());
		//
		// Rotate around point
		// at.rotate(Math.toRadians(-angle), point.getX(), point.getY());

		// Place point at center and rotate around it
		at.translate(center.getX(), center.getY());
		at.rotate(Math.toRadians(-angle));
		at.translate(-point.getX(), -point.getY());

		// Apply zoom
		at.concatenate(getZoomTransform(point, getZoomScale()));

		setRenderingTransform(at);
	}

	private void zoomOn(Point2D center, double zoomFactor) {
		double newZoomScale = zoomScale * zoomFactor;
		if (newZoomScale <= zoomScaleLimit
				&& 1 <= newZoomScale * zoomScaleLimit) {
			// Store new zoom scale
			zoomScale = newZoomScale;
			// Set new rendering transform
			AffineTransform at = getZoomTransform(center, zoomFactor);
			at.concatenate(getRenderingTransform());
			setRenderingTransform(at);
		}
	}

	private AffineTransform getZoomTransform(Point2D center, double scale) {
		double dx = -center.getX() * (scale - 1.0);
		double dy = -center.getY() * (scale - 1.0);

		AffineTransform at = new AffineTransform();
		at.translate(dx, dy);
		at.scale(scale, scale);
		return at;
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
		return isPanInteractorEnabled;
	}

	@Override
	public void setEnablePanInteractor(boolean b) {
		if (isPanInteractorEnabled != b) {
			boolean oldValue = isPanInteractorEnabled;
			isPanInteractorEnabled = b;
			if (isPanInteractorEnabled) {
				addInteractor(panInteractor);
			} else {
				removeInteractor(panInteractor);
			}
			pcs.firePropertyChange("enablePanInteractor", oldValue, b);
		}
	}

	@Override
	public boolean getEnableZoomInteractor() {
		return isZoomInteractorEnabled;
	}

	@Override
	public void setEnableZoomInteractor(boolean b) {
		if (isZoomInteractorEnabled != b) {
			boolean oldValue = isZoomInteractorEnabled;
			isZoomInteractorEnabled = b;
			if (isZoomInteractorEnabled) {
				addMouseWheelListener(zoomListener);
			} else {
				removeMouseWheelListener(zoomListener);
			}
			pcs.firePropertyChange("enableZoomInteractor", oldValue, b);
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
			boolean res = ie.getID() == MouseEvent.MOUSE_PRESSED
					&& (mods & InputEvent.BUTTON1_MASK) != 0;
			return res;
		}
	}

	private class ZoomListener implements MouseWheelListener {

		private static final double defaultFactor = 1.25d;
		private double factor;

		public ZoomListener(double factor) {
			this.factor = factor;
		}

		public ZoomListener() {
			this(defaultFactor);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent ev) {
			double zoomFactor = (ev.getWheelRotation() < 0) ? factor
					: 1d / factor;
			zoomOn(ev.getPoint(), zoomFactor);
		}
	}

}
