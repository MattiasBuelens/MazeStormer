package mazestormer.barcode;

import mazestormer.robot.Robot;

public class NoAction implements IAction{
	
	private static NoAction instance = new NoAction();
	
	private NoAction(){
		
	}
	
	public static IAction getInstance(){
		return instance;
	}

	@Override
	public void performAction(Robot robot) throws IllegalStateException{
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
	}
}
