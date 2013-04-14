package mazestormer.command;

import java.util.HashMap;
import java.util.Map;

import mazestormer.controlMode.ControlMode;
import mazestormer.controlMode.Driver;
import mazestormer.player.Player;

/**
 * Commands and controls a driver through the maze.
 */
public abstract class Commander {

	private final CommandTools player;
	private final Driver driver;

	private ControlMode currentMode;
	private ControlMode startMode;
	private final Map<ControlMode, ControlMode> bindings = new HashMap<>();

	public Commander(CommandTools player) {
		this.player = player;
		this.driver = new Driver(player, this);
	}

	public final Player getPlayer() {
		return player;
	}

	public final Driver getDriver() {
		return driver;
	}

	public final ControlMode getMode() {
		return currentMode;
	}

	public final void setMode(ControlMode mode) {
		releaseControl();
		takeControl(mode);
	}

	protected final void takeControl(ControlMode mode) {
		this.currentMode = mode;
		getDriver().setBarcodeMapping(mode.getBarcodeMapping());
		currentMode.takeControl(getDriver());
	}

	protected final void releaseControl() {
		if (getMode() != null) {
			getMode().releaseControl(getDriver());
		}
		this.currentMode = null;
	}

	protected ControlMode getStartMode() {
		return startMode;
	}

	protected void setStartMode(ControlMode startMode) {
		this.startMode = startMode;
	}

	protected void start() {
		setMode(getStartMode());
		getDriver().start();
	}

	protected void stop() {
		getDriver().stop();
		releaseControl();
	}

	public final void bind(ControlMode previous, ControlMode next) {
		bindings.put(previous, next);
	}

	public boolean nextMode() {
		if (bindings.containsKey(getMode())) {
			setMode(bindings.get(getMode()));
			return true;
		} else {
			releaseControl();
			return false;
		}
	}

}
