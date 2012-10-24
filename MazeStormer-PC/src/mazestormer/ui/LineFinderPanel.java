package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

import mazestormer.controller.ILineFinderController;
import net.miginfocom.swing.MigLayout;

public class LineFinderPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;
	private final ILineFinderController controller;
	private JPanel container;
	
	//Spinner & Button for the high calibration (white)
	private SpinnerNumberModel highCalibrationModel;
	private JButton btnHighCalibration;
	
	//Spinner & Button for the low calibration (dark)
	private SpinnerNumberModel lowCalibrationModel;
	private JButton btnLowCalibration;
	
	//Actions
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();
	
	//The buttons to start/stop finding the line
	private JButton btnStart;
	private JButton btnStop;

	public LineFinderPanel(ILineFinderController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Linefinder control",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow][grow][right]", "[][][]"));
		add(this.container);
		
		createHighCalibrate();
		createLowCalibrate();
		createStartStopButtons();
	}

	private void createHighCalibrate() {
		this.highCalibrationModel = new SpinnerNumberModel(new Integer(0), new Integer(0),
				new Integer(1023), new Integer(1));
		
		JLabel lblCalibrate = new JLabel("Calibrate");
		container.add(lblCalibrate, "cell 0 0,grow");

		JSpinner spinHighCalibration = new JSpinner();
		spinHighCalibration.setModel(highCalibrationModel);
		container.add(spinHighCalibration, "cell 1 0,grow");
		
		btnHighCalibration = new JButton();
		btnHighCalibration.setToolTipText("Click this button if you want to measure the value of the lightsensor when placed on a white surface");
		btnHighCalibration.setAction(new calibrationAction("high"));
		btnHighCalibration.setText("High (white)");
		container.add(btnHighCalibration, "cell 2 0,grow");
	}
	
	private void createLowCalibrate() {
		this.lowCalibrationModel = new SpinnerNumberModel(new Integer(0), new Integer(0),
				new Integer(1023), new Integer(1));
		
		JSpinner spinLowCalibration = new JSpinner();
		spinLowCalibration.setModel(lowCalibrationModel);
		container.add(spinLowCalibration, "cell 1 1,grow");
		
		btnLowCalibration = new JButton();
		btnLowCalibration.setToolTipText("Click this button if you want to measure the value of the lightsensor when placed on a darker surface");
		btnLowCalibration.setAction(new calibrationAction("low"));
		btnLowCalibration.setText("Low (dark)");
		container.add(btnLowCalibration, "cell 2 1,grow");
	}
	
	private void createStartStopButtons() {
		JPanel buttons = new JPanel();
		container.add(buttons, "cell 0 2 4 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnStart = new JButton();
		btnStart.setAction(startAction);
		btnStart.setText("");
		btnStart.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(btnStart);

		btnStop = new JButton();
		btnStop.setAction(stopAction);
		btnStop.setText("");
		btnStop.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(btnStop);
	}
	
	public void calibrate(String type) {
		int lightValue = controller.measureLightValue();
		if(type.equals("high"))
			highCalibrationModel.setValue(lightValue);
		else if(type.equals("low"))
			lowCalibrationModel.setValue(lightValue);
	}
	
	public void startSearching() {
		int highValue = (int) highCalibrationModel.getValue();
		int lowValue = (int) lowCalibrationModel.getValue();
		controller.startSearching(highValue, lowValue);
	}
	
	public void stopSearching() {
		controller.stopSearching();
	}
	
	private class calibrationAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		private final String type;

		public calibrationAction(String type) {
			putValue(NAME, "calibrate");
			putValue(SHORT_DESCRIPTION, "Measures the raw value of the lightsensor");
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			calibrate(type);
		}
	}
	
	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Starts searching the line");
		}

		public void actionPerformed(ActionEvent e) {
			startSearching();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stops the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			stopSearching();
		}
	}
}
