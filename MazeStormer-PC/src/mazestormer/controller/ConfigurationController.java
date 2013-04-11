package mazestormer.controller;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.text.ParseException;

import mazestormer.connect.ControlMode;
import mazestormer.connect.ControlModeChangeEvent;
import mazestormer.connect.RobotType;
import mazestormer.maze.IMaze;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;
import mazestormer.robot.Pilot;
import mazestormer.robot.StopEvent;

public class ConfigurationController extends SubController implements
		IConfigurationController {

	private RobotType robotType;
	private ControlMode controlMode;

	public ConfigurationController(MainController mainController) {
		super(mainController);
	}

	private void log(String logText) {
		getMainController().getPlayer().getLogger().info(logText);
	}

	private void warning(String logText) {
		getMainController().getPlayer().getLogger().warning(logText);
	}

	@Override
	public RobotType getRobotType() {
		return robotType;
	}

	private void setRobotType(RobotType robotType) {
		this.robotType = robotType;
	}

	@Override
	public ControlMode getControlMode() {
		return controlMode;
	}

	@Override
	public void setControlMode(ControlMode controlMode) {
		if (controlMode != this.controlMode) {
			// Stop robot before changing
			stop();
			// Change control mode
			this.controlMode = controlMode;
			postEvent(new ControlModeChangeEvent(controlMode));
		}
	}

	private Pilot getPilot() {
		checkState(isConnected());
		return getMainController().getControllableRobot().getPilot();
	}

	@Override
	public boolean isConnected() {
		return getMainController().isConnected();
	}

	@Override
	public void connect(RobotType robotType) {
		checkState(!isConnected());

		setRobotType(robotType);
		getMainController().connect(robotType);
	}

	@Override
	public void disconnect() {
		checkState(isConnected());

		// Stop the robot
		stop();

		// Disconnect
		setControlMode(null);
		getMainController().disconnect();
	}

	@Override
	public void stop() {
		if (isConnected()) {
			getPilot().stop();
			postEvent(new StopEvent());
		}
	}
	
	private void postState(ConfigurationEvent.EventType eventType) {
		postEvent(new ConfigurationEvent(eventType));
	}

	@Override
	public void loadMaze(String mazeFilePath) {
		IMaze maze = getMainController().getWorld().getMaze();
		CharSequence contents;
		try {
			contents = FileUtils.load(mazeFilePath);
			maze.clear();
			new Parser(maze).parse(contents);
			log("Source maze successfully loaded.");
			postState(ConfigurationEvent.EventType.NEW_MAZE_LOADED);
		} catch (IOException e) {
			warning("Failed to load source maze: " + e.getMessage());
		} catch (ParseException e) {
			warning("Failed to parse source maze:" + e.getMessage());
		}
	}
}
