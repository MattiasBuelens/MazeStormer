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

import mazestormer.connect.ConnectEvent;
import mazestormer.controller.CalibrationChangeEvent;
import mazestormer.controller.ICalibrationController;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaRecordIcon;

public class CalibrationPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final ICalibrationController controller;

	// Calibrate high (white)
	private SpinnerNumberModel highModel;
	private final Action calibrateHighAction = new CalibrateAction("high");

	// Calibrate low (black)
	private SpinnerNumberModel lowModel;
	private final Action calibrateLowAction = new CalibrateAction("low");

	private JPanel container;

	public CalibrationPanel(ICalibrationController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Calibration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createModels();

		container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow][fill]", "[][]"));
		add(this.container);

		createHighCalibrate();
		createLowCalibrate();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void createModels() {
		highModel = new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1023),
				Integer.valueOf(1));
		highModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setHighValue((int) highModel.getValue());
			}
		});

		lowModel = new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1023),
				Integer.valueOf(1));
		lowModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setLowValue((int) lowModel.getValue());
			}
		});
	}

	private void createHighCalibrate() {
		JLabel lblHigh = new JLabel("High (white)");
		container.add(lblHigh, "cell 0 0,grow");

		JSpinner spinHigh = new JSpinner();
		spinHigh.setModel(highModel);
		container.add(spinHigh, "cell 1 0,grow");

		JButton btnHigh = new JButton();
		btnHigh.setAction(calibrateHighAction);
		btnHigh.setText("");
		btnHigh.setIcon(new MediaRecordIcon(24, 24));
		container.add(btnHigh, "cell 2 0,grow");
	}

	private void createLowCalibrate() {
		JLabel lblLow = new JLabel("Low (black)");
		container.add(lblLow, "cell 0 1");

		JSpinner spinLow = new JSpinner();
		spinLow.setModel(lowModel);
		container.add(spinLow, "cell 1 1,grow");

		JButton btnLow = new JButton();
		btnLow.setAction(calibrateLowAction);
		btnLow.setText("");
		btnLow.setIcon(new MediaRecordIcon(24, 24));
		container.add(btnLow, "cell 2 1,grow");
	}

	public void calibrate(String type) {
		if (type.equals("high"))
			controller.calibrateHighValue();
		else if (type.equals("low"))
			controller.calibrateLowValue();
	}

	@Subscribe
	public void onParameterChanged(CalibrationChangeEvent e) {
		switch (e.getParameter()) {
		case "high":
			highModel.setValue(e.getValue());
			break;
		case "low":
			lowModel.setValue(e.getValue());
			break;
		}
	}

	private void updateState(boolean isConnected) {
		highModel.setValue(controller.getHighValue());
		lowModel.setValue(controller.getLowValue());
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		updateState(e.isConnected());
	}

	private class CalibrateAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final String type;

		public CalibrateAction(String type) {
			putValue(NAME, "Calibrate");
			putValue(SHORT_DESCRIPTION, "Calibrate using the currently read value from the light sensor");
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			calibrate(type);
		}
	}

}
