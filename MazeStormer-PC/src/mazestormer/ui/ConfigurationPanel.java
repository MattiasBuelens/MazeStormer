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

import mazestormer.connect.ConnectEvent;
import mazestormer.controller.IConfigurationController;
import mazestormer.controller.IConfigurationController.ControlMode;
import mazestormer.controller.IConfigurationController.RobotType;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaEjectIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.ProcessStopIcon;

public class ConfigurationPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IConfigurationController controller;

	private JPanel container;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnStop;

	private ComboBoxModel<RobotType> robotTypeModel;
	private ComboBoxModel<ControlMode> controlModeModel;

	private final Action connectAction = new ConnectAction();
	private final Action disconnectAction = new DisconnectAction();
	private final Action stopAction = new StopAction();

	public ConfigurationPanel(IConfigurationController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Configuration", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow]", "[][][]"));
		add(container);

		createRobotType();
		createControlMode();

		createButtons();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		setConnectState(controller.isConnected());
	}

	private void createRobotType() {
		robotTypeModel = new DefaultComboBoxModel<RobotType>(RobotType.values());

		JLabel lblType = new JLabel("Robot type");
		container.add(lblType, "cell 0 0,grow");

		JComboBox<RobotType> cmbType = new JComboBox<RobotType>();
		cmbType.setModel(robotTypeModel);
		container.add(cmbType, "cell 1 0,grow");
	}

	private void createControlMode() {
		controlModeModel = new DefaultComboBoxModel<ControlMode>(
				ControlMode.values());

		JLabel lblMode = new JLabel("Control mode");
		container.add(lblMode, "cell 0 1,grow");

		JComboBox<ControlMode> cmbMode = new JComboBox<ControlMode>();
		cmbMode.setModel(controlModeModel);
		container.add(cmbMode, "cell 1 1,grow");
	}

	private void createButtons() {
		JPanel buttons = new JPanel();
		container.add(buttons, "cell 0 2 3 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnConnect = new JButton();
		btnConnect.setAction(connectAction);
		btnConnect.setText("");
		btnConnect.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(btnConnect);

		btnDisconnect = new JButton();
		btnDisconnect.setAction(disconnectAction);
		btnDisconnect.setText("");
		btnDisconnect.setIcon(new MediaEjectIcon(32, 32));
		buttons.add(btnDisconnect);

		btnStop = new JButton();
		btnStop.setAction(stopAction);
		btnStop.setText("");
		btnStop.setIcon(new ProcessStopIcon(32, 32));
		buttons.add(btnStop);
	}

	public void connect() {
		RobotType robotType = (RobotType) robotTypeModel.getSelectedItem();
		ControlMode controlMode = (ControlMode) controlModeModel
				.getSelectedItem();
		controller.connect(robotType, controlMode);
	}

	public void disconnect() {
		controller.disconnect();
	}

	public void stop() {
		controller.stop();
	}

	private void setConnectState(boolean isConnected) {
		btnConnect.setEnabled(!isConnected);
		btnDisconnect.setEnabled(isConnected);
		btnStop.setEnabled(isConnected);
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		setConnectState(e.isConnected());
	}

	private class ConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ConnectAction() {
			putValue(NAME, "Connect");
			putValue(SHORT_DESCRIPTION, "Connect to the robot");
		}

		public void actionPerformed(ActionEvent e) {
			connect();
		}
	}

	private class DisconnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DisconnectAction() {
			putValue(NAME, "Disconnect");
			putValue(SHORT_DESCRIPTION, "Disconnect from the robot");
		}

		public void actionPerformed(ActionEvent e) {
			disconnect();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stop the robot immediately");
		}

		public void actionPerformed(ActionEvent e) {
			stop();
		}
	}
}
