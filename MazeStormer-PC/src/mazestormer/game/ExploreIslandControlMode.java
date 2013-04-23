package mazestormer.game;

import java.util.HashMap;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.explore.AbstractExploreControlMode;
import mazestormer.explore.ControlMode;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.util.Future;
import mazestormer.util.LongPoint;

public class ExploreIslandControlMode extends AbstractExploreControlMode {

	private final ControlMode superControlMode;
	
	public ExploreIslandControlMode(Player player, ControlMode superControlMode) {
		super(player, superControlMode.getCommander());
		this.superControlMode = superControlMode;
	}

	/*
	 * Getters
	 */
	
	private ControlMode getSuperControlMode(){
		return superControlMode;
	}
	
	@Override
	public BarcodeMapping getBarcodeMapping() {
		return new ExploreIslandBarcodeMapping();
	}
	
	/*
	 * Barcode-acties en logica
	 */
	
	private class ObjectAction extends ObjectFoundAction {

		private Barcode barcode;

		private ObjectAction(Barcode barcode) {
			this.barcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			setObjectTile(); // voeg info toe aan maze

			// TODO: verwijder volgende tegels uit queue? Worden ze ooit
			// toegevoegd?

			if (getObjectNumberFromBarcode(barcode) == ((GameRunner) getCommander()).getObjectNumber()) { // indien eigen barcode:
				objectFound(getTeamNumberFromBarcode(barcode));
				return super.performAction(player); // eigen voorwerp wordt
													// opgepikt
			} else {
				return null; // ?
			}
		}

		private int getObjectNumberFromBarcode(Barcode objectBarcode) {
			return (objectBarcode.getValue() % 4);
		}

		private int getTeamNumberFromBarcode(Barcode objectBarcode) {
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}

	}
	
	private class SeesawAction implements IAction{

		private SeesawAction(Barcode barcode){
		}
		
		@Override
		public Future<?> performAction(Player player) {
			// TODO Enkel als de andere kant van de wip ook op dit eiland ligt mag/moet de robot de wip oversteken
			return null;
		}
		
	}
	
	private class ExploreIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeTypeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(0), new ObjectAction(new Barcode(0)));
				put(new Barcode(1), new ObjectAction(new Barcode(1)));
				put(new Barcode(2), new ObjectAction(new Barcode(2)));
				put(new Barcode(3), new ObjectAction(new Barcode(3)));
				put(new Barcode(4), new ObjectAction(new Barcode(4)));
				put(new Barcode(5), new ObjectAction(new Barcode(5)));
				put(new Barcode(6), new ObjectAction(new Barcode(6)));
				put(new Barcode(7), new ObjectAction(new Barcode(7)));
				put(new Barcode(11), new SeesawAction(new Barcode(11)));
				put(new Barcode(13), new SeesawAction(new Barcode(13)));
				put(new Barcode(15), new SeesawAction(new Barcode(15)));
				put(new Barcode(17), new SeesawAction(new Barcode(17)));
				put(new Barcode(19), new SeesawAction(new Barcode(19)));
				put(new Barcode(21), new SeesawAction(new Barcode(21)));
			}
		};

		@Override
		public IAction getAction(Barcode barcode) {
			if(barcodeTypeMapping.containsKey(barcode)){
				return barcodeTypeMapping.get(barcode);
			}
			return new NoAction();
		}
		
	}
	
	@Override
	public Tile nextTile(Tile currentTile) {
		Tile nextTile = super.nextTile(currentTile);
		if(nextTile != null){
			return nextTile;
		}
		return getSuperControlMode().arrangeNextMode().nextTile(currentTile);
	}
	
	/*
	 * Utilities
	 */
	
	public void setSeesawWalls() {
		log("Seesaw on next tiles, set seesaw and barcode");

		IMaze maze = getMaze();

		Tile currentTile = getDriver().getCurrentTile();
		Tile nextTile = getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);
		TileShape tileShape = new TileShape(TileType.STRAIGHT, orientation);

		Barcode seesawBarcode = currentTile.getBarcode();
		Barcode otherBarcode = Seesaw.getOtherBarcode(seesawBarcode);

		// Seesaw
		LongPoint nextTilePosition = nextTile.getPosition();
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, seesawBarcode);
		maze.setExplored(nextTilePosition);

		// Seesaw
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);

		// Other seesaw barcode
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setBarcode(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);
	}
	
	public void setObjectTile() {
		Tile currentTile = getDriver().getCurrentTile();
		Tile nextTile = getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);

		// Make next tile a dead end
		getMaze().setTileShape(nextTile.getPosition(),
				new TileShape(TileType.DEAD_END, orientation));

		// Mark as explored
		getMaze().setExplored(nextTile.getPosition());

		// Remove both tiles from the queue

	}
	
	public void objectFound(int teamNumber) {
		log("Own object found, join team #" + teamNumber);
		// Report object found
		((GameRunner) getSuperControlMode().getCommander()).getGame().objectFound();
		// Join team
		((GameRunner) getSuperControlMode().getCommander()).getGame().joinTeam(teamNumber);
		// TODO Start working together
	}

}
