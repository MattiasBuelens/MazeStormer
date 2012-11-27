package mazestormer.ui;

import java.awt.BorderLayout;
import java.beans.Beans;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import lejos.robotics.navigation.Move;
import mazestormer.connect.ConnectEvent;
import mazestormer.controller.IStateController;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.MoveEvent.EventType;

import com.google.common.eventbus.Subscribe;

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
		textPane.setEditable(false);
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
	public void onMove(MoveEvent e) {
		String x = String.format("%.2f", controller.getXPosition());
		String y = String.format("%.2f", controller.getYPosition());
		String heading = String.format("%.2f", controller.getHeading());
		String text = "POSE BEFORE ASSIGNMENT:" + "\n   x-position= " + x
				+ "\n   y-position= " + y + "\n   heading= " + heading + "\n"
				+ "\nCURRENT ASSIGNMENT: \n   ";

		if (e.getEventType() == EventType.STOPPED) {
			text = text + "stand still";
		} else if (e.getEventType() == EventType.STARTED) {
			Move move = e.getMove();
			switch (move.getMoveType()) {
			case TRAVEL:
				float speed = Math.round(Math.abs(move.getTravelSpeed()) * 10) / 10;
				if (move.getDistanceTraveled() == Float.POSITIVE_INFINITY)
					text = text + "translate forward at " + speed + "cm/s";
				else if (move.getDistanceTraveled() == Float.NEGATIVE_INFINITY)
					text = text + "translate backward at " + speed + "cm/s";
				else if (move.getTravelSpeed() >= 0)
					text = text + "translate "
							+ Math.round(Math.abs(move.getDistanceTraveled() * 10) / 10)
							+ "cm forward at " + speed + "cm/s";
				else if (move.getTravelSpeed() < 0)
					text = text + "translate "
							+ Math.round(Math.abs(move.getDistanceTraveled() * 10) / 10)
							+ "cm backward at " + speed + "cm/s";
				break;
			case ROTATE:
				float rotateSpeed = Math
						.round(Math.abs(move.getRotateSpeed()) * 10) / 10;
				if (move.getAngleTurned() == Float.POSITIVE_INFINITY)
					text = text + "rotate counter-clockwise at " + rotateSpeed
							+ "deg/s";
				else if (move.getAngleTurned() == Float.NEGATIVE_INFINITY)
					text = text + "rotate clockwise at " + rotateSpeed
							+ "deg/s";
				else if (move.getRotateSpeed() >= 0)
					text = text + "rotate " + Math.round(Math.abs(move.getAngleTurned() * 10) / 10)
							+ "deg counter-clockwise at " + rotateSpeed
							+ "deg/s";
				else if (move.getRotateSpeed() < 0)
					text = text + "rotate " + Math.round(Math.abs(move.getAngleTurned() * 10) / 10)
							+ "deg clockwise at " + rotateSpeed + "deg/s";
				break;
			default:
				// n/a
			}
		} else {
			text = text + "UNKNOWN";
		}

		textPane.setText(text);
	}

}
