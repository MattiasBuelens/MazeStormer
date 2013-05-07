package mazestormer.ui;

import java.beans.Beans;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Move;
import mazestormer.controller.IStateController;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.MoveEvent.EventType;
import mazestormer.robot.RobotUpdate;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;

public class StatePanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private IStateController controller;
	private JTextField poseX;
	private JTextField poseY;
	private JTextField poseHeading;
	private JTextField lightValue;
	private JTextField infraredValue;
	private JTextField ultrasonicValue;
	private JTextField infraredAngle;
	private JTextField ultrasonicAngle;
	private JTextField movement;

	public StatePanel(IStateController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Current state", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[grow][grow]", "[grow][grow]"));

		createPosePanel();
		createSensorPanel();
		createMovement();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private final void createPosePanel() {
		JPanel posePanel = new JPanel();
		posePanel.setBorder(new TitledBorder(null, "Pose", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(posePanel, "cell 0 0,grow");
		posePanel.setLayout(new MigLayout("", "[grow][40:40:40,fill][fill]", "[grow][grow][grow]"));

		JLabel lblX = new JLabel("X:");
		posePanel.add(lblX, "cell 0 0");

		poseX = new JTextField();
		poseX.setEditable(false);
		posePanel.add(poseX, "cell 1 0,alignx right");

		JLabel lblUnitX = new JLabel("cm");
		posePanel.add(lblUnitX, "cell 2 0");

		JLabel lblY = new JLabel("Y:");
		posePanel.add(lblY, "cell 0 1");

		poseY = new JTextField();
		poseY.setEditable(false);
		posePanel.add(poseY, "cell 1 1,alignx right");

		JLabel lblUnitY = new JLabel("cm");
		posePanel.add(lblUnitY, "cell 2 1");

		JLabel lblHeading = new JLabel("Heading:");
		posePanel.add(lblHeading, "cell 0 2");
		poseHeading = new JTextField();
		poseHeading.setEditable(false);
		posePanel.add(poseHeading, "cell 1 2,alignx right");

		JLabel lblUnitHeading = new JLabel("\u00B0");
		posePanel.add(lblUnitHeading, "cell 2 2");
	}

	private final void createSensorPanel() {
		JPanel sensorPanel = new JPanel();
		sensorPanel.setBorder(new TitledBorder(null, "Sensor readings", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		add(sensorPanel, "cell 1 0,grow");
		sensorPanel.setLayout(new MigLayout("", "[grow][25:25:25,fill][fill][25:25:25,fill][fill]",
				"[grow][grow][grow]"));

		createLight(sensorPanel);
		createInfrared(sensorPanel);
		createUltrasonic(sensorPanel);
	}

	private final void createLight(JPanel sensorPanel) {
		JLabel lblLight = new JLabel("Light:");
		sensorPanel.add(lblLight, "cell 0 0");

		lightValue = new JTextField();
		lightValue.setEditable(false);
		sensorPanel.add(lightValue, "cell 1 0,alignx right");

		JLabel lblUnitLight = new JLabel("");
		sensorPanel.add(lblUnitLight, "cell 2 0");
	}

	private final void createInfrared(JPanel sensorPanel) {
		JLabel lblInfrared = new JLabel("Infrared:");
		sensorPanel.add(lblInfrared, "cell 0 1");

		infraredValue = new JTextField();
		infraredValue.setEditable(false);
		sensorPanel.add(infraredValue, "cell 1 1,alignx right");

		JLabel lblBetweenInfrared = new JLabel("% at");
		sensorPanel.add(lblBetweenInfrared, "flowx,cell 2 1");

		infraredAngle = new JTextField();
		infraredAngle.setEditable(false);
		sensorPanel.add(infraredAngle, "cell 3 1,alignx right");

		JLabel lblDegreesInfrared = new JLabel("\u00B0");
		sensorPanel.add(lblDegreesInfrared, "cell 4 1");
	}

	private final void createUltrasonic(JPanel sensorPanel) {
		JLabel lblUltrasonic = new JLabel("Ultrasonic:");
		sensorPanel.add(lblUltrasonic, "cell 0 2");

		ultrasonicValue = new JTextField();
		ultrasonicValue.setEditable(false);
		sensorPanel.add(ultrasonicValue, "cell 1 2,alignx right");

		JLabel lblBetweenUltrasonic = new JLabel("cm at");
		sensorPanel.add(lblBetweenUltrasonic, "flowx,cell 2 2");

		ultrasonicAngle = new JTextField();
		ultrasonicAngle.setEditable(false);
		sensorPanel.add(ultrasonicAngle, "cell 3 2,alignx right");

		JLabel lblDegreesUltrasonic = new JLabel("\u00B0");
		sensorPanel.add(lblDegreesUltrasonic, "cell 4 2");
	}

	private void createMovement() {
		JPanel movementPanel = new JPanel();
		movementPanel.setBorder(new TitledBorder(null, "Current movement", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		add(movementPanel, "cell 0 1 2 1,grow");
		movementPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));

		movement = new JTextField();
		movement.setEditable(false);
		movementPanel.add(movement, "cell 0 0");
	}

	@Subscribe
	public void onMove(MoveEvent event) {
		// Pose
		String x = String.format("%.2f", controller.getXPosition());
		poseX.setText(x);
		String y = String.format("%.2f", controller.getYPosition());
		poseY.setText(y);
		String heading = String.format("%.2f", controller.getHeading());
		poseHeading.setText(heading);

		// Move
		Move move = event.getMove();
		if (move != null) {
			StringBuilder sb = new StringBuilder("");
			if (event.getEventType() == EventType.STOPPED) {
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
			movement.setText(sb.toString());
		}
	}

	@Subscribe
	public void onUpdate(RobotUpdate update) {
		// Light
		lightValue.setText(Integer.toString(update.getLightValue()));
		// Infrared
		float infraredAngle = update.getInfraredAngle();
		String infraredText = null;
		if (Float.isNaN(infraredAngle)) {
			infraredText = "--";
		} else {
			infraredText = String.format("%.0f", infraredAngle);
		}
		infraredValue.setText(infraredText);
	}

	@Subscribe
	public void onRangeReading(RangeReading reading) {
		// Ultrasonic
		ultrasonicValue.setText(String.format("%.0f", reading.getRange()));
		ultrasonicAngle.setText(String.format("%.0f", reading.getAngle()));
	}

}
