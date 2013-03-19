package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mazestormer.controller.IExplorerController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

public class ExplorerControlPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;
	private final IExplorerController controller;

	private SpinnerNumberModel lineAdjustIntervalModel;

	private JPanel container;
	private final ParametersPanel parametersPanel;
	private final BarcodeScanParameterPanel barcodeScanParameterPanel;
	private final BarcodeActionParameterPanel barcodeActionParameterPanel;
	private final TeleportParameterPanel teleportParameterPanel;
	private JCheckBox checkLineAdjust;

	// Actions
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();

	// Buttons to start/stop exploring
	private JButton btnStart;
	private JButton btnStop;

	public ExplorerControlPanel(IExplorerController controller) {
		this.controller = controller;
		this.parametersPanel = new ParametersPanel(this.controller.getParametersController());
		this.barcodeScanParameterPanel = new BarcodeScanParameterPanel(this.controller.getBarcodeController());
		this.barcodeActionParameterPanel = new BarcodeActionParameterPanel(this.controller.getBarcodeController());
		this.teleportParameterPanel = new TeleportParameterPanel(this.controller.getCheatController());

		setBorder(new TitledBorder(null, "Maze Explorer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow]", "[][][][][][]"));
		add(container);

		createStartStopButtons();
		createLineAdjust();
		createParametersPanel();
		createBarcodeScanParameterPanel();
		createBarcodeActionParameterPanel();
		createTeleportParameterPanel();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		checkLineAdjust.setSelected(controller.isLineAdjustEnabled());
		lineAdjustIntervalModel.setValue(controller.getLineAdjustInterval());
	}

	private void createStartStopButtons() {
		JPanel buttons = new JPanel();
		container.add(buttons, "cell 0 0 3 1,grow");
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

	private void createLineAdjust() {
		JPanel lineAdjust = new JPanel();
		lineAdjust.setLayout(new MigLayout("", "[][grow 60][grow]", "[]"));
		
		final JSpinner spinLineAdjust = new JSpinner();
		this.lineAdjustIntervalModel = new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1));
		spinLineAdjust.setModel(lineAdjustIntervalModel);
		lineAdjust.add(spinLineAdjust, "cell 1 0,growx");

		final JLabel lblLineAdjustUnit = new JLabel("tiles");
		lineAdjust.add(lblLineAdjustUnit, "cell 2 0,alignx left");
		this.lineAdjustIntervalModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setLineAdjustInterval((int) lineAdjustIntervalModel.getValue());
			}
		});

		this.checkLineAdjust = new JCheckBox("Adjust robot position every");
		lineAdjust.add(checkLineAdjust, "cell 0 0");
		this.checkLineAdjust.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean isEnabled = (e.getStateChange() == ItemEvent.SELECTED);
				controller.setLineAdjustEnabled(isEnabled);
				spinLineAdjust.setEnabled(isEnabled);
			}
		});
		
		this.container.add(lineAdjust, "cell 0 1 2 1,growx");
	}

	private void createParametersPanel() {
		this.container.add(this.parametersPanel, "cell 0 2 1 1,growx");
	}

	private void createBarcodeScanParameterPanel() {
		this.container.add(this.barcodeScanParameterPanel, "cell 1 2 1 1,growx");
	}

	private void createBarcodeActionParameterPanel() {
		this.container.add(this.barcodeActionParameterPanel, "cell 1 3 1 1,growx");
	}

	private void createTeleportParameterPanel() {
		this.container.add(this.teleportParameterPanel, "cell 0 3 1 1,growx");
	}

	public void startExploring() {
		controller.startExploring();
	}

	public void stopExploring() {
		controller.stopExploring();
	}

	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Starts searching the line");
		}

		public void actionPerformed(ActionEvent e) {
			startExploring();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stops the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			stopExploring();
		}
	}
}
