package mazestormer.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mazestormer.controller.IScanController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.SystemSearchIcon;

public class ScanPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IScanController controller;

	private JPanel container;

	private SpinnerNumberModel rangeModel;
	private SpinnerNumberModel countModel;
	private final Action scanAction = new ScanAction();

	public ScanPanel(IScanController controller) {
		this.controller = controller;

		setBorder(null);
		setLayout(new BorderLayout(0, 0));

		container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow 25][right]",
				"[][][]"));
		add(container, BorderLayout.NORTH);

		createRange();
		createAngleIncrement();

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
		container.add(lblScanRange, "cell 0 0,grow");

		JSpinner spinRange = new JSpinner();
		rangeModel = new SpinnerNumberModel(180, 0, 180, 1);
		spinRange.setModel(rangeModel);
		container.add(spinRange, "cell 1 0,grow");

		JLabel lblUnit = new JLabel("degrees");
		container.add(lblUnit, "cell 2 0,grow");
	}

	private void createAngleIncrement() {
		JLabel lblScanCount = new JLabel("Scan count");
		container.add(lblScanCount, "cell 0 1,grow");

		JSpinner spinCount = new JSpinner();
		countModel = new SpinnerNumberModel(5, 1, null, 1);
		spinCount.setModel(countModel);
		container.add(spinCount, "cell 1 1,grow");

		JLabel lblUnit = new JLabel("scans");
		container.add(lblUnit, "cell 2 1,grow");

		JButton btnScan = new JButton();
		btnScan.setAction(scanAction);
		btnScan.setText("");
		btnScan.setIcon(new SystemSearchIcon(32, 32));
		container.add(btnScan, "cell 0 2 3 1,alignx center");
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
