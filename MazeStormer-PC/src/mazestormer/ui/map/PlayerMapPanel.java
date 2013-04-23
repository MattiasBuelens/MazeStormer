package mazestormer.ui.map;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import lejos.robotics.navigation.Pose;
import mazestormer.controller.IPlayerMapController;
import mazestormer.player.PlayerIdentifier;
import mazestormer.ui.SplitButton;
import mazestormer.ui.map.event.MapRobotPoseChangeEvent;

import com.google.common.eventbus.Subscribe;

public class PlayerMapPanel extends MapPanel {

	private static final long serialVersionUID = 1L;

	private final PlayerIdentifier player;

	private boolean isFollowing;

	private final IPlayerMapController controller;

	private final Action goToRobotAction = new GoToRobotAction();
	private final Action goToStartAction = new GoToStartAction();
	private final Action clearRangesAction = new ClearRangesAction();

	public static final double zoomFactor = 1.5d;

	public PlayerMapPanel(IPlayerMapController controller, PlayerIdentifier player) {
		super(controller);
		this.controller = controller;
		this.player = player;

		createActionButtons();
	}

	private void createActionButtons() {
		// Append to left action bar
		leftActionBar.add(createFollowButton());
		leftActionBar.add(createGoButton());

		// Add as first button on right action bar
		rightActionBar.add(createClearRangesButton(), 0);
	}

	private JToggleButton createFollowButton() {
		JToggleButton btnFollow = new JToggleButton("Follow robot");
		btnFollow.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setFollowing(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		return btnFollow;
	}

	private SplitButton createGoButton() {
		JPopupMenu menuGo = new JPopupMenu();

		JMenuItem menuGoRobot = new JMenuItem();
		menuGoRobot.setAction(goToRobotAction);
		menuGoRobot.setText("Go to robot");
		menuGo.add(menuGoRobot);
		JMenuItem menuGoStart = new JMenuItem();
		menuGoStart.setAction(goToStartAction);
		menuGoStart.setText("Go to start");
		menuGo.add(menuGoStart);

		SplitButton btnGo = new SplitButton();
		btnGo.setAlwaysDropDown(true);
		btnGo.setText("Go to");

		btnGo.setPopupMenu(menuGo);
		addPopup(this, menuGo);

		return btnGo;
	}

	private JButton createClearRangesButton() {
		JButton btnClearRanges = new JButton();
		btnClearRanges.setAction(clearRangesAction);
		btnClearRanges.setText("Clear ranges");
		return btnClearRanges;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
		updateRobotPose(controller.getRobotPose());
	}

	private void updateRobotPose(Pose pose) {
		canvas.setEnablePanInteractor(!isFollowing());
		if (isFollowing() && pose != null) {
			canvas.centerOn(pose.getX(), pose.getY(), pose.getHeading());
		}
	}

	@Subscribe
	public void onMapRobotPoseChanged(MapRobotPoseChangeEvent event) {
		if (event.getOwner().equals(controller) && event.getPlayer().equals(player)) {
			updateRobotPose(event.getPose());
		}
	}

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

	}

	private class GoToRobotAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public GoToRobotAction() {
			putValue(NAME, "Go to robot");
			putValue(SHORT_DESCRIPTION, "Center the map on the robot.");
		}

		public void actionPerformed(ActionEvent e) {
			Pose pose = controller.getRobotPose();
			if (pose != null) {
				canvas.centerOn(pose.getX(), pose.getY(), 0);
			}
		}
	}

	private class GoToStartAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public GoToStartAction() {
			putValue(NAME, "Go to start");
			putValue(SHORT_DESCRIPTION, "Center the map on the start position.");
		}

		public void actionPerformed(ActionEvent e) {
			canvas.centerOn(0, 0, 0);
		}
	}

	private class ClearRangesAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ClearRangesAction() {
			putValue(NAME, "Clear ranges");
			putValue(SHORT_DESCRIPTION, "Clear the detected ranges on the map.");
		}

		public void actionPerformed(ActionEvent e) {
			controller.clearRanges();
		}
	}

}
