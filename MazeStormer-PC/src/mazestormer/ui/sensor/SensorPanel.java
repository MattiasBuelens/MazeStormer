package mazestormer.ui.sensor;

import java.beans.Beans;

import mazestormer.controller.IStateController;
import mazestormer.ui.ViewPanel;
import net.miginfocom.swing.MigLayout;

import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.swing.svg.JSVGComponent;

public class SensorPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IStateController controller;

	protected JSVGComponent canvas;

	public SensorPanel(IStateController controller) {
		this.controller = controller;
		setLayout(new MigLayout("", "0[grow,fill]0", "0[150px,fill]0"));

		createCanvas();

		if (!Beans.isDesignTime())
			registerController();
	}

	private final void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private final void createCanvas() {
		canvas = new JSVGComponent(null, false, false);
		canvas.setBorder(null);
		canvas.setBackground(this.getBackground());
		add(canvas, "cell 0 0,grow");
	}

	public void requestDOMChange(Runnable request) {
		UpdateManager updateManager = canvas.getUpdateManager();
		if (updateManager != null) {
			updateManager.getUpdateRunnableQueue().invokeLater(request);
		}
	}

}
