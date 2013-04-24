package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import mazestormer.controller.GameSetUpEvent;
import mazestormer.controller.IGameSetUpController;
import mazestormer.game.ConnectionMode;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackPauseIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;
import com.javarichclient.icon.tango.actions.SystemLogOutIcon;

public class GameSetUpPanel extends ViewPanel {

	private static final long serialVersionUID = 1521591580799849697L;

	private final IGameSetUpController controller;

	private final JButton rename = new JButton();
	private final JButton join = new JButton();
	private final JButton leave = new JButton();
	private final JButton pause = new JButton();
	private final JButton stop = new JButton();
	private final Action renameAction = new RenameAction();
	private final Action joinAction = new JoinAction();
	private final Action leaveAction = new LeaveAction();
	private final Action pauseAction = new PauseAction();
	private final Action stopAction = new StopAction();
	private final JCheckBox ready = new JCheckBox();

	private ComboBoxModel<ConnectionMode> serverModel;
	private final JComboBox<ConnectionMode> server = new JComboBox<ConnectionMode>();
	private final JTextField playerID = new JTextField();
	private final JTextField gameID = new JTextField();

	public GameSetUpPanel(IGameSetUpController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Team Treasure Trek", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[][100px:n,grow][fill]", "[][][][][]"));

		createServer();
		createPlayerID();
		createJoinGame();
		createButtons();

		enableGameButtons(false, false);

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		serverModel.setSelectedItem(controller.getConnectionMode());
		playerID.setText(controller.getPlayerID());
		gameID.setText(controller.getGameID());
	}

	private ConnectionMode getServer() {
		return (ConnectionMode) serverModel.getSelectedItem();
	}

	private void createServer() {
		serverModel = new DefaultComboBoxModel<ConnectionMode>(ConnectionMode.values());

		JLabel lblServer = new JLabel("Server");
		add(lblServer, "cell 0 0");

		server.setModel(serverModel);
		add(server, "cell 1 0 2 1,grow");
	}

	private void createPlayerID() {
		JLabel lblName = new JLabel("Player name");
		add(lblName, "cell 0 1");

		add(playerID, "cell 1 1,grow");

		rename.setAction(renameAction);
		rename.setText("");
		rename.setIcon(new GoNextIcon(24, 24));
		add(rename, "cell 2 1");
	}

	private String getPlayerID() {
		return playerID.getText();
	}

	private void setPlayerID() {
		String id = getPlayerID();
		if (!id.isEmpty()) {
			controller.setPlayerID(id);
		}
	}

	private void createJoinGame() {
		JLabel lblLow = new JLabel("Join game");
		add(lblLow, "cell 0 2");

		add(gameID, "cell 1 2,grow");

		join.setAction(joinAction);
		join.setText("");
		join.setIcon(new ListAllIcon(24, 24));
		add(join, "cell 2 2");
	}

	private String getGameID() {
		return gameID.getText();
	}

	private void createButtons() {
		ready.setText("Ready");
		ready.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean isEnabled = (e.getStateChange() == ItemEvent.SELECTED);
				setReady(isEnabled);
			}
		});
		add(ready, "cell 0 3");

		leave.setAction(leaveAction);
		leave.setText("");
		leave.setIcon(new SystemLogOutIcon(24, 24));
		add(leave, "cell 2 3");

		JPanel buttons = new JPanel();
		add(buttons, "cell 0 4 3 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		pause.setAction(pauseAction);
		pause.setText("");
		pause.setIcon(new MediaPlaybackPauseIcon(32, 32));
		buttons.add(pause, "cell 0 0");

		stop.setAction(stopAction);
		stop.setText("");
		stop.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(stop, "cell 0 1");
	}

	private void enableGameButtons(boolean isJoined, boolean isPlaying) {
		rename.setEnabled(!isJoined);
		join.setEnabled(!isJoined);
		leave.setEnabled(isJoined);

		// TODO Implement events to report playing state
		ready.setEnabled(isJoined);
		pause.setEnabled(isJoined);// && isPlaying);
		stop.setEnabled(isJoined);// && isPlaying);
	}

	@Subscribe
	public void onGameEvent(GameSetUpEvent e) {
		switch (e.getEventType()) {
		case JOINED:
			// Clear ready state
			ready.setSelected(false);
			// Enable buttons
			enableGameButtons(true, true); // TODO Pass playing state
			break;
		case LEFT:
			enableGameButtons(false, false);
			break;
		case DISCONNECTED:
			enableGameButtons(false, false);
			break;
		case NOT_READY:
			showNotReady();
			break;
		default:
			break;
		}
	}

	private void showNotReady() {
		JOptionPane.showMessageDialog(null,
				"You have to select a robot type and/or source maze\n before you could create or join a game.",
				"Setup", 1);
	}

	private void joinGame() {
		controller.setConnectionMode(getServer());
		controller.setGameID(getGameID());
		if (!getGameID().trim().isEmpty()) {
			controller.joinGame();
		}
	}

	private void setReady(boolean isReady) {
		controller.setReady(isReady);
	}

	private void pauseGame() {
		controller.pauseGame();
	}

	private void stopGame() {
		controller.stopGame();
	}

	private void leaveGame() {
		controller.leaveGame();
	}

	private class RenameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public RenameAction() {
			putValue(NAME, "Set player name");
			putValue(SHORT_DESCRIPTION, "Set player name");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setPlayerID();
		}

	}

	private class JoinAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public JoinAction() {
			putValue(NAME, "Join game");
			putValue(SHORT_DESCRIPTION, "Join the game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			joinGame();
		}

	}

	private class LeaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public LeaveAction() {
			putValue(NAME, "Leave game");
			putValue(SHORT_DESCRIPTION, "Leave the game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			leaveGame();
		}
	}

	private class PauseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public PauseAction() {
			putValue(NAME, "Pause game");
			putValue(SHORT_DESCRIPTION, "Pause the game");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			pauseGame();
		}

	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop game");
			putValue(SHORT_DESCRIPTION, "Stop the game");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			stopGame();
		}

	}

}
