package mazestormer.explore;

import java.util.HashMap;
import java.util.Map;

import mazestormer.player.Player;

/**
 * Uses different controlmodes to achieve an objective.
 */
public abstract class Commander {

	/*
	 * Data
	 */
	private final Player player;
	private final Driver driver;

	/*
	 * Control Modes (and bindings)
	 */
	private ControlMode currentMode;
	private ControlMode startMode;
	private final Map<ControlMode, ControlMode> bindings = new HashMap<>();

	public Commander(Player player) {
		this.player = player;
		this.driver = new Driver(player, this);
	}

	/*
	 * Getters
	 */
	
	public final Player getPlayer() {
		return player;
	}
	
	public Driver getDriver() {
		return driver;
	}
	
	/*
	 * Control mode management
	 */

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

	/*
	 * Objective management
	 */

	/**
	 * Starts persuing the objective of this commander.
	 */
	protected void start() {
		setMode(getStartMode());
		getDriver().start();
	}
	
	/**
	 * Stops persuing the objective of this commander.
	 */
	protected void stop() {
		getDriver().stop();
		releaseControl();
	}

}
