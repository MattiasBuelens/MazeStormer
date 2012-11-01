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
import javax.swing.border.TitledBorder;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

import mazestormer.controller.EventType;
import mazestormer.controller.IBarcodeController;
import net.miginfocom.swing.MigLayout;

public class BarcodePanel extends ViewPanel {
	
	private static final long serialVersionUID = 12L;
	
	private final IBarcodeController controller;
	
	private JPanel container;
	private JButton btnStart;
	private JButton btnStop;
	private ComboBoxModel<String> actionModel;
	
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();

	public BarcodePanel(IBarcodeController controller){
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Barcode control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow]", "[][]"));
		add(this.container);
		
		createActionChoicePanel();
		createButtons();

		if(!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());

		setButtonState(false);
	}
	
	private void createActionChoicePanel(){
		this.actionModel = new DefaultComboBoxModel<String>(IBarcodeController.ACTIONS);
		
		JLabel lblAction = new JLabel("Action");
		this.container.add(lblAction, "cell 0 0,grow");

		JComboBox<String> cmbAction = new JComboBox<String>();
		cmbAction.setModel(this.actionModel);
		this.container.add(cmbAction, "cell 1 0,grow");
	}
	
	private void createButtons(){
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 1 2 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.btnStart = new JButton();
		this.btnStart.setAction(startAction);
		this.btnStart.setText("");
		this.btnStart.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(this.btnStart);

		this.btnStop = new JButton();
		this.btnStop.setAction(stopAction);
		this.btnStop.setText("");
		this.btnStop.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(this.btnStop);
	}

	public void startAction(){
		String s = (String) this.actionModel.getSelectedItem();
		this.controller.startAction(s);
	}

	public void stopAction(){
		this.controller.stopAction();
	}

	private void setButtonState(boolean isRunning){
		this.btnStart.setEnabled(!isRunning);
		this.btnStop.setEnabled(isRunning);
	}
	
	@Subscribe
	public void onActionEvent(mazestormer.controller.ActionEvent e){
		setButtonState(e.getEventType() == EventType.STARTED);
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
}
