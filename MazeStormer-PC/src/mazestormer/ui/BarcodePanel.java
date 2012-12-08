package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import mazestormer.barcode.ActionType;
import mazestormer.controller.BarcodeActionEvent;
import mazestormer.controller.BarcodeScanEvent;
import mazestormer.controller.IBarcodeController;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

public class BarcodePanel extends ViewPanel {

	private static final long serialVersionUID = 12L;

	private final IBarcodeController controller;
	private final BarcodeParameterPanel barcodeParameterPanel;

	private JPanel container;
	private JButton btnStartAction;
	private JButton btnStopAction;
	private JButton btnStartScan;
	private JButton btnStopScan;
	private ComboBoxModel<ActionType> actionModel;

	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();

	public BarcodePanel(IBarcodeController controller) {
		this.controller = controller;
		this.barcodeParameterPanel = new BarcodeParameterPanel(this.controller);

		setBorder(new TitledBorder(null, "Barcode actions",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow][fill]", "[][][][][][][]"));
		add(this.container);

		createActionChoicePanel();
		createActionButtons();
		createScanButtons();
		createBarcodeParameterPanel();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(this.controller.getEventBus());

		setActionButtonState(false);
		setScanButtonState(false);
	}

	private void createActionChoicePanel() {
		this.actionModel = new DefaultComboBoxModel<ActionType>(
				ActionType.values());

		JLabel lblAction = new JLabel("Action");
		this.container.add(lblAction, "cell 0 0,grow");

		JComboBox<ActionType> cmbAction = new JComboBox<ActionType>();
		cmbAction.setModel(this.actionModel);
		this.container.add(cmbAction, "cell 1 0 2 1,grow");
	}

	private void createActionButtons() {
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 1 3 1,grow");
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

	private void createScanButtons() {
		JLabel lblScan = new JLabel("Scan");
		this.container.add(lblScan, "cell 0 2");

		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 3 3 1,grow");
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
	
	private void createBarcodeParameterPanel() {
		this.container.add(this.barcodeParameterPanel, "cell 0 4 3 1,growx");
	}

	public void startAction() {
		ActionType actionType = (ActionType) actionModel.getSelectedItem();
		this.controller.startAction(actionType);
	}

	public void stopAction() {
		this.controller.stopAction();
	}

	private void setActionButtonState(boolean isRunning) {
		this.btnStartAction.setEnabled(!isRunning);
		this.btnStopAction.setEnabled(isRunning);
	}

	public void startScan() {
		this.controller.startScan();
	}

	public void stopScan() {
		this.controller.stopScan();
	}

	private void setScanButtonState(boolean isRunning) {
		this.btnStartScan.setEnabled(!isRunning);
		this.btnStopScan.setEnabled(isRunning);
	}

	@Subscribe
	public void onActionEvent(BarcodeActionEvent e) {
		setActionButtonState(e.getEventType() == BarcodeActionEvent.EventType.STARTED);
	}

	@Subscribe
	public void onScanEvent(BarcodeScanEvent e) {
		setScanButtonState(e.getEventType() == BarcodeScanEvent.EventType.STARTED);
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
}
