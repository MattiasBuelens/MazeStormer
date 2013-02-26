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
	private final JButton join = new JButton();
	private final JButton start = new JButton();
	private final JButton leave = new JButton();
	private final JTextField gameID  = new JTextField();

	private final Action joinAction = new JoinAction();
	private final Action startAction = new StartAction();
	private final Action leaveAction = new LeaveAction();

	public GameSetUpPanel(IGameSetUpController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Team Treasure Trek",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[][100px:n,grow][fill]", "[][]"));

		createButtons();
		createInputField();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(this.controller.getEventBus());
	}

	private void createButtons() {
		join.setToolTipText("Join the game");
		add(this.join, "cell 2 0");
		this.join.setAction(this.joinAction);
		this.join.setText("");
		this.join.setIcon(new ListAllIcon(24, 24));
		
		start.setToolTipText("Start the game");
		add(this.start, "cell 1 1,alignx right");
		this.start.setAction(this.startAction);
		this.start.setText("");
		this.start.setIcon(new GoNextIcon(24, 24));

		leave.setToolTipText("Leave");
		add(this.leave, "cell 2 1");
		this.leave.setAction(this.leaveAction);
		this.leave.setText("");
		this.leave.setIcon(new SystemLogOutIcon(24, 24));

		enableGameButtons(true);
	}

	private void createInputField() {
		JLabel lblLow = new JLabel("Join game");
		add(lblLow, "cell 0 0");
		add(this.gameID, "cell 1 0,grow");
	}

	private String getGameId() {
		return this.gameID.getText();
	}

	private void enableGameButtons(boolean request) {
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
		JOptionPane
				.showMessageDialog(
						null,
						"You have to select a robot type and/or source maze\n before you could create or join a game.",
						"Setup", 1);
	}

	private void joinGame() {
		String id = getGameId();
		if (!id.equals(""))
			this.controller.joinGame(id);
	}
	
	private void startGame() {
		this.controller.startGame();
	}

	private void leaveGame() {
		this.controller.leaveGame();
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
