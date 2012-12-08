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

import net.miginfocom.swing.MigLayout;

import mazestormer.controller.IBarcodeController;

public class BarcodeActionParameterPanel extends ViewPanel {
	private static final long serialVersionUID = 1L;
	private final IBarcodeController controller;
	
	private SpinnerNumberModel lowSpeedModel;
	private SpinnerNumberModel highSpeedModel;
	
	private JPanel container;
	
	public BarcodeActionParameterPanel(IBarcodeController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Barcode action parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow][fill]", "[][]"));
		add(this.container);

		createSpeedSpinners();

		if (!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());

		updateCurrentSpinValue();
	}
	
	private void createSpeedSpinners() {
		JLabel lblLowSpeed = new JLabel("Barcode low speed");
		this.container.add(lblLowSpeed, "cell 0 0");

		JSpinner spinLowSpeed = new JSpinner();
		this.lowSpeedModel = new SpinnerNumberModel(2, (int) this.controller.getLowerSpeedBound(), 
				(int) this.controller.getUpperSpeedBound(), 1);
		spinLowSpeed.setModel(this.lowSpeedModel);
		this.container.add(spinLowSpeed, "cell 1 0,growx");

		JLabel lblLowSpeedUnit = new JLabel("cm/sec");
		container.add(lblLowSpeedUnit, "cell 2 0");
		this.lowSpeedModel.addChangeListener(new LowSpeedChangeListener());
		
		JLabel lblHighSpeed = new JLabel("Barcode high speed");
		this.container.add(lblHighSpeed, "cell 0 1");

		JSpinner spinHighSpeed = new JSpinner();
		this.highSpeedModel = new SpinnerNumberModel(9, (int) this.controller.getLowerSpeedBound(), 
				(int) this.controller.getUpperSpeedBound(), 1);
		spinHighSpeed.setModel(this.highSpeedModel);
		this.container.add(spinHighSpeed, "cell 1 1,growx");

		JLabel lblHighSpeedUnit = new JLabel("cm/sec");
		container.add(lblHighSpeedUnit, "cell 2 1");
		this.highSpeedModel.addChangeListener(new HighSpeedChangeListener());
	}
	
	private void updateCurrentSpinValue() {
		this.lowSpeedModel.setValue((int) this.controller.getLowSpeed());
		this.highSpeedModel.setValue((int) this.controller.getHighSpeed());
	}
	
	private class LowSpeedChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setLowSpeed((int) lowSpeedModel.getValue());
		}
	}
	
	private class HighSpeedChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setHighSpeed((int) highSpeedModel.getValue());
		}
	}
}
