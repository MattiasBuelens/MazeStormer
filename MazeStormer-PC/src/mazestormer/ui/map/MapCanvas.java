package mazestormer.ui.map;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherListener;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public class MapCanvas extends JSVGCanvas implements
		SVGLoadEventDispatcherListener {

	private MapDocument document;

	public MapCanvas() {
		this(new MapDocument());
	}

	public MapCanvas(MapDocument document) {
		super();
		setDocumentState(ALWAYS_DYNAMIC);
		addSVGLoadEventDispatcherListener(this);
		
		this.document = document;
		setSVGDocument(document.build());
	}

	/**
	 * Indicates whether the canvas has finished its first render and is ready
	 * for modification of the DOM.
	 */
	private boolean isReadyForModification = false;

	/**
	 * Renew the document by replacing the root node with the one of the new
	 * document.
	 * 
	 * @param doc
	 *            The new document.
	 */
	public void renewDocument(final SVGDocument doc) {
		if (isReadyForModification) {
			getUpdateManager().getUpdateRunnableQueue().invokeLater(
					new Runnable() {
						@Override
						public void run() {
							// Get the root tags of the documents
							Node oldRoot = getSVGDocument().getFirstChild();
							Node newRoot = doc.getFirstChild();

							// Make the new node suitable for the old
							// document
							newRoot = getSVGDocument()
									.importNode(newRoot, true);

							// Replace the nodes
							getSVGDocument().replaceChild(newRoot, oldRoot);
						}
					});
		} else {
			setSVGDocument(doc);
		}
	}

	@Override
	public void svgLoadEventDispatchStarted(SVGLoadEventDispatcherEvent e) {
	}

	@Override
	public void svgLoadEventDispatchCompleted(SVGLoadEventDispatcherEvent e) {
		isReadyForModification = true;
	}

	@Override
	public void svgLoadEventDispatchCancelled(SVGLoadEventDispatcherEvent e) {
	}

	@Override
	public void svgLoadEventDispatchFailed(SVGLoadEventDispatcherEvent e) {
	}
}
