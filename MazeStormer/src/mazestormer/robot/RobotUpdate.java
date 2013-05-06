package mazestormer.robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.Transmittable;
import lejos.robotics.navigation.Move;

public class RobotUpdate implements Transmittable {

	private Move movement;
	private int lightValue;
	private float infraredAngle;

	public RobotUpdate() {
		this(null, -1, Float.NaN);
	}

	public RobotUpdate(Move movement, int lightValue, float infraredAngle) {
		setMovement(movement);
		setLightValue(lightValue);
		setInfraredAngle(infraredAngle);
	}

	public final boolean hasMovement() {
		return movement != null;
	}

	public final Move getMovement() {
		if (!hasMovement()) {
			throw new IllegalStateException("No movement update available.");
		}
		return movement;
	}

	public final void setMovement(Move movement) {
		this.movement = movement;
	}

	public final int getLightValue() {
		return lightValue;
	}

	public final void setLightValue(int lightValue) {
		this.lightValue = lightValue;
	}

	public final float getInfraredAngle() {
		return infraredAngle;
	}

	public final void setInfraredAngle(float infraredAngle) {
		this.infraredAngle = infraredAngle;
	}

	@Override
	public void dumpObject(DataOutputStream dos) throws IOException {
		dos.writeBoolean(hasMovement());
		if (hasMovement()) {
			getMovement().dumpObject(dos);
		}
		dos.writeInt(getLightValue());
		dos.writeFloat(getInfraredAngle());
	}

	@Override
	public void loadObject(DataInputStream dis) throws IOException {
		if (dis.readBoolean()) {
			Move movement = createMove();
			movement.loadObject(dis);
			setMovement(movement);
		}
		setLightValue(dis.readInt());
		setInfraredAngle(dis.readFloat());
	}

	public static RobotUpdate create(ControllableRobot robot) {
		// Movement
		Move movement = null;
		if (robot.getPilot().isMoving()) {
			movement = robot.getPilot().getMovement();
		}

		// Sensor readings
		int lightValue = -1;
		if (robot.getLightSensor() != null) {
			lightValue = robot.getLightSensor().getLightValue();
		}
		float infraredAngle = Float.NaN;
		if (robot.getIRSensor() != null) {
			// infraredAngle = robot.getIRSensor().getAngle();
		}

		return new RobotUpdate(movement, lightValue, infraredAngle);
	}

	private static Move createMove() {
		return new Move(0, 0, false);
	}

}
