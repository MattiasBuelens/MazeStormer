package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.EditFindReplaceIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;
import com.javarichclient.icon.tango.actions.MediaSkipForwardIcon;

import net.miginfocom.swing.MigLayout;

import mazestormer.controller.EventType;
import mazestormer.controller.IPathFindingController;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

public class PathFindingPanel extends ViewPanel {
	private static final long serialVersionUID = 1L;
	
	private final IPathFindingController controller;

	private JPanel container;

	private JButton btnStartStepAction;
	private JButton btnStartAction;
	private JButton btnStopAction;
	private JButton btnAddSourceMaze;
	
	private final Action startStepAction = new StartStepAction();
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();
	private final Action addSourceMazeAction = new AddSourceMazeAction();
	private JLabel lblX;
	private JLabel lblY;
	private SpinnerNumberModel yModel;
	private SpinnerNumberModel xModel;
	
	public PathFindingPanel(IPathFindingController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Path Finder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow][grow]", "[][][][]"));
		add(this.container);
		
		createActionButtons();
		createCoordinates();

		if (!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());
		setActionButtonState(false);
	}
	
	private void createActionButtons() {
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 0 3 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.btnStartStepAction = new JButton();
		this.btnStartStepAction.setAction(this.startStepAction);
		this.btnStartStepAction.setText("");
		this.btnStartStepAction.setIcon(new MediaPlaybackStartIcon(32, 32));
		buttons.add(this.btnStartStepAction);
		
		this.btnStartAction = new JButton();
		this.btnStartAction.setAction(this.startAction);
		this.btnStartAction.setText("");
		this.btnStartAction.setIcon(new MediaSkipForwardIcon(32, 32));
		buttons.add(this.btnStartAction);

		this.btnStopAction = new JButton();
		this.btnStopAction.setAction(this.stopAction);
		this.btnStopAction.setText("");
		this.btnStopAction.setIcon(new MediaPlaybackStopIcon(32, 32));
		buttons.add(this.btnStopAction);
		
		this.btnAddSourceMaze = new JButton();
		this.btnAddSourceMaze.setAction(this.addSourceMazeAction);
		this.btnAddSourceMaze.setText("");
		this.btnAddSourceMaze.setIcon(new EditFindReplaceIcon(32, 32));
		buttons.add(this.btnAddSourceMaze);
	}
	
	private void createCoordinates() {
		JLabel lblCurrent = new JLabel("Current tile coordinates (X,Y)");
		this.container.add(lblCurrent, "cell 0 1");
		
		this.lblX = new JLabel("");
		lblX.setHorizontalAlignment(SwingConstants.RIGHT);
		this.container.add(this.lblX, "cell 1 1,growx");
		
		this.lblY = new JLabel("");
		lblY.setHorizontalAlignment(SwingConstants.RIGHT);
		this.container.add(this.lblY, "cell 2 1,growx");
		
		JLabel lblGoal = new JLabel("Goal tile coordinates (X,Y)");
		this.container.add(lblGoal, "cell 0 2,alignx left,aligny baseline");
		
		JSpinner xSpinner = new JSpinner();
		this.xModel = new SpinnerNumberModel(0,0,0,1);
		xSpinner.setModel(this.xModel);
		this.container.add(xSpinner, "cell 1 2,growx");
		
		JSpinner ySpinner = new JSpinner();
		this.yModel = new SpinnerNumberModel(0,0,0,1);
		ySpinner.setModel(this.yModel);
		this.container.add(ySpinner, "cell 2 2,growx");
	}
	
	public void update() {
		updateCurrentCoordinates();
		updatePossibleGoalCoordinates();
	}
	
	private void updateCurrentCoordinates() {
		this.lblX.setText(""+ this.controller.getCurrentTileX());
		this.lblY.setText(""+ this.controller.getCurrentTileY());
	}
	
	private void updatePossibleGoalCoordinates() {
		this.xModel.setMinimum((int) this.controller.getTileMinX());
		this.xModel.setMaximum((int) this.controller.getTileMaxX());
		this.yModel.setMinimum((int) this.controller.getTileMinY());
		this.yModel.setMaximum((int) this.controller.getTileMaxY());
		
		this.xModel.setValue((int) this.controller.getCurrentTileX());
		this.yModel.setValue((int) this.controller.getCurrentTileY());
	}
	
	public void startAction() {
		this.controller.startAction((int) this.xModel.getValue(), (int) this.yModel.getValue());
	}
	
	public void startStepAction() {
		this.controller.startStepAction((int) this.xModel.getValue(), (int) this.yModel.getValue());
	}

	public void stopAction() {
		this.controller.stopAction();
	}

	private void setActionButtonState(boolean isRunning) {
		this.btnStartStepAction.setEnabled(!isRunning);
		this.btnStartAction.setEnabled(!isRunning);
		this.btnStopAction.setEnabled(isRunning);
	}
	
	public void addSourceMazeAction() {
		this.controller.addSourceMaze();
		update();
	}
	
	@Subscribe
	public void onActionEvent(mazestormer.controller.ActionEvent e) {
		setActionButtonState(e.getEventType() == EventType.STARTED);
		
		if(e.getEventType() == EventType.STOPPED) {
			updateCurrentCoordinates();
		}
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
	
	private class StartStepAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartStepAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Start the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			startStepAction();
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
	
	private class AddSourceMazeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public AddSourceMazeAction() {
			putValue(NAME, "Add source maze");
			putValue(SHORT_DESCRIPTION, "Add source maze");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			addSourceMazeAction();
		}
		
	}
}
