package mazestormer.remote;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.util.Delay;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LCD.drawString("LCP setup", 0, 0);
		NXTCommConnector connector = Bluetooth.getConnector();
		LCPResponder responder = new LCPResponder(connector);
		responder.start();
		
		Delay.msDelay(5000);

		while (responder.isConnected()) {
			Thread.yield();
		}

		LCD.drawString("LCP exit", 0, 1);
		Button.waitForAnyPress();
		NXT.shutDown();
	}
}
