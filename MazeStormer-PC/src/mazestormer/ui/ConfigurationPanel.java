package mazestormer.ui;

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
import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.controller.IConfigurationController;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.GoNextIcon;
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
	private final Action controlModeAction = new ControlModeAction();
	private final Action stopAction = new StopAction();

	private JButton btnSwitchMode;

	public ConfigurationPanel(IConfigurationController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Configuration", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		container = new JPanel();
		container.setLayout(new MigLayout("hidemode 3",
				"[grow 75][grow][fill][fill]", "[fill][fill]"));
		add(container);

		createRobotType();
		createControlMode();

		btnStop = new JButton();
		container.add(btnStop, "cell 3 0 1 2");
		btnStop.setAction(stopAction);
		btnStop.setText("");
		btnStop.setIcon(new ProcessStopIcon(32, 32));

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

		btnConnect = new JButton();
		container.add(btnConnect, "flowx,cell 2 0");
		btnConnect.setAction(connectAction);
		btnConnect.setText("");
		btnConnect.setIcon(new MediaPlaybackStartIcon(24, 24));

		btnDisconnect = new JButton();
		container.add(btnDisconnect, "cell 2 0");
		btnDisconnect.setAction(disconnectAction);
		btnDisconnect.setText("");
		btnDisconnect.setIcon(new MediaEjectIcon(24, 24));
	}

	private void createControlMode() {
		controlModeModel = new DefaultComboBoxModel<ControlMode>(
				ControlMode.values());

		JLabel lblMode = new JLabel("Control mode");
		container.add(lblMode, "cell 0 1,grow");

		JComboBox<ControlMode> cmbMode = new JComboBox<ControlMode>();
		cmbMode.setModel(controlModeModel);
		container.add(cmbMode, "cell 1 1,grow");

		btnSwitchMode = new JButton();
		btnSwitchMode.setAction(controlModeAction);
		btnSwitchMode.setText("");
		btnSwitchMode.setIcon(new GoNextIcon(24, 24));
		container.add(btnSwitchMode, "cell 2 1");
	}

	public void connect() {
		RobotType robotType = (RobotType) robotTypeModel.getSelectedItem();
		controller.connect(robotType);
		setControlMode();
	}

	public void setControlMode() {
		ControlMode controlMode = (ControlMode) controlModeModel
				.getSelectedItem();
		controller.setControlMode(controlMode);
	}

	public void disconnect() {
		controller.disconnect();
	}

	public void stop() {
		controller.stop();
	}

	private void setConnectState(boolean isConnected) {
		btnConnect.setEnabled(!isConnected);
		btnConnect.setVisible(!isConnected);
		btnDisconnect.setEnabled(isConnected);
		btnDisconnect.setVisible(isConnected);
		btnSwitchMode.setEnabled(isConnected);
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

	private class ControlModeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ControlModeAction() {
			putValue(NAME, "Set control mode");
			putValue(SHORT_DESCRIPTION, "Set the control mode of the robot");
		}

		public void actionPerformed(ActionEvent e) {
			setControlMode();
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
