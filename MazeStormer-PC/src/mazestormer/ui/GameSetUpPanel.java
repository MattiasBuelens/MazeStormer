package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.border.TitledBorder;

import mazestormer.controller.GameSetUpEvent;
import mazestormer.controller.IGameSetUpController;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.SystemLogOutIcon;
import com.javarichclient.icon.tango.actions.ViewRefreshIcon;

public class GameSetUpPanel extends ViewPanel {
	
	private static final long serialVersionUID = 1521591580799849697L;
	
	private final IGameSetUpController controller;
	
	private JButton createNew;
	private JButton join;
	private JButton refresh;
	private JButton leave;
	private ComboBoxModel<String> lobbyModel;
	
	private final Action createNewAction = new CreateNewAction();
	private final Action joinAction = new JoinAction();
	private final Action refreshAction = new RefreshAction();
	private final Action leaveAction = new LeaveAction();

	public GameSetUpPanel(IGameSetUpController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Game SetUp",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		
		createButtons();
		createList();
		
		if (!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());
	}
	
	private void createButtons() {
		this.createNew = new JButton("New button");
		add(this.createNew, "cell 0 0");
		this.createNew.setAction(this.createNewAction);
		this.createNew.setText("");
		this.createNew.setIcon(new ListAllIcon(24, 24));
		
		this.join = new JButton("New button");
		add(this.join, "cell 0 1");
		this.join.setAction(this.joinAction);
		this.join.setText("");
		this.join.setIcon(new GoNextIcon(24, 24));
		
		this.refresh = new JButton("New button");
		add(this.refresh, "cell 0 2");
		this.refresh.setAction(this.refreshAction);
		this.refresh.setText("");
		this.refresh.setIcon(new ViewRefreshIcon(24, 24));
		
		this.leave = new JButton("New button");
		add(this.leave, "cell 0 3");
		this.leave.setAction(this.leaveAction);
		this.leave.setText("");
		this.leave.setIcon(new SystemLogOutIcon(24, 24));
		
		enableGameButtons(true);
	}
	
	private void createList() {
		JComboBox<String> lobbyBox = new JComboBox<String>();
		this.lobbyModel = new DefaultComboBoxModel<String>(refreshLobby());
		lobbyBox.setModel(this.lobbyModel);
		add(lobbyBox, "cell 1 0,grow");
	}
	
	private void enableGameButtons(boolean request) {
		createNew.setEnabled(request);
		join.setEnabled(request);
		leave.setEnabled(!request);
	}
	
	@Subscribe
	public void onGameEvent(GameSetUpEvent e) {
		switch(e.getEventType()) {
		case JOINED :
			enableGameButtons(false);
			break;
		case LEFT :
			enableGameButtons(true);
			break;
		case DISCONNECTED :
			enableGameButtons(true);
			break;
		case NOT_READY :
			showNotReady();
			break;
		default:
			break;
		}
	}
	
	private void showNotReady() {
		JOptionPane.showMessageDialog(null,"You have to select a robot type and/or source maze\n before you could create or join a game.", "Setup", 1);
	}
	
	private String askForGameID() {
		return JOptionPane.showInputDialog(null, "You have to give a game identifier.", "New game", 3);
	}
	
	private void createGame() {
		String gameID = askForGameID();
		if(gameID != null) {
			this.controller.createGame(gameID.trim());
		}
	}
	private void joinGame() {
		this.controller.joinGame((String) this.lobbyModel.getSelectedItem());
	}
	
	private String[] refreshLobby() {
		return this.controller.refreshLobby();
	}
	
	private void leaveGame() {
		this.controller.leaveGame();
	}
	
	private class CreateNewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public CreateNewAction() {
			putValue(NAME, "Create new game");
			putValue(SHORT_DESCRIPTION,
					"Create new game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			createGame();
		}
		
	}
	
	private class JoinAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public JoinAction() {
			putValue(NAME, "Join selected game");
			putValue(SHORT_DESCRIPTION,
					"Join selected game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			joinGame();
		}
		
	}
	
	private class RefreshAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public RefreshAction() {
			putValue(NAME, "Refresh gamelobby");
			putValue(SHORT_DESCRIPTION,
					"Refresh gamelobby");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshLobby();
		}
	}
	
	private class LeaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public LeaveAction() {
			putValue(NAME, "Leave current game");
			putValue(SHORT_DESCRIPTION,
					"Leave current game");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			leaveGame();
			
		}
	}
}
