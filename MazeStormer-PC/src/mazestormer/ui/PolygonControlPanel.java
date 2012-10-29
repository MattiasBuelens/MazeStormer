package mazestormer.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import mazestormer.controller.IPolygonControlController;
import mazestormer.controller.IPolygonControlController.Direction;
import mazestormer.controller.PolygonEvent;
import mazestormer.controller.EventType;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

public class PolygonControlPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IPolygonControlController controller;

	private JPanel container;
	private JButton btnStart;
	private JButton btnStop;

	private SpinnerModel sideLengthModel;
	private SpinnerModel nbSidesModel;
	private ComboBoxModel<Direction> directionModel;

	private final Action startAction = new StartAction();
	private final Action stopAction = new StopAction();

	public PolygonControlPanel(IPolygonControlController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Polygon control",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		container = new JPanel();
		container.setLayout(new MigLayout("", "[grow 75][grow][right]",
				"[][][][]"));
		add(container);

		createNbSides();
		createSideLength();
		createDirection();

		createButtons();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		setButtonState(false);
	}

	private void createNbSides() {
		nbSidesModel = new SpinnerNumberModel(new Integer(2), new Integer(2),
				null, new Integer(1));

		JLabel lblNbSides = new JLabel("Number of sides");
		container.add(lblNbSides, "cell 0 0,grow");

		JSpinner spinNbSides = new JSpinner();
		spinNbSides.setModel(nbSidesModel);
		container.add(spinNbSides, "cell 1 0,grow");
		
		JLabel lblUnit = new JLabel("sides");
		container.add(lblUnit, "cell 2 0,grow");
	}

	private void createSideLength() {
		sideLengthModel = new SpinnerNumberModel(new Double(0), new Double(0),
				null, new Double(1));

		JLabel lblSideLength = new JLabel("Side length");
		container.add(lblSideLength, "cell 0 1,grow");

		JSpinner spinSideLength = new JSpinner();
		spinSideLength.setModel(sideLengthModel);
		container.add(spinSideLength, "cell 1 1,grow");

		JLabel lblUnit = new JLabel("cm");
		container.add(lblUnit, "cell 2 1,grow");
	}

	private void createDirection() {
		directionModel = new DefaultComboBoxModel<Direction>(Direction.values());

		JLabel lblDirection = new JLabel("Direction");
		container.add(lblDirection, "cell 0 2,grow");

		JComboBox<Direction> cmbDirection = new JComboBox<Direction>();
		cmbDirection.setModel(directionModel);
		container.add(cmbDirection, "cell 1 2,grow");
	}

	private void createButtons() {
		JPanel buttons = new JPanel();
		container.add(buttons, "cell 0 3 3 1,grow");
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

	public void startPolygon() {
		double sideLength = (Double) sideLengthModel.getValue();
		int nbSides = (Integer) nbSidesModel.getValue();
		Direction direction = (Direction) directionModel.getSelectedItem();
		controller.startPolygon(nbSides, sideLength, direction);
	}

	public void stopPolygon() {
		controller.stopPolygon();
	}

	private void setButtonState(boolean isRunning) {
		btnStart.setEnabled(!isRunning);
		btnStop.setEnabled(isRunning);
	}

	@Subscribe
	public void onPolygonEvent(PolygonEvent e) {
		setButtonState(e.getEventType() == EventType.STARTED);
	}

	private class StartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Start the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			startPolygon();
		}
	}

	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stop the robot driver");
		}

		public void actionPerformed(ActionEvent e) {
			stopPolygon();
		}
	}
}
