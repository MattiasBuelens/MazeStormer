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

		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		table = new JTable();
		add(table);

		textPane = new JTextPane();
		textPane.setContentType("text/html");
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
		StringBuilder sb = new StringBuilder();
		// Font
		sb.append("<html><body style=\"font:");
		sb.append(getFont().getSize()).append("pt ");
		sb.append(getFont().getFamily());
		sb.append("\">");
		// Pose
		String x = String.format("%.2f", controller.getXPosition());
		String y = String.format("%.2f", controller.getYPosition());
		String heading = String.format("%.2f", controller.getHeading());
		sb.append("<strong>Pose before move:</strong><table>");
		sb.append("<tr><th>X</th><td>").append(x).append("</td></tr>");
		sb.append("<tr><th>Y</th><td>").append(y).append("</td></tr>");
		sb.append("<tr><th>Heading</th><td>").append(heading).append("°</dd></td></tr></table>");
		// Move
		Move move = e.getMove();
		if (move != null) {
			sb.append("<strong>Move:</strong> ");
			if (e.getEventType() == EventType.STOPPED) {
				sb.append("stand still");
			} else {
				switch (move.getMoveType()) {
				case TRAVEL:
					float travelSpeed = move.getTravelSpeed();
					float distance = move.getDistanceTraveled();
					sb.append("translate ");
					// Direction
					sb.append(distance >= 0 ? "forward" : "backward");
					// Distance
					if (!Float.isInfinite(distance)) {
						distance = Math.round(Math.abs(distance * 10) / 10);
						sb.append(" ").append(distance).append("cm");
					}
					// Speed
					travelSpeed = Math.round(Math.abs(travelSpeed) * 10) / 10;
					sb.append(" at ").append(travelSpeed).append(" cm/s");
					break;
				case ROTATE:
					float rotateSpeed = move.getRotateSpeed();
					float angle = move.getAngleTurned();
					boolean isCCW = angle >= 0;
					sb.append("rotate ");
					// Angle
					if (!Float.isInfinite(angle)) {
						angle = Math.round(Math.abs(angle * 10) / 10);
						sb.append(angle).append("° ");
					}
					// Direction
					sb.append(isCCW ? "counter-clockwise" : "clockwise");
					// Speed
					rotateSpeed = Math.round(Math.abs(rotateSpeed) * 10) / 10;
					sb.append(" at ").append(rotateSpeed).append("°/s");
					break;
				default:
					break;
				}
			}
		}
		sb.append("</body></html>");

		textPane.setText(sb.toString());
	}

}
