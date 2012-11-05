package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;

import mazestormer.connect.ConnectEvent;
import mazestormer.controller.CalibrationChangeEvent;
import mazestormer.controller.ICalibrationController;

public class CalibrationPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	// Spinner, button & action for the high calibration (white)
	private SpinnerNumberModel highCalibrationModel;
	private JButton btnHighCalibration;
	private final Action highCalibrationAction = new calibrationAction("high");

	// Spinner, button & action for the low calibration (dark)
	private SpinnerNumberModel lowCalibrationModel;
	private JButton btnLowCalibration;
	private final Action lowCalibrationAction = new calibrationAction("low");

	private final ICalibrationController controller;

	private JPanel container;

	public CalibrationPanel(ICalibrationController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Calibration", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow][grow][right]",
				"[][]"));
		add(this.container);

		createHighCalibrate();
		createLowCalibrate();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void createHighCalibrate() {
		this.highCalibrationModel = new SpinnerNumberModel(new Integer(0),
				new Integer(0), new Integer(1023), new Integer(1));
		highCalibrationModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setHighValue((int) highCalibrationModel.getValue());
			}
		});

		JLabel lblCalibrate = new JLabel("LightSensor");
		container.add(lblCalibrate, "cell 0 0,grow");

		JSpinner spinHighCalibration = new JSpinner();
		spinHighCalibration.setModel(highCalibrationModel);
		container.add(spinHighCalibration, "cell 1 0,grow");

		btnHighCalibration = new JButton();
		btnHighCalibration
				.setToolTipText("Click this button if you want to measure the value of the lightsensor when placed on a white surface");
		btnHighCalibration.setAction(highCalibrationAction);
		btnHighCalibration.setText("High (white)");
		container.add(btnHighCalibration, "cell 2 0,grow");
	}

	private void createLowCalibrate() {
		this.lowCalibrationModel = new SpinnerNumberModel(new Integer(0),
				new Integer(0), new Integer(1023), new Integer(1));
		lowCalibrationModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setLowValue((int) lowCalibrationModel.getValue());
			}
		});

		JSpinner spinLowCalibration = new JSpinner();
		spinLowCalibration.setModel(lowCalibrationModel);
		container.add(spinLowCalibration, "cell 1 1,grow");

		btnLowCalibration = new JButton();
		btnLowCalibration
				.setToolTipText("Click this button if you want to measure the value of the lightsensor when placed on a darker surface");
		btnLowCalibration.setAction(lowCalibrationAction);
		btnLowCalibration.setText("Low (dark)");
		container.add(btnLowCalibration, "cell 2 1,grow");
	}

	public void calibrate(String type) {
		int lightValue = controller.measureLightValue();
		if (type.equals("high"))
			highCalibrationModel.setValue(lightValue);
		else if (type.equals("low"))
			lowCalibrationModel.setValue(lightValue);
	}

	@Subscribe
	public void onParameterChanged(CalibrationChangeEvent e) {
		switch (e.getParameter()) {
		case "high":
			highCalibrationModel.setValue(e.getValue());
			break;
		case "low":
			lowCalibrationModel.setValue(e.getValue());
			break;
		}
	}

	private class calibrationAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final String type;

		public calibrationAction(String type) {
			putValue(NAME, "calibrate");
			putValue(SHORT_DESCRIPTION,
					"Measures the raw value of the lightsensor");
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			calibrate(type);
		}
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void updateState(boolean isConnected) {
		setVisible(isConnected);
		highCalibrationModel.setValue(controller.getHighValue());
		lowCalibrationModel.setValue(controller.getLowValue());
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		updateState(e.isConnected());
	}
}
