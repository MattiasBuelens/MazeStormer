package mazestormer.barcode;

public class BarcodeDecoder{
	
	private static final int RANGE = 64;
	private static IAction[] actions = new IAction[RANGE];
	
	static{
		setActions();
	}
	
	private static void setActions(){
		getActions()[5] = new RotateCounterClockwiseAction();
		getActions()[9] = new RotateClockwiseAction();
		getActions()[15] = new SoundAction();
		getActions()[19] = new WaitAction();
		getActions()[25] = new LowSpeedAction();
		getActions()[37] = new HighSpeedAction();
		// TODO
		getActions()[55] = NoAction.getInstance();
	}
	
	public static IAction[] getActions(){
		return actions.clone();
	}
	
	private static IAction getActionAt(int index)
			throws IndexOutOfBoundsException{
		if(index<0 || index>= RANGE)
			return NoAction.getInstance();
		if(actions[index] == null)
			return NoAction.getInstance();
		return actions[index];
	}
	
	public static IAction getAction(byte barcode){
		return getActionAt(((Byte) barcode).intValue());
	}
}
