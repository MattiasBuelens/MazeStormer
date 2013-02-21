package mazestormer.remote;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.util.Delay;

public class CompassTest implements Runnable, ButtonListener {

	private CompassHTSensor compass;
	private volatile boolean isRunning = false;

	public static void main(String[] args) throws Exception {
		new CompassTest().run();
	}

	@Override
	public void run() {
		compass = new CompassHTSensor(SensorPort.S3);
		isRunning = true;
		Button.ESCAPE.addButtonListener(this);

		LCD.drawString("ESC to quit", 0, 0);

		while (isRunning) {
			read();
		}

		NXT.shutDown();
	}

	private void read() {
		LCD.clear(1);
		LCD.drawString(compass.getDegrees() + "°", 0, 1);

		Delay.msDelay(1000);
	}

	public void stop() {
		isRunning = false;
	}

	@Override
	public void buttonPressed(Button b) {
		switch (b.getId()) {
		case Button.ID_ESCAPE:
			stop();
			break;
		default:
		}
	}

	@Override
	public void buttonReleased(Button b) {
	}

}
