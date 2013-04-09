package mazestormer.ui.map;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLinearGradientElement;

public final class SVGUtils {

	private SVGUtils() {
	}

	/**
	 * Creates a new SVG document.
	 */
	public static final SVGDocument createSVGDocument() {
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		return (SVGDocument) impl.createDocument(svgNS, SVGConstants.SVG_SVG_TAG, null);
	}

	/**
	 * Loads an SVG file into a SVG document.
	 * 
	 * @param uri
	 *            The URI of the SVG file.
	 */
	public static final SVGDocument loadSVGDocument(String uri) {
		UserAgent ua = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(ua);
		try {
			return (SVGDocument) loader.loadDocument(uri);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Loads an SVG file into a SVG document.
	 * 
	 * @param uri
	 *            The URI of the SVG file.
	 */
	public static final SVGDocument loadSVGDocument(URI uri) {
		return loadSVGDocument(uri.toString());
	}

	/**
	 * Loads an SVG file into a SVG document.
	 * 
	 * @param url
	 *            The URL of the SVG file.
	 */
	public static final SVGDocument loadSVGDocument(URL url) {
		return loadSVGDocument(url.toExternalForm());
	}

	public static final void printSVG(SVGDocument document, OutputStream out) {
		try {
			Transcoder t = new SVGTranscoder();
			TranscoderInput input = new TranscoderInput(document);
			TranscoderOutput output = new TranscoderOutput(new PrintWriter(out));
			t.transcode(input, output);
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
	}

	public static void removeChildNodes(Node node) {
		Node firstChild = node.getFirstChild();
		while (firstChild != null) {
			node.removeChild(firstChild);
			firstChild = node.getFirstChild();
		}
	}

	public static String doubleString(double number) {
		return decimalFormat.format(number);
	}

	private static DecimalFormatSymbols dsf = new DecimalFormatSymbols(Locale.US);
	private static DecimalFormat decimalFormat = new DecimalFormat("#.####", dsf);

	public static String doublePercentage(double fraction) {
		return percentageFormat.format(fraction);
	}

	private static DecimalFormat percentageFormat = new DecimalFormat("#%", dsf);

	public static class LinearGradientBuilder implements SVGConstants {

		private final Document document;
		private final SVGLinearGradientElement gradient;

		public LinearGradientBuilder(Document document, String id) {
			this.document = document;

			this.gradient = (SVGLinearGradientElement) createElement(SVG_LINEAR_GRADIENT_TAG);
			gradient.setAttribute(SVG_ID_ATTRIBUTE, id);
		}

		public LinearGradientBuilder start(double x1, double y1) {
			gradient.setAttributeNS(null, SVG_X1_ATTRIBUTE, doubleString(x1));
			gradient.setAttributeNS(null, SVG_Y1_ATTRIBUTE, doubleString(y1));
			return this;
		}

		public LinearGradientBuilder stop(double x2, double y2) {
			gradient.setAttributeNS(null, SVG_X2_ATTRIBUTE, doubleString(x2));
			gradient.setAttributeNS(null, SVG_Y2_ATTRIBUTE, doubleString(y2));
			return this;
		}

		public LinearGradientBuilder horizontal() {
			start(0, 0);
			stop(1, 0);
			return this;
		}

		public LinearGradientBuilder vertical() {
			start(0, 0);
			stop(0, 1);
			return this;
		}

		public LinearGradientBuilder add(float offset, String svgColor, float opacity) {
			Element stop = createElement(SVG_STOP_TAG);
			stop.setAttribute(SVG_OFFSET_ATTRIBUTE, doublePercentage(offset));
			stop.setAttribute(SVG_STOP_COLOR_ATTRIBUTE, svgColor);
			stop.setAttribute(SVG_STOP_OPACITY_ATTRIBUTE, doubleString(opacity));
			gradient.appendChild(stop);
			return this;
		}

		public LinearGradientBuilder add(float offset, String svgColor) {
			add(offset, svgColor, 1);
			return this;
		}

		public SVGLinearGradientElement build() {
			return gradient;
		}

		private Element createElement(String tagName) {
			return document.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, tagName);
		}

	}

}
