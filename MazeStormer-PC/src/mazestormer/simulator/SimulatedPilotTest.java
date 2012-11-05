package mazestormer.simulator;

import static org.junit.Assert.*;
import lejos.geom.Point;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.Pilot;

import org.junit.Before;
import org.junit.Test;

public class SimulatedPilotTest {

	private SimulatedPilot pilot;
	private PoseProvider tracker;

	private static final double travelSpeed = 100d; // cm/sec
	private static final double rotateSpeed = 360d; // degrees/sec

	private static final double timeDelta = 25d; // ms
	private static final double distanceDelta = 0.1d; // cm
	private static final double angleDelta = 0.5d; // degrees

	@Before
	public void setUp() throws Exception {
		pilot = new SimulatedPilot(Pilot.leftWheelDiameter,
				Pilot.rightWheelDiameter, Pilot.trackWidth);
		pilot.setTravelSpeed(travelSpeed);
		pilot.setRotateSpeed(rotateSpeed);
		tracker = new OdometryPoseProvider(pilot);
	}

	@Test
	public void travelForward() {
		float expectedDistance = 10; // cm
		double expectedDuration = getExceptedTravelDuration(expectedDistance);
		long start = System.currentTimeMillis();

		// Travel forward
		pilot.travel(expectedDistance);

		long duration = System.currentTimeMillis() - start;
		float distance = tracker.getPose().getX();

		assertEquals(expectedDuration, duration, timeDelta);
		assertEquals(expectedDistance, distance, distanceDelta);
	}

	@Test
	public void travelBackward() {
		float expectedDistance = -10; // cm
		double expectedDuration = getExceptedTravelDuration(expectedDistance);
		long start = System.currentTimeMillis();

		// Travel backward
		pilot.travel(expectedDistance);

		long duration = System.currentTimeMillis() - start;
		float distance = tracker.getPose().getX();

		assertEquals(expectedDuration, duration, timeDelta);
		assertEquals(expectedDistance, distance, distanceDelta);
	}

	@Test
	public void travelStop() throws InterruptedException {
		float targetDistance = 10; // cm
		float expectedDistance = 5; // cm
		double expectedDuration = getExceptedTravelDuration(expectedDistance);

		long start = System.currentTimeMillis();

		// Travel forward
		pilot.travel(targetDistance, true);
		assertTrue(pilot.isMoving());

		// Wait
		Thread.sleep((long) expectedDuration);
		assertTrue(pilot.isMoving());

		// Stop while moving
		pilot.stop();

		long duration = System.currentTimeMillis() - start;
		float distance = tracker.getPose().getX();
		assertFalse(pilot.isMoving());

		assertEquals(expectedDuration, duration, timeDelta);
		assertEquals(expectedDistance, distance, timeDelta * distanceDelta);
	}

	@Test
	public void rotateLeft() {
		float expectedAngle = 30; // degrees
		double expectedDuration = getExceptedRotateDuration(expectedAngle);
		long start = System.currentTimeMillis();

		// Rotate counter-clockwise
		pilot.rotate(expectedAngle);

		long duration = System.currentTimeMillis() - start;
		float angle = tracker.getPose().getHeading();

		assertEquals(expectedDuration, duration, timeDelta);
		assertEquals(expectedAngle, angle, angleDelta);
	}

	@Test
	public void rotateRight() {
		float expectedAngle = -30; // degrees
		double expectedDuration = getExceptedRotateDuration(expectedAngle);
		long start = System.currentTimeMillis();

		// Rotate clockwise
		pilot.rotate(expectedAngle);

		long duration = System.currentTimeMillis() - start;
		float angle = tracker.getPose().getHeading();

		assertEquals(expectedDuration, duration, timeDelta);
		assertEquals(expectedAngle, angle, angleDelta);
	}

	@Test
	public void square() {
		polygon(4, 0.5f);
	}

	@Test
	public void pentagon() {
		polygon(5, 0.5f);
	}

	private void polygon(int sides, float polygonSide) {
		float polygonAngle = 360f / ((float) sides); // degrees
		double expectedDuration = sides
				* (getExceptedRotateDuration(polygonSide) + getExceptedRotateDuration(polygonAngle));
		long start = System.currentTimeMillis();

		// Expect return at origin
		float expectedDistance = 0f;
		float expectedAngle = 0f;

		// Travel along a square
		for (int i = 1; i <= sides; ++i) {
			pilot.travel(polygonSide);
			pilot.rotate(polygonAngle);
		}

		long duration = System.currentTimeMillis() - start;
		float distance = tracker.getPose().distanceTo(new Point(0, 0));
		float angle = tracker.getPose().getHeading();

		assertEquals(expectedDuration, duration, sides * timeDelta);
		assertEquals(expectedDistance, distance, distanceDelta);
		assertEquals(expectedAngle, angle, angleDelta);
	}

	private double getExceptedTravelDuration(double distance) {
		return Math.abs(distance) / pilot.getTravelSpeed() * 1000d;
	}

	private double getExceptedRotateDuration(double angle) {
		return Math.abs(angle) / pilot.getRotateSpeed() * 1000d;
	}

}
