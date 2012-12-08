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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.javarichclient.icon.tango.actions.SystemLogOutIcon;

import mazestormer.controller.ICheatController;
import net.miginfocom.swing.MigLayout;

public class TeleportParameterPanel extends ViewPanel {
	private static final long serialVersionUID = 1L;
	private final ICheatController controller;
	
	private final Action teleportAction = new TeleportAction();
	private SpinnerNumberModel yModel;
	private SpinnerNumberModel xModel;
	
	private JPanel container;
	private JButton btnTeleportAction;

	public TeleportParameterPanel(ICheatController controller) {
		this.controller = controller;
		
		setBorder(new TitledBorder(null, "Teleport",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.container = new JPanel();
		this.container.setLayout(new MigLayout("", "[grow 75][grow][grow]", "[][]"));
		add(this.container);

		createTeleportSpinners();
		createTeleportButton();

		if (!Beans.isDesignTime())
			registerController();
	}
	
	private void registerController() {
		registerEventBus(this.controller.getEventBus());
	}
	
	private void createTeleportButton() {
		JPanel buttons = new JPanel();
		this.container.add(buttons, "cell 0 0 3 1,grow");
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.btnTeleportAction = new JButton();
		this.btnTeleportAction.setAction(this.teleportAction);
		this.btnTeleportAction.setText("");
		this.btnTeleportAction.setIcon(new SystemLogOutIcon(32, 32));
		buttons.add(this.btnTeleportAction);
	}
	
	private void createTeleportSpinners() {
		JLabel lblGoal = new JLabel("Teleport to tile coordinates (X,Y)");
		this.container.add(lblGoal, "cell 0 1,alignx left,aligny baseline");

		JSpinner xSpinner = new JSpinner();
		this.xModel = new SpinnerNumberModel(0, (int) this.controller.getTileMinX(),
				(int) this.controller.getTileMaxX(), 1);
		xSpinner.setModel(this.xModel);
		this.container.add(xSpinner, "cell 1 1,growx");

		JSpinner ySpinner = new JSpinner();
		this.yModel = new SpinnerNumberModel(0, (int) this.controller.getTileMinY(),
				(int) this.controller.getTileMaxY(), 1);
		ySpinner.setModel(this.yModel);
		this.container.add(ySpinner, "cell 2 1,growx");
	}
	
	public void update() {
		updateTeleportCoordinates();
	}

	private void updateTeleportCoordinates() {
		this.xModel.setMinimum((int) this.controller.getTileMinX());
		this.xModel.setMaximum((int) this.controller.getTileMaxX());
		this.yModel.setMinimum((int) this.controller.getTileMinY());
		this.yModel.setMaximum((int) this.controller.getTileMaxY());
	}
	
	private void teleportAction() {
		this.controller.teleportTo((int) this.xModel.getValue(),
				(int) this.yModel.getValue());
	}
	
	private class TeleportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public TeleportAction() {
			putValue(NAME, "Teleport");
			putValue(SHORT_DESCRIPTION, "Teleport the robot.");
		}

		public void actionPerformed(ActionEvent e) {
			update();
			teleportAction();
		}
	}
}
