package mazestormer.ui.map;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.apache.batik.swing.gvt.Interactor;

public class MapCanvas extends JSVGCanvas {

	private static final long serialVersionUID = 1L;

	public MapCanvas() {
		setupInteractors();
	}

	private void setupInteractors() {
		// Disable default interactors
		setEnableImageZoomInteractor(false);
		setEnablePanInteractor(false);
		setEnableRotateInteractor(false);
		setEnableResetTransformInteractor(false);

		// Add interactors
		addInteractor(new PanInteractor());
		addMouseWheelListener(new ZoomListener());
	}

	@SuppressWarnings("unchecked")
	private void addInteractor(Interactor interactor) {
		getInteractors().add(interactor);
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
			double scale = (ev.getWheelRotation() < 0) ? scaleFactor
					: 1.0f / scaleFactor;

			AffineTransform rat = getRenderingTransform();
			double dx = -ev.getX() * (scale - 1.0);
			double dy = -ev.getY() * (scale - 1.0);

			AffineTransform at = new AffineTransform();
			at.translate(dx, dy);
			at.scale(scale, scale);
			at.concatenate(rat);

			if (at.getScaleX() <= scaleLimit
					&& 1 <= at.getScaleX() * scaleLimit) {
				setRenderingTransform(at);
			}
		}
	}

}
