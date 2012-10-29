package mazestormer.barcode;

import mazestormer.robot.Robot;
import static com.google.common.base.Preconditions.*;

public class NoAction implements IAction{
	
	private static NoAction instance = new NoAction();
	
	private NoAction(){
		
	}
	
	public static IAction getInstance(){
		return instance;
	}

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		checkNotNull(robot);
	}
}
