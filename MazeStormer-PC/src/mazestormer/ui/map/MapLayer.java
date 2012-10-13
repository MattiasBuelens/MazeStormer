package mazestormer.ui.map;

import java.util.Comparator;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Element;

public abstract class MapLayer {

	public MapLayer() {
		setVisible(true);
	}

	private Element element;
	private boolean isVisible;

	protected Element getElement() {
		return element;
	}

	private void setElement(Element element) {
		this.element = element;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = visible;
		update();
	}

	protected void update() {
		Element element = getElement();
		if (element != null) {
			element.setAttributeNS(null, "display", isVisible() ? "inline"
					: "none");
		}
	}

	public Element build(AbstractDocument document) {
		setElement(create(document));
		update();
		return getElement();
	}

	protected abstract Element create(AbstractDocument document);

	public abstract int getZIndex();

	public static class ZIndexComparator implements Comparator<MapLayer> {

		@Override
		public int compare(MapLayer left, MapLayer right) {
			if (left == null) {
				return (right == null) ? 0 : -1;
			} else if (right == null) {
				return 1;
			} else {
				return left.getZIndex() - right.getZIndex();
			}
		}

	}

}
