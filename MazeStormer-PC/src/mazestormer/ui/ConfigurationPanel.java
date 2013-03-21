package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import mazestormer.connect.ConnectEvent;
import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.controller.IConfigurationController;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.DocumentOpenIcon;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.MediaEjectIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.ProcessStopIcon;
import javax.swing.JTextField;

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
	private final Action browseMazeAction = new BrowseMazeAction();
	private final Action loadMazeAction = new LoadMazeAction();

	private JButton btnSwitchMode;
	private JLabel lblMaze;
	private JTextField txtMaze;
	private JButton btnMazeBrowse;
	private JButton btnMazeLoad;

	public ConfigurationPanel(IConfigurationController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Configuration", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		container = new JPanel();
		container.setLayout(new MigLayout("hidemode 3",
				"[grow 75][grow][fill][fill]", "[fill][fill][fill]"));
		add(container);

		createRobotType();
		createControlMode();
		createSourceMaze();

		btnStop = new JButton();
		btnStop.setAction(stopAction);
		btnStop.setText("");
		btnStop.setIcon(new ProcessStopIcon(32, 32));
		container.add(btnStop, "cell 3 0 1 3");

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

	private void createSourceMaze() {
		lblMaze = new JLabel("Source maze");
		container.add(lblMaze, "cell 0 2,grow");

		txtMaze = new JTextField();
		container.add(txtMaze, "flowx,cell 1 2,growx");

		btnMazeBrowse = new JButton();
		btnMazeBrowse.setAction(browseMazeAction);
		btnMazeBrowse.setText("");
		btnMazeBrowse.setIcon(new DocumentOpenIcon(24, 24));
		container.add(btnMazeBrowse, "cell 1 2");

		btnMazeLoad = new JButton();
		btnMazeLoad.setAction(loadMazeAction);
		btnMazeLoad.setText("");
		btnMazeLoad.setIcon(new GoNextIcon(24, 24));
		container.add(btnMazeLoad, "cell 2 2");
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

	public void loadMaze() {
		controller.loadMaze(getMazePath());
	}

	private String getMazePath() {
		return txtMaze.getText();
	}

	private void setMazePath(String path) {
		txtMaze.setText(path);
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

	private class BrowseMazeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public BrowseMazeAction() {
			putValue(NAME, "Browse for source maze file");
			putValue(SHORT_DESCRIPTION,
					"Browse for a file to use as source maze");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(getMazePath());
			int result = chooser.showOpenDialog(ConfigurationPanel.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file != null) {
					setMazePath(file.getAbsolutePath());
				}
			}
		}
	}

	private class LoadMazeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public LoadMazeAction() {
			putValue(NAME, "Load source maze");
			putValue(SHORT_DESCRIPTION,
					"Load the source maze from the given file");
		}

		public void actionPerformed(ActionEvent e) {
			loadMaze();
		}
	}
}
