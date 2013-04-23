package mazestormer.game;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.explore.AbstractExploreControlMode;
import mazestormer.explore.Commander;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class FindObjectControlMode extends ControlMode {

	private ControlMode currentSubControlMode;
	private ExploreIslandControlMode exploreIslandMode;
	private LeaveIslandControlMode leaveIslandMode;

	public FindObjectControlMode(Player player, Commander commander) {
		super(player, commander);
		currentSubControlMode = new ExploreIslandControlMode(getPlayer(), this);
	}

	/*
	 * Getters
	 */

	@Override
	public GameRunner getCommander() {
		return (GameRunner) super.getCommander();
	}

	private ControlMode getSubControlMode() {
		return this.currentSubControlMode;
	}

	/*
	 * Methodes specifiek voor deze controlMode
	 */

	@Override
	public void takeControl(Driver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseControl(Driver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public Tile nextTile(Tile currentTile) {
		return currentSubControlMode.nextTile(currentTile);
	}
	
	public ControlMode arrangeNextMode() {
		if(getSubControlMode() instanceof LeaveIslandControlMode){
			currentSubControlMode = new ExploreIslandControlMode(getPlayer(), this);
			return currentSubControlMode;
		}
		else if(getSubControlMode() instanceof ExploreIslandControlMode && !isMazeComplete()){
			currentSubControlMode = new LeaveIslandControlMode(getPlayer(), this);
			return currentSubControlMode;
		}
		else return getCommander().nextMode();
	}


	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		return getSubControlMode().getBarcodeMapping();
	}

	public void exploreIsland(){
		currentSubControlMode.releaseControl(getDriver());
		currentSubControlMode = exploreIslandMode;
		currentSubControlMode.takeControl(getDriver());
	}
	
	public void leaveIsland(){
		currentSubControlMode.releaseControl(getDriver());
		currentSubControlMode = leaveIslandMode;
		currentSubControlMode.takeControl(getDriver());
	}
	
	/*
	 * Utilities
	 */
	
	private boolean isMazeComplete(){
		//TODO: implementeren
		return true;
	}

}
