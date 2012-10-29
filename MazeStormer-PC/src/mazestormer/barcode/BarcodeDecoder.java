package mazestormer.barcode;

public class BarcodeDecoder{
	
	private static final int RANGE = 64;
	private static IAction[] actions = new IAction[RANGE];
	
	static{
		setActions();
	}
	
	private static void setActions(){
		// temporary
		// getActions()[1] = new NoAction();
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
