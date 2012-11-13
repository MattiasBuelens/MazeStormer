package mazestormer.command;

public class RotateCommand extends Command {

	public RotateCommand(CommandType type) {
		super(type);
	}

	public RotateCommand(CommandType type, double angle) {
		this(type);
		setAngle(angle);
	}

	public double getAngle() {
		return getParameter();
	}

	public void setAngle(double angle) {
		setParameter(angle);
	}

}
