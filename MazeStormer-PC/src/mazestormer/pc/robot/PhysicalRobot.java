package mazestormer.pc.robot;

public class PhysicalRobot implements Robot {

	@Override
	public void moveForward(long distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveBackward(long distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnClockwise(float angle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnCounterClockwise(float angle) {
		turnClockwise(-angle);
	}

}
