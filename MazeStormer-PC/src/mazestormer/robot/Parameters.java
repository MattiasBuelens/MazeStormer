package mazestormer.robot;

public class Parameters {
	private final double travelSpeed;
	private final double rotateSpeed;

	public Parameters(double travelSpeed, double rotateSpeed) {
		assert travelSpeed > 0;
		assert rotateSpeed > 0;

		this.travelSpeed = travelSpeed;
		this.rotateSpeed = rotateSpeed;
	}

	public double getTravelSpeed() {
		return travelSpeed;
	}

	public double getRotateSpeed() {
		return rotateSpeed;
	}
}
