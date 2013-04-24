package mazestormer.command;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.IAction;
import mazestormer.maze.Tile;
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
	
	/*
	 * Constructor
	 */

	public Commander(Player player) {
		this.player = player;
		this.driver = new Driver(player, this.getMode());
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
	
	public ControlMode getCurrentControlMode(){
		return currentMode;
	}

	/*
	 * Objective management
	 */

	/**
	 * Starts persuing the objective of this commander.
	 */
	protected void start() {
		getDriver().start();
	}
	
	/**
	 * Stops persuing the objective of this commander.
	 */
	protected void stop() {
		getDriver().stop();
		releaseControl();
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
		currentMode.takeControl();
	}

	protected final void releaseControl() {
		if (getMode() != null) {
			getMode().releaseControl();
		}
		this.currentMode = null;
	}

	public abstract ControlMode nextMode();
	
	/*
	 * Driver support
	 */
	
	public Tile nextTile(Tile currentTile){
		return getCurrentControlMode().nextTile(currentTile);
	}
	
	public IAction getAction(Barcode barcode){
		return getCurrentControlMode().getAction(barcode);
	}

}
