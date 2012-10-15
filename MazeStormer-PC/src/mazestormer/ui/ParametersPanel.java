package mazestormer.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mazestormer.ui.event.RobotParameterChangeRequest;
import net.miginfocom.swing.MigLayout;

public class ParametersPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private JPanel container;

	private SpinnerModel travelSpeedModel;
	private SpinnerModel rotateSpeedModel;

	/**
	 * Create the panel.
	 */
	public ParametersPanel() {
		setBorder(new TitledBorder(null, "Parameters", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		container = new JPanel();
		container.setLayout(new MigLayout("",
				"[grow 75][100px:n,grow 25][right]", "[][]"));
		add(container, BorderLayout.NORTH);

		createTravelSpeed();
		createRotateSpeed();
	}

	private void createTravelSpeed() {
		JLabel lblTravelSpeed = new JLabel("Travel speed");
		container.add(lblTravelSpeed, "cell 0 0,grow");

		travelSpeedModel = new SpinnerNumberModel(new Double(0), new Double(0),
				null, new Double(1));

		travelSpeedModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setTravelSpeed((double) travelSpeedModel.getValue());
			}
		});

		JSpinner spinTravelSpeed = new JSpinner();
		spinTravelSpeed.setModel(travelSpeedModel);
		container.add(spinTravelSpeed, "cell 1 0,grow");

		JLabel lblUnit = new JLabel("cm/sec");
		container.add(lblUnit, "cell 2 0,grow");
	}

	private void createRotateSpeed() {
		JLabel lblRotateSpeed = new JLabel("Rotate speed");
		container.add(lblRotateSpeed, "cell 0 1,grow");

		rotateSpeedModel = new SpinnerNumberModel(new Double(0), new Double(0),
				null, new Double(1));

		rotateSpeedModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setRotateSpeed((double) rotateSpeedModel.getValue());
			}
		});

		JSpinner spinRotateSpeed = new JSpinner();
		spinRotateSpeed.setModel(rotateSpeedModel);
		container.add(spinRotateSpeed, "cell 1 1,grow");

		JLabel lblUnit = new JLabel("degrees/sec");
		container.add(lblUnit, "cell 2 1,grow");
	}

	private void setTravelSpeed(double value) {
		getEventBus().post(
				new RobotParameterChangeRequest("travelSpeed", value));
	}

	private void setRotateSpeed(double value) {
		getEventBus().post(
				new RobotParameterChangeRequest("rotateSpeed", value));
	}
}
