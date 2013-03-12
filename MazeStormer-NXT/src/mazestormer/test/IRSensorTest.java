package mazestormer.test;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;
import lejos.nxt.addon.IRSeekerV2.Mode;

public class IRSensorTest {

	private IRSeekerV2 ir;
	private volatile boolean isRunning = false;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new IRSensorTest();
	}

	public IRSensorTest() {
		ir = new IRSeekerV2(SensorPort.S3, Mode.AC);

		// Orange button
		Button.ENTER.addButtonListener(new ButtonListener() {

			@Override
			public void buttonPressed(Button b) {
				for (int i = 1; i <= 5; i++) {
					StringBuilder sb = new StringBuilder("");
					LCD.clear(i - 1);
					sb.append("Dir: ");
					sb.append(i);
					sb.append(" - Val: ");
					sb.append(ir.getSensorValue(i));
					LCD.drawString(sb.toString(), 0, i - 1);
				}
			}

			@Override
			public void buttonReleased(Button b) {

			}

		});

		Button.ESCAPE.addButtonListener(new ButtonListener() {

			@Override
			public void buttonPressed(Button b) {
				isRunning = false;
			}

			@Override
			public void buttonReleased(Button b) {

			}

		});

		isRunning = true;
		while (isRunning) {
			Thread.yield();
		}
	}
}
