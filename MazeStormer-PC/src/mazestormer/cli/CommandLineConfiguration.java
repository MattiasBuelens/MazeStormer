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
					"A robot that storms through mazes, powered by LEGO NXT.", options, "", true);
			System.exit(0);
		}
		if (line.hasOption("maze")) {
			String mazeFilePath = line.getOptionValue("maze");
			controller.configuration().loadMaze(mazeFilePath);
		}
		if (line.hasOption("connect")) {
			String robotTypeString = line.getOptionValue("connect");
			RobotType robotType = RobotType.valueOf(robotTypeString.toUpperCase());
			controller.configuration().connect(robotType);
		}
		if (line.hasOption("mode")) {
			String modeString = line.getOptionValue("mode");
			ControlMode mode = ControlMode.byShortName(modeString);
			controller.configuration().setControlMode(mode);
		}
	}

	private static final Options options = new Options();
	static {
		options.addOption(OptionBuilder.withLongOpt("help").withDescription("prints this help message").create("?"));
		options.addOption(OptionBuilder.withLongOpt("connect").hasArgs(1).withArgName("robotType")
				.withDescription("connects to the robot").create("c"));
		options.addOption(OptionBuilder.withLongOpt("mode").hasArgs(1).withArgName("controlMode")
				.withDescription("switches to the given control mode").create("m"));
		options.addOption(OptionBuilder.hasArgs(1).withArgName("mazeFile")
				.withDescription("loads the given source maze").create("maze"));
	}

}
