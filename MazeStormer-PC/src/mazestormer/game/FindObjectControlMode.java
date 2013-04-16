package mazestormer.game;

import java.util.HashMap;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.barcode.SeesawAction;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.Tile;
import mazestormer.player.Player;

public class FindObjectControlMode extends ControlMode{

	public FindObjectControlMode(Player player) {
		super(player, new FindObjectBarcodeMapping());
		// TODO Auto-generated constructor stub
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static class FindObjectBarcodeMapping implements BarcodeMapping{

		private static final Map<Barcode,Class<?>> barcodeTypeMapping = new HashMap<Barcode, Class<?>>(){
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(0), ObjectFoundAction.class);
				put(new Barcode(1), ObjectFoundAction.class);
				put(new Barcode(2), ObjectFoundAction.class);
				put(new Barcode(3), ObjectFoundAction.class);
				put(new Barcode(4), ObjectFoundAction.class);
				put(new Barcode(5), ObjectFoundAction.class);
				put(new Barcode(6), ObjectFoundAction.class);
				put(new Barcode(7), ObjectFoundAction.class);
				put(new Barcode(11), SeesawAction.class);
				put(new Barcode(13), SeesawAction.class);
				put(new Barcode(15), SeesawAction.class);
				put(new Barcode(17), SeesawAction.class);
				put(new Barcode(19), SeesawAction.class);
				put(new Barcode(21), SeesawAction.class);
			}
		};
		
		@Override
		public IAction getAction(Barcode barcode) {
			Class<?> foundBarcodeType = barcodeTypeMapping.get(barcode);
			// objectbarcode
			if(foundBarcodeType.equals(ObjectFoundAction.class)) {
				if(getObjectNumberFromBarcode(barcode).equals(getCommander().getObjectNumber())){
					// indien eigen barcode: return ObjectBarcodeAction;
					return new ObjectFoundAction(); // eigen voorwerp wordt opgepikt
				}
					// verwijder volgende tegels uit queue
					
				// voeg info toe aan maze
			}
			// seesawBarcode
			else if(foundBarcodeType.equals(SeesawAction.class))
			{
					// voeg info toe aan maze
					// andere nog te exploreren tegels?
			
					// ja
						// return noAction;
							// driver zal gewoon verder exploreren
			
					// nee
						// is wip open?
						
							// ja
								// return seesawBarcode; // de seesaw wordt overgestoken en van daar wordt verder geëxploreerd
							
							// nee
								// zijn er andere wippen beschikbaar?

									// ja
										// rijd naar de volgende wip zijn barcode-tegel
						
									// nee
										// rijd naar een T of Cross -stuk en wacht tot er iemand passeert

								// return null;
			}
			return null;
		}
		
		
		private static int getObjectNumberFromBarcode(Barcode objectBarcode){
			return (objectBarcode.getValue() % 4);
		}
		
		private static int getTeamNumberFromBarcode(Barcode objectBarcode){
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}
	}

}
