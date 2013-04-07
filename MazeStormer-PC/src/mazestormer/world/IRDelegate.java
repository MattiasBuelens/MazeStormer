package mazestormer.world;

import mazestormer.infrared.IRRobot;
import mazestormer.infrared.PhysicalIRRobot;
import mazestormer.infrared.VirtualIRRobot;
import mazestormer.player.RelativePlayer;
import mazestormer.robot.Robot;

public class IRDelegate {
	
	private final RelativePlayer delegate;
	private Robot current;
	private IRRobot irRobot;

	public IRDelegate(RelativePlayer player) {
		this.delegate = player;
		updateCurrent();
	}
	
	public RelativePlayer delegate() {
		return delegate;
	}
	
	private void updateCurrent() {
		if(this.current != delegate().getRobot()) {
			this.current = delegate().getRobot();
			this.irRobot = getAnIRRobot(this.current);
		}
	}
	
	public IRRobot getIRRobot() {
		updateCurrent();
		return this.irRobot;
	}
	
	private static IRRobot getAnIRRobot(Robot robot) {
		// TODO: Physical <> virtual
		// dummy if test without yellow remarks :D
		if(robot.hashCode()!=0) {
			return (new VirtualIRRobot(robot));
		} else {
			return (new PhysicalIRRobot(robot));
		}
	}
}
