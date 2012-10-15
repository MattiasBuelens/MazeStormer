package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import mazestormer.controller.IManualControlController;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.GoDownIcon;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.GoPreviousIcon;
import com.javarichclient.icon.tango.actions.GoUpIcon;
import javax.swing.border.TitledBorder;

public class ManualControlPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final IManualControlController controller;

	private final Action moveForward = new MoveForwardAction();
	private final Action moveBackward = new MoveBackwardAction();
	private final Action turnLeft = new RotateLeftAction();
	private final Action turnRight = new RotateRightAction();
	private final Action stop = new StopAction();

	private JToggleButton btnForward;
	private JToggleButton btnLeft;
	private JToggleButton btnBackward;
	private JToggleButton btnRight;

	/**
	 * Create the panel.
	 */
	public ManualControlPanel(IManualControlController controller) {
		setBorder(new TitledBorder(null, "Manual control",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.controller = controller;

		registerKeyboardActions();

		setLayout(new MigLayout("", "[][][]", "[][]"));
		createControls();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());
	}

	private void createControls() {
		btnForward = new JToggleButton();
		btnForward.addItemListener(new ControlListener(moveForward, stop));
		btnForward.setIcon(new GoUpIcon(32, 32));
		add(btnForward, "cell 1 0,grow");

		btnLeft = new JToggleButton();
		btnLeft.addItemListener(new ControlListener(turnLeft, stop));
		btnLeft.setToolTipText("Left");
		btnLeft.setIcon(new GoPreviousIcon(32, 32));
		add(btnLeft, "cell 0 1,grow");

		btnBackward = new JToggleButton();
		btnBackward.addItemListener(new ControlListener(moveBackward, stop));
		btnBackward.setIcon(new GoDownIcon(32, 32));
		add(btnBackward, "cell 1 1,grow");

		btnRight = new JToggleButton();
		btnRight.addItemListener(new ControlListener(turnRight, stop));
		btnRight.setIcon(new GoNextIcon(32, 32));
		add(btnRight, "cell 2 1,grow");
	}

	private void setCurrentButton(JToggleButton button) {
		btnForward.setSelected(button == btnForward);
		btnBackward.setSelected(button == btnBackward);
		btnLeft.setSelected(button == btnLeft);
		btnRight.setSelected(button == btnRight);
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

	/**
	 * The currently executing keyboard action.
	 */
	private Action currentAction;

	private void registerKeyboardActions() {
		bindKeyboardAction(KeyEvent.VK_Z, moveForward, stop);
		bindKeyboardAction(KeyEvent.VK_S, moveBackward, stop);
		bindKeyboardAction(KeyEvent.VK_Q, turnLeft, stop);
		bindKeyboardAction(KeyEvent.VK_D, turnRight, stop);
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

	private class MoveForwardAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public MoveForwardAction() {
			putValue(NAME, "Move forward");
			putValue(SHORT_DESCRIPTION, "Move the robot forward");
		}

		@Override
		public void go() {
			moveForward();
		}
	}

	private class MoveBackwardAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public MoveBackwardAction() {
			putValue(NAME, "Move backward");
			putValue(SHORT_DESCRIPTION, "Move the robot backward");
		}

		@Override
		public void go() {
			moveBackward();
		}
	}

	private class RotateLeftAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public RotateLeftAction() {
			putValue(NAME, "Rotate left");
			putValue(SHORT_DESCRIPTION, "Rotate the robot counter-clockwise");
		}

		@Override
		public void go() {
			rotateLeft();
		}
	}

	private class RotateRightAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public RotateRightAction() {
			putValue(NAME, "Rotate right");
			putValue(SHORT_DESCRIPTION, "Rotate the robot clockwise");
		}

		@Override
		public void go() {
			rotateRight();
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
