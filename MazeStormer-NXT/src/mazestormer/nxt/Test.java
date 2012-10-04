package mazestormer.nxt;

import lejos.nxt.Motor;

/**
 * Make the robot dance.
 * 
 * TODO Remove this test class.
 */
public class Test {

	public static void main(String[] args) throws InterruptedException {
		Motor.A.forward();
		Motor.B.backward();
		Thread.sleep(2000);
		Motor.A.stop();
		Motor.B.stop();
		Thread.sleep(500);
		Motor.A.backward();
		Motor.B.forward();
		Thread.sleep(2000);
		Motor.A.stop();
		Motor.B.stop();
		Thread.sleep(500);
	}

}
