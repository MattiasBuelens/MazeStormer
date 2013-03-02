package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.border.TitledBorder;

import mazestormer.controller.GameSetUpEvent;
import mazestormer.controller.IGameSetUpController;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.SystemLogOutIcon;

public class GameSetUpPanel extends ViewPanel {

	private static final long serialVersionUID = 1521591580799849697L;

	private final IGameSetUpController controller;
	private final JButton rename = new JButton();
	private final JButton join = new JButton();
	private final JButton start = new JButton();
	private final JButton leave = new JButton();
	private final JTextField playerID = new JTextField();
	private final JTextField gameID = new JTextField();

	private final Action renameAction = new RenameAction();
	private final Action joinAction = new JoinAction();
	private final Action startAction = new StartAction();
	private final Action leaveAction = new LeaveAction();

	public GameSetUpPanel(IGameSetUpController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Team Treasure Trek", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[][100px:n,grow][fill]", "[][][]"));

		createPlayerID();
		createJoinGame();
		createButtons();

		enableGameButtons(true);

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		playerID.setText(controller.getPlayerID());
	}

	private void createPlayerID() {
		JLabel lblName = new JLabel("Player name");
		add(lblName, "cell 0 0");

		add(playerID, "cell 1 0,grow");

		rename.setToolTipText("Set player name");
		rename.setAction(renameAction);
		rename.setText("");
		rename.setIcon(new GoNextIcon(24, 24));
		add(rename, "cell 2 0");
	}

	private String getPlayerID() {
		return playerID.getText();
	}

	private void createJoinGame() {
		JLabel lblLow = new JLabel("Join game");
		add(lblLow, "cell 0 1");

		add(gameID, "cell 1 1,grow");

		join.setToolTipText("Join the game");
		join.setAction(joinAction);
		join.setText("");
		join.setIcon(new ListAllIcon(24, 24));
		add(join, "cell 2 1");
	}

	private String getGameID() {
		return gameID.getText();
	}

	private void createButtons() {
		start.setToolTipText("Start the game");
		start.setAction(startAction);
		start.setText("");
		start.setIcon(new GoNextIcon(24, 24));
		add(start, "cell 1 2,alignx right");

		leave.setToolTipText("Leave the game");
		leave.setAction(leaveAction);
		leave.setText("");
		leave.setIcon(new SystemLogOutIcon(24, 24));
		add(leave, "cell 2 2");
	}

	private void enableGameButtons(boolean request) {
		rename.setEnabled(request);
		join.setEnabled(request);
		start.setEnabled(!request);
		leave.setEnabled(!request);
	}

	@Subscribe
	public void onGameEvent(GameSetUpEvent e) {
		switch (e.getEventType()) {
		case JOINED:
			enableGameButtons(false);
			break;
		case LEFT:
			enableGameButtons(true);
			break;
		case DISCONNECTED:
			enableGameButtons(true);
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

	private void setPlayerID() {
		String id = getPlayerID();
		if (!id.isEmpty())
			controller.setPlayerID(id);
	}

	private void joinGame() {
		String id = getGameID();
		if (!id.isEmpty())
			controller.joinGame(id);
	}

	private void startGame() {
		controller.startGame();
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
			putValue(NAME, "Join selected game");
			putValue(SHORT_DESCRIPTION, "Join selected game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			joinGame();
		}

	}

	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start selected game");
			putValue(SHORT_DESCRIPTION, "Start selected game");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			startGame();
		}

	}

	private class LeaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public LeaveAction() {
			putValue(NAME, "Leave current game");
			putValue(SHORT_DESCRIPTION, "Leave current game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			leaveGame();

		}
	}
}
