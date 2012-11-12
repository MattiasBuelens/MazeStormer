package mazestormer.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mazestormer.controller.IScanController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.EditClearIcon;
import com.javarichclient.icon.tango.actions.SystemSearchIcon;

public class ScanPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IScanController controller;

	private SpinnerNumberModel rangeModel;
	private SpinnerNumberModel countModel;
	private final Action scanAction = new ScanAction();
	private final Action clearAction = new ClearAction();

	public ScanPanel(IScanController controller) {
		this.controller = controller;

		setBorder(null);
		setLayout(new MigLayout("", "[grow 75][grow 25][][fill]",
				"[grow,fill][grow,fill]"));

		createRange();
		createAngleIncrement();
		createButtons();

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

	public void clear() {
		controller.clear();
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

	private void createButtons() {
		JPopupMenu menuScan = new JPopupMenu();

		JMenuItem menuClear = new JMenuItem();
		menuClear.setAction(clearAction);
		menuClear.setIcon(new EditClearIcon(16, 16));
		menuScan.add(menuClear);

		SplitButton btnScan = new SplitButton();
		btnScan.setAlwaysDropDown(false);
		btnScan.setAction(scanAction);
		btnScan.setText("");
		btnScan.setIcon(new SystemSearchIcon(32, 32));

		btnScan.setPopupMenu(menuScan);
		addPopup(this, menuScan);

		add(btnScan, "cell 3 0 1 2");
	}

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

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

	private class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ClearAction() {
			putValue(NAME, "Clear");
			putValue(SHORT_DESCRIPTION, "Clear the detected ranges");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

}
