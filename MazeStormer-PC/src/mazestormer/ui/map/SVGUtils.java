package mazestormer.ui.map;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

public class SVGUtils {

	public static final SVGDocument createSVGDocument() {
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		return (SVGDocument) impl.createDocument(svgNS, "svg", null);
	}

	public static final SVGDocument loadSVGDocument(String svgUrl) {
		UserAgent ua = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(ua);
		try {
			return (SVGDocument) loader.loadDocument(svgUrl);
		} catch (IOException e) {
			return null;
		}
	}

	public static final SVGDocument loadSVGDocument(URI svgUri) {
		return loadSVGDocument(svgUri.toString());
	}

	public static final SVGDocument loadSVGDocument(URL svgUrl) {
		return loadSVGDocument(svgUrl.toExternalForm());
	}

}
