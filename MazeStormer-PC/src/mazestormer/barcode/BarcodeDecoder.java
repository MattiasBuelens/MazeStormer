package mazestormer.barcode;

import java.util.List;
import java.util.ArrayList;

public class BarcodeDecoder{
	
	private static final int RANGE = 64;
	private static List<Class<? extends Action>> actions = new ArrayList<Class<? extends Action>>();
	
	static{
		setInitial();
		setActions();
	}
	
	private static List<Class<? extends Action>> getActions(){
		return actions;
	}
	
	
	private static void setInitial(){
		for(int i=0; i<RANGE; i++)
			getActions().add(NoAction.class);
	}
	
	private static void setActions(){
//		getActions().set(1, NoAction.class);
//		getActions().set(2, NoAction.class);
//		getActions().set(3, NoAction.class);
//		getActions().set(4, NoAction.class);
//		getActions().set(5, NoAction.class);
//		getActions().set(6, NoAction.class);
//		getActions().set(7, NoAction.class);
//		getActions().set(9, NoAction.class);
//		getActions().set(10, NoAction.class);
//		getActions().set(11, NoAction.class);
//		getActions().set(13, NoAction.class);
//		getActions().set(14, NoAction.class);
//		getActions().set(15, NoAction.class);
//		getActions().set(17, NoAction.class);
//		getActions().set(19, NoAction.class);
//		getActions().set(21, NoAction.class);
//		getActions().set(22, NoAction.class);
//		getActions().set(23, NoAction.class);
//		getActions().set(25, NoAction.class);
//		getActions().set(27, NoAction.class);
//		getActions().set(29, NoAction.class);
//		getActions().set(31, NoAction.class);
//		getActions().set(35, NoAction.class);
//		getActions().set(37, NoAction.class);
//		getActions().set(39, NoAction.class);
//		getActions().set(43, NoAction.class);
//		getActions().set(47, NoAction.class);
//		getActions().set(55, NoAction.class);
	}
	
	public static int toInt(byte b) {
	    return ((Byte) b).intValue();
	}
	
	public static Class<? extends Action> getActionClass(byte b)
			throws IllegalArgumentException{
		int i = toInt(b);
		if(0>i || i>=getActions().size())
			throw new IllegalArgumentException("The given byte doesn't correspond to an existing action.");
		return getActions().get(i);
	}
}
