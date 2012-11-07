package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import mazestormer.controller.ILineFinderController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

public class LineFinderPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;
	private final ILineFinderController controller;
	private JPanel container;

	// Actions
	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();

	// The buttons to start/stop finding the line
	private JButton btnStart;
	private JButton btnStop;

	public LineFinderPanel(ILineFinderController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Perpendicular on line",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		container.setLayout(new MigLayout("", "[grow]", "[]"));
		add(this.container);

		createStartStopButtons();
	}

	private void createStartStopButtons() {
		JPanel buttons = new JPanel();
		container.add(buttons, "cell 0 0,grow");
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

	public void startSearching() {
		controller.startSearching();
	}

	public void stopSearching() {
		controller.stopSearching();
	}

	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Starts searching the line");
		}

		public void actionPerformed(ActionEvent e) {
			startSearching();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stops the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			stopSearching();
		}
	}
}
