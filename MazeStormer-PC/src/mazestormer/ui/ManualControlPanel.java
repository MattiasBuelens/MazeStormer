package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import mazestormer.controller.IManualControlController;
import mazestormer.robot.StopEvent;
import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.javarichclient.icon.tango.actions.GoDownIcon;
import com.javarichclient.icon.tango.actions.GoJumpIcon;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.GoPreviousIcon;
import com.javarichclient.icon.tango.actions.GoUpIcon;

public class ManualControlPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IManualControlController controller;

	private final Action forwardAction = new ForwardAction();
	private final Action backwardAction = new BackwardAction();
	private final Action leftAction = new LeftAction();
	private final Action rightAction = new RightAction();
	private final Action turnCWAction = new TurnClockwiseAction();
	private final Action turnCCWAction = new TurnCounterClockwiseAction();
	private final Action travelAction = new TravelAction();
	private final Action rotateAction = new RotateAction();
	private final Action stopAction = new StopAction();

	private JPanel container;
	private JToggleButton btnForward;
	private JToggleButton btnLeft;
	private JToggleButton btnBackward;
	private JToggleButton btnRight;
	private JPanel movePanel;
	private ParametersPanel parametersPanel;
	private ScanPanel scanPanel;

	private SpinnerNumberModel travelDistanceModel;
	private SpinnerNumberModel rotateAngleModel;

	/**
	 * Create the panel.
	 */
	public ManualControlPanel(IManualControlController controller) {
		setBorder(new TitledBorder(null, "Manual control",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.controller = controller;

		registerKeyboardActions();
		setLayout(new MigLayout("", "[grow][grow]", "[][][]"));

		createModels();

		container = new JPanel();
		container.setLayout(new MigLayout("", "[][][]", "[][]"));
		add(container, "cell 0 0 2 1,alignx center,aligny top");
		createControls();

		createMovePanel();

		parametersPanel = new ParametersPanel(controller.parameters());
		add(parametersPanel, "cell 1 1,grow");

		scanPanel = new ScanPanel(controller.scan());
		add(scanPanel, "cell 0 2 2 1,grow");

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void createModels() {
		travelDistanceModel = new SpinnerNumberModel(new Float(40f), new Float(
				0f), null, new Float(1f));
		rotateAngleModel = new SpinnerNumberModel(new Float(180f), new Float(
				-180f), new Float(180f), new Float(1f));
	}

	private void createControls() {
		Icon iconCW = new GoJumpIcon(32, 32);
		TransformIcon iconCCW = new TransformIcon(new GoJumpIcon(32, 32));
		iconCCW.reflectVertical();

		// Icon iconCW = new EditRedoIcon(32, 32);
		// Icon iconCCW = new EditUndoIcon(32, 32);

		JButton btnTurnCCW = new JButton();
		btnTurnCCW.setAction(turnCCWAction);
		btnTurnCCW.setText("");
		btnTurnCCW.setIcon(iconCCW);
		container.add(btnTurnCCW, "cell 0 0");

		btnForward = new JToggleButton();
		btnForward.addItemListener(new ControlListener(forwardAction,
				stopAction));
		btnForward.setIcon(new GoUpIcon(32, 32));
		btnForward.setToolTipText("Forward");
		container.add(btnForward, "cell 1 0");

		JButton btnTurnCW = new JButton();
		btnTurnCW.setAction(turnCWAction);
		btnTurnCW.setText("");
		btnTurnCW.setIcon(iconCW);
		container.add(btnTurnCW, "cell 2 0");

		btnLeft = new JToggleButton();
		btnLeft.addItemListener(new ControlListener(leftAction, stopAction));
		btnLeft.setIcon(new GoPreviousIcon(32, 32));
		btnLeft.setToolTipText("Left");
		container.add(btnLeft, "cell 0 1");

		btnBackward = new JToggleButton();
		btnBackward.addItemListener(new ControlListener(backwardAction,
				stopAction));
		btnBackward.setIcon(new GoDownIcon(32, 32));
		btnBackward.setToolTipText("Backward");
		container.add(btnBackward, "cell 1 1");

		btnRight = new JToggleButton();
		btnRight.addItemListener(new ControlListener(rightAction, stopAction));
		btnRight.setIcon(new GoNextIcon(32, 32));
		btnRight.setToolTipText("Right");
		container.add(btnRight, "cell 2 1,grow");
	}

	private void setCurrentButton(JToggleButton button) {
		btnForward.setSelected(button == btnForward);
		btnBackward.setSelected(button == btnBackward);
		btnLeft.setSelected(button == btnLeft);
		btnRight.setSelected(button == btnRight);
	}

	private void createMovePanel() {
		movePanel = new JPanel();
		add(movePanel, "cell 0 1,grow");
		movePanel.setLayout(new MigLayout("", "[fill][grow,fill][]", "[fill][fill]"));

		JButton btnTravel = new JButton();
		btnTravel.setAction(travelAction);
		movePanel.add(btnTravel, "cell 0 0");

		JSpinner spinTravelDistance = new JSpinner();
		spinTravelDistance.setModel(travelDistanceModel);
		movePanel.add(spinTravelDistance, "cell 1 0");

		JLabel lblTravelUnit = new JLabel("cm");
		movePanel.add(lblTravelUnit, "cell 2 0");

		JButton btnRotate = new JButton("Rotate");
		btnRotate.setAction(rotateAction);
		movePanel.add(btnRotate, "cell 0 1");

		JSpinner spinRotateAngle = new JSpinner();
		spinRotateAngle.setModel(rotateAngleModel);
		movePanel.add(spinRotateAngle, "cell 1 1");

		JLabel lblRotateUnit = new JLabel("\u00B0");
		movePanel.add(lblRotateUnit, "cell 2 1");
	}

	public void moveForward() {
		setCurrentButton(btnForward);
		controller.moveForward();
	}

	public void moveBackward() {
		setCurrentButton(btnBackward);
		controller.moveBackward();
	}

	public void rotateLeft() {
		setCurrentButton(btnLeft);
		controller.rotateLeft();
	}

	public void rotateRight() {
		setCurrentButton(btnRight);
		controller.rotateRight();
	}

	public void stop() {
		setCurrentButton(null);
		controller.stop();
	}

	public void travel(float distance) {
		setCurrentButton(null);
		controller.travel(distance);
	}

	public void rotate(float angle) {
		setCurrentButton(null);
		controller.rotate(angle);
	}

	@Subscribe
	public void onStopped(StopEvent e) {
		setCurrentButton(null);
	}

	/**
	 * The currently executing keyboard action.
	 */
	private Action currentAction;

	private void registerKeyboardActions() {
		bindKeyboardAction(KeyEvent.VK_Z, forwardAction, stopAction);
		bindKeyboardAction(KeyEvent.VK_S, backwardAction, stopAction);
		bindKeyboardAction(KeyEvent.VK_Q, leftAction, stopAction);
		bindKeyboardAction(KeyEvent.VK_D, rightAction, stopAction);
	}

	private void bindKeyboardAction(int key, Action startAction,
			Action stopAction) {
		Object start = new Object();
		Object stop = new Object();

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(key, 0, false), start);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(key, 0, true), stop);

		getActionMap().put(start, startAction);
		getActionMap().put(stop, stopAction);
	}

	private class ControlListener implements ItemListener {

		private final Action startAction;
		private final Action stopAction;

		public ControlListener(Action startAction, Action stopAction) {
			this.startAction = startAction;
			this.stopAction = new ConditionalAction(stopAction, startAction);
		}

		private void trigger(Action action) {
			action.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, null));
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				trigger(startAction);
			} else {
				trigger(stopAction);
			}
		}

	}

	private abstract class ImmediateAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (currentAction != this) {
				currentAction = this;
				go();
			}
		}

		protected abstract void go();
	}

	private class ConditionalAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final Action action;
		private final Action whenAction;

		public ConditionalAction(Action action, Action whenAction) {
			this.action = action;
			this.whenAction = whenAction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentAction == whenAction) {
				action.actionPerformed(e);
			}
		}
	}

	private class ForwardAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public ForwardAction() {
			putValue(NAME, "Move forward");
			putValue(SHORT_DESCRIPTION, "Move the robot forward");
		}

		@Override
		public void go() {
			moveForward();
		}
	}

	private class BackwardAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public BackwardAction() {
			putValue(NAME, "Move backward");
			putValue(SHORT_DESCRIPTION, "Move the robot backward");
		}

		@Override
		public void go() {
			moveBackward();
		}
	}

	private class LeftAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public LeftAction() {
			putValue(NAME, "Rotate left");
			putValue(SHORT_DESCRIPTION, "Rotate the robot counter-clockwise");
		}

		@Override
		public void go() {
			rotateLeft();
		}
	}

	private class RightAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public RightAction() {
			putValue(NAME, "Rotate right");
			putValue(SHORT_DESCRIPTION, "Rotate the robot clockwise");
		}

		@Override
		public void go() {
			rotateRight();
		}
	}

	private class TurnClockwiseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public TurnClockwiseAction() {
			putValue(NAME, "Turn clockwise");
			putValue(SHORT_DESCRIPTION, "Turn 90\u00B0 clockwise");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			rotate(-90f);
		}
	}

	private class TurnCounterClockwiseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public TurnCounterClockwiseAction() {
			putValue(NAME, "Turn clockwise");
			putValue(SHORT_DESCRIPTION, "Turn 90\u00B0 counter-clockwise");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			rotate(90f);
		}
	}

	private class TravelAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public TravelAction() {
			putValue(NAME, "Travel");
			putValue(SHORT_DESCRIPTION, "Travel the given distance");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			travel((float) travelDistanceModel.getValue());
		}
	}

	private class RotateAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public RotateAction() {
			putValue(NAME, "Rotate ");
			putValue(SHORT_DESCRIPTION, "Rotate along the given angle");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			rotate((float) rotateAngleModel.getValue());
		}
	}

	private class StopAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public StopAction() {
			putValue(NAME, "Stop");
			putValue(SHORT_DESCRIPTION, "Stop the robot");
		}

		@Override
		public void go() {
			stop();
		}
	}

}
