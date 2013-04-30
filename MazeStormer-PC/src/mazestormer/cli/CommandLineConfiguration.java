package mazestormer.cli;

import java.io.IOException;
import java.util.Properties;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.controller.IMainController;
import mazestormer.game.ConnectionMode;
import mazestormer.game.DummyGame;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.Player;
import mazestormer.player.RelativePlayer;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Robot;
import mazestormer.util.LongPoint;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Joiner;
import com.rabbitmq.client.Connection;

public class CommandLineConfiguration {

	private final IMainController controller;

	public CommandLineConfiguration(IMainController controller) {
		this.controller = controller;
	}

	public void parse(String[] args) throws ParseException {
		CommandLineParser parser = new BasicParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java mazestormer.controller.MainController",
					"A robot that storms through mazes, powered by LEGO NXT.\n", options, "", true);
			System.exit(0);
		}
		if (line.hasOption("maze")) {
			String mazeFilePath = line.getOptionValue("maze");
			controller.configuration().loadMaze(mazeFilePath);
		}
		if (line.hasOption("robot")) {
			String robotTypeString = line.getOptionValue("robot");
			RobotType robotType = RobotType.valueOf(robotTypeString.toUpperCase());
			controller.configuration().connect(robotType);
		}
		if (line.hasOption("control")) {
			String modeString = line.getOptionValue("control");
			ControlMode mode = ControlMode.byShortName(modeString);
			controller.configuration().setControlMode(mode);
		}
		if (line.hasOption("travel-speed")) {
			String travelSpeed = line.getOptionValue("travel-speed");
			controller.manualControl().parameters().setTravelSpeed(Double.parseDouble(travelSpeed));
		}
		if (line.hasOption("rotate-speed")) {
			String rotateSpeed = line.getOptionValue("rotate-speed");
			controller.manualControl().parameters().setRotateSpeed(Double.parseDouble(rotateSpeed));
		}
		if (line.hasOption("ttt")) {
			Properties tttProps = line.getOptionProperties("ttt");
			String modeString = tttProps.getProperty("server", tttServerDefault);
			ConnectionMode mode = ConnectionMode.valueOf(modeString.toUpperCase());
			String player = tttProps.getProperty("player", tttPlayerDefault);
			String game = tttProps.getProperty("game", tttGameDefault);
			controller.configuration().setControlMode(ControlMode.TeamTreasureTrek);
			controller.gameSetUpControl().setConnectionMode(mode);
			controller.gameSetUpControl().setPlayerID(player);
			controller.gameSetUpControl().setGameID(game);
		}
		if (line.hasOption("dummy")) {
			String[] dummies = line.getOptionValues("dummy");
			if (dummies.length % 3 != 0) {
				throw new ParseException("Invalid number of arguments for dummy: " + dummies.length);
			}
			for (int i = 0; i < dummies.length / 3; ++i) {
				long x = Long.parseLong(dummies[3 * i]);
				long y = Long.parseLong(dummies[3 * i + 1]);
				Orientation orientation = Orientation.byShortName(dummies[3 * i + 2].toUpperCase());
				createDummy("Dummy" + (i + 1), x, y, orientation);
			}
		}
		if (line.hasOption("dummy-player")) {
			String[] dummies = line.getOptionValues("dummy-player");
			for (String dummy : dummies) {
				try {
					createDummyPlayer(dummy);
				} catch (IOException e) {
					System.err.println("Failed to create dummy player: " + e.getMessage());
				}
			}
		}
	}

	private void createDummy(String name, long x, long y, Orientation orientation) {
		// Create dummy
		Robot robot = new ObservableRobot(ControllableRobot.robotWidth, ControllableRobot.robotHeight);
		RelativePlayer dummy = new RelativePlayer(name, robot, new Maze());
		// Position on tile
		IMaze maze = controller.getWorld().getMaze();
		Point position = maze.fromTile(new LongPoint(x, y).toPoint());
		float heading = Orientation.EAST.angleTo(orientation);
		// Set pose
		Pose pose = new Pose();
		pose.setLocation(position);
		pose.setHeading(heading);
		dummy.getRobot().getPoseProvider().setPose(pose);
		// Add to world
		controller.getWorld().addPlayer(dummy);
	}

	private void createDummyPlayer(String name) throws IOException {
		// Create player
		Robot robot = new ObservableRobot(ControllableRobot.robotWidth, ControllableRobot.robotHeight);
		Player player = new RelativePlayer(name, robot, new Maze());
		// Create game
		Connection connection = controller.gameSetUpControl().getConnectionMode().getConnection();
		String gameID = controller.gameSetUpControl().getGameID();
		DummyGame game = new DummyGame(connection, gameID, player);
		game.join();
	}

	private static final Options options = new Options();

	private static final String tttServerDefault = ConnectionMode.LOCAL.name().toLowerCase();
	private static final String tttPlayerDefault = "Brons";
	private static final String tttGameDefault = "BronsGame";

	@SuppressWarnings("static-access")
	private static void createOptions() {
		// help
		options.addOption(OptionBuilder.withLongOpt("help").withDescription("Prints this help message.").create("?"));

		// robot
		String robotTypes = makeList((Object[]) RobotType.values());
		options.addOption(OptionBuilder.withLongOpt("robot").hasArgs(1).withArgName("robotType")
				.withDescription("Connects to a robot:\n" + robotTypes).create("r"));

		// control mode
		String controlModes = makeList(ControlMode.getShortNames());
		options.addOption(OptionBuilder.withLongOpt("control").hasArgs(1).withArgName("controlMode")
				.withDescription("Switches to a control mode:\n" + controlModes).create("c"));

		// maze
		options.addOption(OptionBuilder.withLongOpt("maze").hasArgs(1).withArgName("mazeFile")
				.withDescription("Loads the given source maze.").create("m"));

		// travel speed
		options.addOption(OptionBuilder.withLongOpt("travel-speed").hasArgs(1).withArgName("cm/s")
				.withDescription("Sets the travel speed.").create("ts"));

		// rotate speed
		options.addOption(OptionBuilder.withLongOpt("rotate-speed").hasArgs(1).withArgName("deg/s")
				.withDescription("Sets the rotate speed.").create("rs"));

		// ttt
		String tttServers = Joiner.on('|').join(ConnectionMode.getNames()).toLowerCase()
				.replace(tttServerDefault, "[" + tttServerDefault + "]");
		String tttDesc = "Joins a Team Treasure Trek game.\nControl mode is also set to 'ttt'.\n"
				+ "Optional arguments:\n- server=" + tttServers + "\n- player[=" + tttPlayerDefault + "]\n- game[="
				+ tttGameDefault + "]";
		options.addOption(OptionBuilder.withArgName("property=value").hasOptionalArgs(3).withValueSeparator('=')
				.withDescription(tttDesc).create("ttt"));

		// dummy
		String orientations = Joiner.on('|').join(Orientation.getShortNames());
		String dummyDesc = "Adds a dummy robot to the world.\nRepeat this option to add multiple dummies.\n"
				+ "Required arguments:\n- x\n- y\n- orient=" + orientations;
		options.addOption(OptionBuilder.withLongOpt("dummy").withArgName("x> <y> <orient").hasArgs(3)
				.withDescription(dummyDesc).create("d"));

		// dummy player
		String dummyPlayerDesc = "Adds a dummy player to the game.\nRepeat this option to add multiple players.";
		options.addOption(OptionBuilder.withLongOpt("dummy-player").withArgName("playerID").hasArg().hasOptionalArgs()
				.withDescription(dummyPlayerDesc).create("dp"));
	}

	private static String makeList(Iterable<?> items) {
		return "- " + Joiner.on("\n- ").join(items).toLowerCase();
	}

	private static String makeList(Object... items) {
		return "- " + Joiner.on("\n- ").join(items).toLowerCase();
	}

	static {
		createOptions();
	}

}
