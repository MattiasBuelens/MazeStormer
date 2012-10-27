package mazestormer.barcode;

import mazestormer.robot.Robot;
import static com.google.common.base.Preconditions.*;

public class NoAction implements Action {

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		checkNotNull(robot);
	}
}
