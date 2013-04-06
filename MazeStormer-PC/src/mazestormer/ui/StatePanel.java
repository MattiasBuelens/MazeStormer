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
	private JTextField textField_X;
	private JTextField textField_Y;
	private JTextField textField_Heading;
	private JTextField textField_Light;
	private JTextField textField_Infrared;
	private JTextField textField_Ultrasonic;
	private JTextField textField_InfraredAngle;
	private JTextField textField_UltrasonicAngle;
	private JTextField textField_Movement;

	public StatePanel(IStateController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[100px][192px]", "[grow][grow]"));
		
		JPanel posePanel = new JPanel();
		add(posePanel, "cell 0 0,grow");
		posePanel.setLayout(new MigLayout("", "[][40:40:40][]", "[grow][grow][grow][grow]"));
		
		JLabel lblPose = new JLabel("Pose:");
		lblPose.setFont(new Font("Tahoma", Font.BOLD, 11));
		posePanel.add(lblPose, "cell 0 0");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut_1, "flowx,cell 0 1");
		
		textField_X = new JTextField();
		textField_X.setEditable(false);
		posePanel.add(textField_X, "cell 1 1,alignx right");
		textField_X.setColumns(10);
		
		JLabel lblCm = new JLabel("cm");
		posePanel.add(lblCm, "cell 2 1");
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut, "flowx,cell 0 2");
		
		textField_Y = new JTextField();
		textField_Y.setEditable(false);
		posePanel.add(textField_Y, "cell 1 2,alignx right");
		textField_Y.setColumns(10);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		posePanel.add(horizontalStrut_4, "flowx,cell 0 3");
		
		textField_Heading = new JTextField();
		textField_Heading.setEditable(false);
		posePanel.add(textField_Heading, "cell 1 3,alignx right");
		textField_Heading.setColumns(10);
		
		JLabel lblCm_1 = new JLabel("cm");
		posePanel.add(lblCm_1, "cell 2 2");
		
		JLabel label = new JLabel("\u00B0");
		posePanel.add(label, "cell 2 3");
		
		JLabel lblX = new JLabel("X:");
		posePanel.add(lblX, "cell 0 1");
		lblX.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JLabel lblY = new JLabel("Y:");
		posePanel.add(lblY, "cell 0 2");
		
		JLabel lblHeading = new JLabel("Heading:");
		posePanel.add(lblHeading, "cell 0 3");
		
		JPanel sensorPanel = new JPanel();
		add(sensorPanel, "cell 1 0,grow");
		sensorPanel.setLayout(new MigLayout("", "[][25:25:25][][25:25:25][]", "[grow][grow][grow][grow]"));
		
		JLabel lblNewLabel = new JLabel("Sensorreadings:");
		sensorPanel.add(lblNewLabel, "cell 0 0 2 1");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_2, "flowx,cell 0 1");
		
		textField_Light = new JTextField();
		textField_Light.setEditable(false);
		sensorPanel.add(textField_Light, "cell 1 1,alignx right");
		textField_Light.setColumns(10);
		
		JLabel label_1 = new JLabel("%");
		sensorPanel.add(label_1, "cell 2 1");
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_3, "flowx,cell 0 2");
		
		textField_Infrared = new JTextField();
		textField_Infrared.setEditable(false);
		sensorPanel.add(textField_Infrared, "cell 1 2,alignx right");
		textField_Infrared.setColumns(10);
		
		JLabel label_2 = new JLabel("%");
		sensorPanel.add(label_2, "flowx,cell 2 2");
		
		JLabel lblAt = new JLabel("at");
		sensorPanel.add(lblAt, "cell 2 2");
		
		textField_InfraredAngle = new JTextField();
		textField_InfraredAngle.setEditable(false);
		sensorPanel.add(textField_InfraredAngle, "cell 3 2,alignx right");
		textField_InfraredAngle.setColumns(10);
		
		JLabel label_3 = new JLabel("\u00B0");
		sensorPanel.add(label_3, "cell 4 2");
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		sensorPanel.add(horizontalStrut_5, "flowx,cell 0 3");
		
		textField_Ultrasonic = new JTextField();
		textField_Ultrasonic.setEditable(false);
		sensorPanel.add(textField_Ultrasonic, "cell 1 3,alignx right");
		textField_Ultrasonic.setColumns(10);
		
		JLabel lblCm_2 = new JLabel("cm");
		sensorPanel.add(lblCm_2, "flowx,cell 2 3");
		
		JLabel lblAt_1 = new JLabel("at");
		sensorPanel.add(lblAt_1, "cell 2 3");
		
		textField_UltrasonicAngle = new JTextField();
		textField_UltrasonicAngle.setEditable(false);
		sensorPanel.add(textField_UltrasonicAngle, "cell 3 3,alignx right");
		textField_UltrasonicAngle.setColumns(10);
		
		JLabel label_4 = new JLabel("\u00B0");
		sensorPanel.add(label_4, "cell 4 3");
		
		JLabel lblLight = new JLabel("Light:");
		sensorPanel.add(lblLight, "cell 0 1");
		
		JLabel lblInfrared = new JLabel("Infrared:");
		sensorPanel.add(lblInfrared, "cell 0 2");
		
		JLabel lblUltrasonic = new JLabel("Ultrasonic:");
		sensorPanel.add(lblUltrasonic, "cell 0 3");
		
		JPanel movementPanel = new JPanel();
		add(movementPanel, "cell 0 1 2 1,grow");
		movementPanel.setLayout(new MigLayout("", "[][243.00,grow]", "[18.00,grow][grow]"));
		
		JLabel lblCurrentMovement = new JLabel("Current movement:");
		movementPanel.add(lblCurrentMovement, "cell 0 0");
		lblCurrentMovement.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		textField_Movement = new JTextField();
		textField_Movement.setEditable(false);
		movementPanel.add(textField_Movement, "cell 0 1 2 1,growx");
		textField_Movement.setColumns(10);

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
		// Pose
		String x = String.format("%.2f", controller.getXPosition());
		textField_X.setText(x);
		String y = String.format("%.2f", controller.getYPosition());
		textField_Y.setText(y);
		String heading = String.format("%.2f", controller.getHeading());
		textField_Heading.setText(heading);
		
		// Move
		Move move = e.getMove();
		if (move != null) {
			StringBuilder sb = new StringBuilder("");
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
			textField_Movement.setText(sb.toString());
		}
	}

}
