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
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JPanel;

public class StatePanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private IStateController controller;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;

	public StatePanel(IStateController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[100px:148.00:148px][150px:n:200px]", "[grow][grow]"));
		
		JPanel posePanel = new JPanel();
		add(posePanel, "cell 0 0,grow");
		posePanel.setLayout(new MigLayout("", "[]", "[grow][grow][grow][grow]"));
		
		JLabel lblPose = new JLabel("Pose:");
		posePanel.add(lblPose, "cell 0 0");
		lblPose.setFont(lblPose.getFont().deriveFont(lblPose.getFont().getStyle() | Font.BOLD));
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut_1, "flowx,cell 0 1");
		
		JLabel lblX = new JLabel("X:");
		posePanel.add(lblX, "cell 0 1");
		lblX.setHorizontalAlignment(SwingConstants.TRAILING);
		
		textField = new JTextField();
		posePanel.add(textField, "cell 0 1,growx");
		textField.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut, "flowx,cell 0 2");
		
		JLabel lblY = new JLabel("Y:");
		posePanel.add(lblY, "cell 0 2");
		
		textField_1 = new JTextField();
		posePanel.add(textField_1, "cell 0 2,growx");
		textField_1.setColumns(10);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut_4, "flowx,cell 0 3");
		
		JLabel lblHeading = new JLabel("Heading:");
		posePanel.add(lblHeading, "cell 0 3");
		
		textField_2 = new JTextField();
		posePanel.add(textField_2, "cell 0 3,growx");
		textField_2.setColumns(10);
		
		JLabel lblCm = new JLabel("cm");
		posePanel.add(lblCm, "cell 0 1");
		
		JLabel lblCm_1 = new JLabel("cm");
		posePanel.add(lblCm_1, "cell 0 2");
		
		JLabel label = new JLabel("\u00B0");
		posePanel.add(label, "cell 0 3");
		
		JPanel sensorPanel = new JPanel();
		add(sensorPanel, "cell 1 0,grow");
		sensorPanel.setLayout(new MigLayout("", "[130.00][75.00]", "[grow][grow][grow][grow]"));
		
		JLabel lblNewLabel = new JLabel("Sensorreadings:");
		sensorPanel.add(lblNewLabel, "cell 0 0");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_2, "flowx,cell 0 1");
		
		JLabel lblLight = new JLabel("Light:");
		sensorPanel.add(lblLight, "cell 0 1");
		
		textField_3 = new JTextField();
		sensorPanel.add(textField_3, "cell 0 1,growx");
		textField_3.setColumns(10);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_3, "flowx,cell 0 2");
		
		JLabel lblInfrared = new JLabel("Infrared:");
		sensorPanel.add(lblInfrared, "cell 0 2");
		
		textField_4 = new JTextField();
		sensorPanel.add(textField_4, "cell 0 2,growx");
		textField_4.setColumns(10);
		
		JLabel lblAt = new JLabel("at");
		sensorPanel.add(lblAt, "flowx,cell 1 2");
		
		textField_6 = new JTextField();
		sensorPanel.add(textField_6, "cell 1 2,growx");
		textField_6.setColumns(10);
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_5, "flowx,cell 0 3");
		
		JLabel lblUltrasonic = new JLabel("Ultrasonic:");
		sensorPanel.add(lblUltrasonic, "cell 0 3");
		
		textField_5 = new JTextField();
		sensorPanel.add(textField_5, "cell 0 3,growx");
		textField_5.setColumns(10);
		
		JLabel lblAt_1 = new JLabel("at");
		sensorPanel.add(lblAt_1, "flowx,cell 1 3");
		
		textField_7 = new JTextField();
		sensorPanel.add(textField_7, "cell 1 3,growx");
		textField_7.setColumns(10);
		
		JLabel label_1 = new JLabel("%");
		sensorPanel.add(label_1, "cell 0 1");
		
		JLabel label_2 = new JLabel("%");
		sensorPanel.add(label_2, "cell 0 2");
		
		JLabel lblCm_2 = new JLabel("cm");
		sensorPanel.add(lblCm_2, "cell 0 3");
		
		JLabel label_4 = new JLabel("\u00B0");
		sensorPanel.add(label_4, "cell 1 3");
		
		JLabel label_3 = new JLabel("\u00B0");
		sensorPanel.add(label_3, "cell 1 2");
		
		JPanel assignmentPanel = new JPanel();
		add(assignmentPanel, "cell 0 1 2 1,grow");
		assignmentPanel.setLayout(new MigLayout("", "[][243.00,grow]", "[18.00,grow][grow]"));
		
		JLabel lblCurrentAssignment = new JLabel("Current assignment:");
		assignmentPanel.add(lblCurrentAssignment, "cell 0 0");
		lblCurrentAssignment.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		textField_8 = new JTextField();
		assignmentPanel.add(textField_8, "cell 0 1 2 1,growx");
		textField_8.setColumns(10);

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

		//textPane.setText(sb.toString());
	}

}
