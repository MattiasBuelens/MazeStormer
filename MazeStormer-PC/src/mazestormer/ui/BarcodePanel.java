package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

import mazestormer.controller.EventType;
import mazestormer.controller.IBarcodeController;
import mazestormer.controller.Threshold;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSpinner;

public class BarcodePanel extends ViewPanel {
	
	private static final long serialVersionUID = 12L;
	
	private final IBarcodeController controller;
	
	private JPanel container;
	private JButton btnStartAction;
	private JButton btnStopAction;
	private JButton btnStartScan;
	private JButton btnStopScan;
	private ComboBoxModel<String> actionModel;
	
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();
	
	private SpinnerNumberModel wbModel;
	private SpinnerNumberModel bwModel;
	private SpinnerNumberModel scanSpeedModel;

	public BarcodePanel(IBarcodeController controller){
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Barcode actions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow]", "[][][][][][][]"));
		add(this.container);
		
		createActionChoicePanel();
		createActionButtons();
		createScanButtons();
		createTresholdSpinners();
		createScanSpeedSpinner();

		if(!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());

		setActionButtonState(false);
		setScanButtonState(false);
	}
	
	private void createActionChoicePanel(){
		this.actionModel = new DefaultComboBoxModel<String>(IBarcodeController.ACTIONS);
		
		JLabel lblAction = new JLabel("Action");
		this.container.add(lblAction, "cell 0 0,grow");

		JComboBox<String> cmbAction = new JComboBox<String>();
		cmbAction.setModel(this.actionModel);
		this.container.add(cmbAction, "cell 1 0,grow");
	}
	
	private void createActionButtons(){
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 1 2 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.btnStartAction = new JButton();
		this.btnStartAction.setAction(startAction);
		this.btnStartAction.setText("");
		this.btnStartAction.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(this.btnStartAction);

		this.btnStopAction = new JButton();
		this.btnStopAction.setAction(stopAction);
		this.btnStopAction.setText("");
		this.btnStopAction.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(this.btnStopAction);
	}
	
	private void createScanButtons(){
		JLabel lblScan = new JLabel("Scan");
		this.container.add(lblScan, "cell 0 2");
		
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 3 2 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.btnStartScan = new JButton();
		this.btnStartScan.setAction(new StartScanAction());
		this.btnStartScan.setText("");
		this.btnStartScan.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(this.btnStartScan);

		this.btnStopScan = new JButton();
		this.btnStopScan.setAction(new StopScanAction());
		this.btnStopScan.setText("");
		this.btnStopScan.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(this.btnStopScan);
	}
	
	private void createTresholdSpinners(){
		JLabel lblBW = new JLabel("Black -> White Treshold");
		this.container.add(lblBW, "cell 0 4");
		JSpinner bwSpinner = new JSpinner();
		this.bwModel = new SpinnerNumberModel(50, 0, 100, 1);
		bwSpinner.setModel(this.bwModel);
		this.container.add(bwSpinner, "cell 1 4,growx");
		this.bwModel.addChangeListener(new BWChangeListener());
		
		JLabel lblWB = new JLabel("White -> Black Treshold");
		this.container.add(lblWB, "cell 0 5");
		JSpinner wbSpinner = new JSpinner();
		this.wbModel = new SpinnerNumberModel(50, 0, 100, 1);
		wbSpinner.setModel(this.wbModel);
		this.container.add(wbSpinner, "cell 1 5,growx");
		this.wbModel.addChangeListener(new WBChangeListener());
	}
	
	private void createScanSpeedSpinner(){
		JLabel lblScanTravelSpeed = new JLabel("Scan Travel Speed [cm/s]");
		this.container.add(lblScanTravelSpeed, "cell 0 6");
		
		JSpinner scanSpeedSpinner = new JSpinner();
		this.scanSpeedModel = new SpinnerNumberModel(new Double(1), new Double(2), null, new Double(1));
		scanSpeedSpinner.setModel(this.scanSpeedModel);
		this.container.add(scanSpeedSpinner, "cell 1 6,growx");
		this.scanSpeedModel.addChangeListener(new scanSpeedChangeListener());
	}

	public void startAction(){
		String s = (String) this.actionModel.getSelectedItem();
		this.controller.startAction(s);
	}

	public void stopAction(){
		this.controller.stopAction();
	}

	private void setActionButtonState(boolean isRunning){
		this.btnStartAction.setEnabled(!isRunning);
		this.btnStopAction.setEnabled(isRunning);
	}
	
	public void startScan(){
		this.controller.startScan();
	}
	
	public void stopScan(){
		this.controller.stopScan();
	}
	
	public void setScanSpeed(double speed){
		this.controller.setScanSpeed(speed);
	}
	
	private void setScanButtonState(boolean isRunning){
		this.btnStartScan.setEnabled(!isRunning);
		this.btnStopScan.setEnabled(isRunning);
	}
	
	@Subscribe
	public void onActionEvent(mazestormer.controller.ActionEvent e){
		setActionButtonState(e.getEventType() == EventType.STARTED);
		setScanButtonState(e.getEventType() == EventType.SCAN_STARTED);
	}

	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Start the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			startAction();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stop the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			stopAction();
		}
	}
	
	private class StartScanAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartScanAction() {
			putValue(NAME, "Start Scan");
			putValue(SHORT_DESCRIPTION, "Start barcode scanning");
		}

		public void actionPerformed(ActionEvent e) {
			startScan();
		}
	}
	
	private class StopScanAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopScanAction() {
			putValue(NAME, "Stop Scan");
			putValue(SHORT_DESCRIPTION, "Stop barcode scanning");
		}

		public void actionPerformed(ActionEvent e) {
			stopScan();
		}
	}
	
	private class WBChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			Threshold.WHITE_BLACK.setThresholdValue((int) wbModel.getValue());
		}
	}
	
	private class BWChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			Threshold.BLACK_WHITE.setThresholdValue((int) bwModel.getValue());
		}
	}
	
	private class scanSpeedChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			setScanSpeed((int) scanSpeedModel.getValue());
		}
	}
}
