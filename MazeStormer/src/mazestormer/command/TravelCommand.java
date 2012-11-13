package mazestormer.command;

public class TravelCommand extends Command {

	public TravelCommand(CommandType type) {
		super(type);
	}

	public TravelCommand(CommandType type, double distance) {
		this(type);
		setDistance(distance);
	}

	public double getDistance() {
		return getParameter();
	}

	public void setDistance(double distance) {
		setParameter(distance);
	}

}
