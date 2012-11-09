package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mazestormer.controller.IScanController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.SystemSearchIcon;

public class ScanPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IScanController controller;

	private SpinnerNumberModel rangeModel;
	private SpinnerNumberModel countModel;
	private final Action scanAction = new ScanAction();

	public ScanPanel(IScanController controller) {
		this.controller = controller;

		setBorder(null);
		setLayout(new MigLayout("", "[grow 75][grow 25][][fill]",
				"[grow,fill][grow,fill]"));

		createRange();
		createAngleIncrement();

		JButton btnScan = new JButton();
		btnScan.setAction(scanAction);
		btnScan.setText("");
		btnScan.setIcon(new SystemSearchIcon(32, 32));
		add(btnScan, "cell 3 0 1 2,alignx center");

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	public void scan() {
		int range = (int) rangeModel.getValue();
		int count = (int) countModel.getValue();
		controller.scan(range, count);
	}

	private void createRange() {
		JLabel lblScanRange = new JLabel("Scan range");
		add(lblScanRange, "cell 0 0,grow");

		JSpinner spinRange = new JSpinner();
		rangeModel = new SpinnerNumberModel(180, 0, 180, 1);
		spinRange.setModel(rangeModel);
		add(spinRange, "cell 1 0,grow");

		JLabel lblUnit = new JLabel("\u00B0");
		add(lblUnit, "cell 2 0,grow");
	}

	private void createAngleIncrement() {
		JLabel lblScanCount = new JLabel("Scan count");
		add(lblScanCount, "cell 0 1,grow");

		JSpinner spinCount = new JSpinner();
		countModel = new SpinnerNumberModel(5, 1, null, 1);
		spinCount.setModel(countModel);
		add(spinCount, "cell 1 1,grow");

		JLabel lblUnit = new JLabel("scans");
		add(lblUnit, "cell 2 1,grow");
	}

	private class ScanAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ScanAction() {
			putValue(NAME, "Scan");
			putValue(SHORT_DESCRIPTION, "Perform an ultrasonic scan");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			scan();
		}
	}

}
