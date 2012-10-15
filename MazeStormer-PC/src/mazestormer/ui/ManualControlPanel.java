package mazestormer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.GoDownIcon;
import com.javarichclient.icon.tango.actions.GoNextIcon;
import com.javarichclient.icon.tango.actions.GoPreviousIcon;
import com.javarichclient.icon.tango.actions.GoUpIcon;

public class ManualControlPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final Action moveForward = new MoveForwardAction();
	private final Action moveBackward = new MoveBackwardAction();
	private final Action turnLeft = new TurnLeftAction();
	private final Action turnRight = new TurnRightAction();
	private final Action stop = new StopAction();

	private JToggleButton btnForward;
	private JToggleButton btnLeft;
	private JToggleButton btnBackward;
	private JToggleButton btnRight;

	/**
	 * Create the panel.
	 */
	public ManualControlPanel() {
		registerKeyboardActions();

		setLayout(new MigLayout("", "[][][]", "[][]"));
		createControls();
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

	private void setCurrent(JToggleButton button) {
		btnForward.setSelected(button == btnForward);
		btnBackward.setSelected(button == btnBackward);
		btnLeft.setSelected(button == btnLeft);
		btnRight.setSelected(button == btnRight);
	}

	public void moveForward() {
		setCurrent(btnForward);
		System.out.println("Forward");
	}

	public void moveBackward() {
		setCurrent(btnBackward);
		System.out.println("Backward");
	}

	public void turnLeft() {
		setCurrent(btnLeft);
		System.out.println("Left");
	}

	public void turnRight() {
		setCurrent(btnRight);
		System.out.println("Right");
	}

	public void stop() {
		setCurrent(null);
		System.out.println("Stop");
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
			action.actionPerformed(new ActionEvent(this, 0, null));
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

	private class TurnLeftAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public TurnLeftAction() {
			putValue(NAME, "Turn left");
			putValue(SHORT_DESCRIPTION, "Turn the robot counter-clockwise");
		}

		@Override
		public void go() {
			turnLeft();
		}
	}

	private class TurnRightAction extends ImmediateAction {
		private static final long serialVersionUID = 1L;

		public TurnRightAction() {
			putValue(NAME, "Turn right");
			putValue(SHORT_DESCRIPTION, "Turn the robot clockwise");
		}

		@Override
		public void go() {
			turnRight();
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
