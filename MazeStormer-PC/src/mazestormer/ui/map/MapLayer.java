package mazestormer.ui.map;

import java.util.Comparator;

import mazestormer.ui.map.event.MapLayerPropertyChangeEvent;
import mazestormer.util.AbstractEventSource;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGStylableElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

public abstract class MapLayer extends AbstractEventSource {

	private final String name;

	private Element element;
	private boolean isVisible;

	public MapLayer(String name) {
		this.name = name;
		setVisible(true);
	}

	public String getName() {
		return name;
	}

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
		if (this.isVisible != visible) {
			postEvent(new MapLayerPropertyChangeEvent(this, "isVisible",
					visible));
		}
		this.isVisible = visible;

		update();
	}

	protected void update() {
		Element element = getElement();
		if (element != null && element instanceof SVGStylableElement) {
			CSSStyleDeclaration css = ((SVGStylableElement) element)
					.getOverrideStyle();
			//css.setProperty("display", isVisible() ? "inline" : "none",
			//		"important");
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
