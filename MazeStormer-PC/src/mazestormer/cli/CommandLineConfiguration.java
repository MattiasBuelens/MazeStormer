package mazestormer.cli;

import mazestormer.connect.ControlMode;
import mazestormer.connect.RobotType;
import mazestormer.controller.IMainController;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Joiner;

@SuppressWarnings("static-access")
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
	}

	private static final Options options = new Options();
	static {
		// help
		options.addOption(OptionBuilder.withLongOpt("help").withDescription("prints this help message").create("?"));

		// robot
		String robotTypes = makeList((Object[]) RobotType.values());
		options.addOption(OptionBuilder.withLongOpt("robot").hasArgs(1).withArgName("robotType")
				.withDescription("connects to a robot:\n" + robotTypes).create("r"));

		// control mode
		String controlModes = makeList(ControlMode.getShortNames());
		options.addOption(OptionBuilder.withLongOpt("control").hasArgs(1).withArgName("controlMode")
				.withDescription("switches to a control mode:\n" + controlModes).create("c"));

		// maze
		options.addOption(OptionBuilder.withLongOpt("maze").hasArgs(1).withArgName("mazeFile")
				.withDescription("loads the given source maze").create("m"));
	}

	private static String makeList(Iterable<?> items) {
		return "- " + Joiner.on("\n- ").join(items).toLowerCase();
	}

	private static String makeList(Object... items) {
		return "- " + Joiner.on("\n- ").join(items).toLowerCase();
	}

}
