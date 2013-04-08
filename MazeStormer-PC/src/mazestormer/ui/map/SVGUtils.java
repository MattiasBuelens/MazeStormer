package mazestormer.ui.map;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;

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
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public final class SVGUtils {

	private SVGUtils() {
	}

	/**
	 * Creates a new SVG document.
	 */
	public static final SVGDocument createSVGDocument() {
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		return (SVGDocument) impl.createDocument(svgNS,
				SVGConstants.SVG_SVG_TAG, null);
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

}
