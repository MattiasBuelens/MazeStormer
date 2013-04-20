package mazestormer.cli;

import java.util.Properties;

import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.controller.IMainController;
import mazestormer.game.ConnectionMode;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Joiner;

public class CommandLineConfiguration {

	private final IMainController controller;

	public CommandLineConfiguration(IMainController controller) {
		this.controller = controller;
	}

	public void parse(String[] args) {
		CommandLineParser parser = new BasicParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			return;
		}

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
		if (line.hasOption("ttt")) {
			Properties tttProps = line.getOptionProperties("ttt");
			String modeString = tttProps.getProperty("server", defaultTTTServer);
			ConnectionMode mode = ConnectionMode.valueOf(modeString.toUpperCase());
			String player = tttProps.getProperty("player", defaultTTTPlayer);
			String game = tttProps.getProperty("game", defaultTTTGame);
			controller.configuration().setControlMode(ControlMode.TeamTreasureTrek);
			controller.gameSetUpControl().setConnectionMode(mode);
			controller.gameSetUpControl().setPlayerID(player);
			controller.gameSetUpControl().setGameID(game);
		}
	}

	private static final Options options = new Options();

	private static final String defaultTTTServer = ConnectionMode.LOCAL.name().toLowerCase();
	private static final String defaultTTTPlayer = "Brons";
	private static final String defaultTTTGame = "BronsGame";

	@SuppressWarnings("static-access")
	private static void createOptions() {
		// help
		options.addOption(OptionBuilder.withLongOpt("help").withDescription("prints this help message").create("?"));

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

		// ttt
		String tttServers = Joiner.on('|').join(ConnectionMode.getNames()).toLowerCase()
				.replace(defaultTTTServer, "[" + defaultTTTServer + "]");
		String tttDesc = "Joins a Team Treasure Trek game.\nControl mode is also set to 'ttt'\n"
				+ "Optional properties:\n- server=" + tttServers + "\n- player[=" + defaultTTTPlayer + "]\n- game[="
				+ defaultTTTGame + "]";
		options.addOption(OptionBuilder.withArgName("property=value").hasOptionalArgs(3).withValueSeparator('=')
				.withDescription(tttDesc).create("ttt"));
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
