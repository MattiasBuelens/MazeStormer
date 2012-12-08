package mazestormer.ui;

import java.beans.Beans;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mazestormer.controller.IBarcodeController;
import net.miginfocom.swing.MigLayout;

public class BarcodeParameterPanel extends ViewPanel {
	private static final long serialVersionUID = 1L;
	
	private final IBarcodeController controller;
	
	private SpinnerNumberModel bwModel;
	private SpinnerNumberModel wbModel;
	private SpinnerNumberModel scanSpeedModel;

	private JPanel container;
	
	public BarcodeParameterPanel(IBarcodeController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Barcode scan parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow][fill]", "[][][]"));
		add(this.container);

		createTresholdSpinners();
		createScanSpeedSpinner();

		if (!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());

		this.scanSpeedModel.setValue((int) controller.getScanSpeed());
		this.wbModel.setValue((int) controller.getWBThreshold());
		this.bwModel.setValue((int) controller.getBWThreshold());
	}
	
	private void createTresholdSpinners() {
		JLabel lblBW = new JLabel("Black \u2192 White");
		this.container.add(lblBW, "cell 0 0");
		JSpinner spinBW = new JSpinner();
		this.bwModel = new SpinnerNumberModel(50, 0, 100, 1);
		spinBW.setModel(this.bwModel);
		this.container.add(spinBW, "cell 1 0,growx");
		this.bwModel.addChangeListener(new BWChangeListener());

		JLabel lblBWUnit = new JLabel("%");
		container.add(lblBWUnit, "cell 2 0");

		JLabel lblWB = new JLabel("White \u2192 Black");
		this.container.add(lblWB, "cell 0 1");
		JSpinner spinWB = new JSpinner();
		this.wbModel = new SpinnerNumberModel(50, 0, 100, 1);
		spinWB.setModel(this.wbModel);
		this.container.add(spinWB, "cell 1 1,growx");
		this.wbModel.addChangeListener(new WBChangeListener());
	}

	private void createScanSpeedSpinner() {
		JLabel lblWBUnit = new JLabel("%");
		container.add(lblWBUnit, "cell 2 1");
		JLabel lblSpeed = new JLabel("Scan travel speed");
		this.container.add(lblSpeed, "cell 0 2");

		JSpinner spinSpeed = new JSpinner();
		this.scanSpeedModel = new SpinnerNumberModel(2, 1, 20, 1);
		spinSpeed.setModel(this.scanSpeedModel);
		this.container.add(spinSpeed, "cell 1 2,growx");

		JLabel lblSpeedUnit = new JLabel("cm/sec");
		container.add(lblSpeedUnit, "cell 2 2");
		this.scanSpeedModel.addChangeListener(new scanSpeedChangeListener());
	}
	
	private class WBChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setWBThreshold((int) wbModel.getValue());
		}
	}

	private class BWChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setBWThreshold((int) bwModel.getValue());
		}
	}

	private class scanSpeedChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setScanSpeed((int) scanSpeedModel.getValue());
		}
	}
}
