package mazestormer.ui;

import java.awt.BorderLayout;
import java.beans.Beans;

import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.IStateController;
import mazestormer.controller.StateController;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.MoveEvent.EventType;

import com.google.common.eventbus.Subscribe;
import javax.swing.JTextPane;

public class StatePanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private IStateController controller;

	private JTable table;
	
	private JTextPane textPane;

	public StatePanel(IStateController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		table = new JTable();
		add(table);
		
		textPane = new JTextPane();
		add(textPane, BorderLayout.CENTER);

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void updateState(boolean isConnected) {
		setVisible(isConnected);
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		updateState(e.isConnected());
	}
	
	@Subscribe
	public void onMove(MoveEvent me) {
		String text = "STATUS:"
					+ "\n- CURRENT POSITION: \n"
					+ this.controller.getMainController().getPose()
					+ "\n"
					+ "\n- CURRENT ASSIGNMENT: \n";
		
		if(me.getEventType() == EventType.STOPPED) {
			text = text + "STAND STILL";
		}
		
		else if(me.getEventType() == EventType.STARTED) {
			text = text + me.getMove();
			}
				
		else {
			text = text + "UNKNOWN";
		}
	
		textPane.setText(text);
	}

}
