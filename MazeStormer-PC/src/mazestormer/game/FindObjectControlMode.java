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
		// als de queue van de explorecontrolMode niet leeg is, gewoon daaraan
		// opvragen. anders met de reachableSeesawQueue werken, hierin zitten al
		// de barcodeTiles van seesaws. Indien zelfs deze leeg is, ga dan op een
		// T stuk, of ga over naar een volgende fase
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		return getSubControlMode().getBarcodeMapping();
	}

	/*
	 * Utilities
	 */

}
