package mazestormer.remote;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.comm.LCPBTResponder;
import lejos.nxt.comm.LCPResponder;

public class Program {

	public static void main(String[] args) throws InterruptedException {
		LCD.drawString("LCP setup", 0, 0);
		LCPResponder responder = new LCPBTResponder();
		responder.start();
		responder.join(10000);

		LCD.drawString("LCP exit", 0, 1);
		Button.waitForAnyPress();
		NXT.shutDown();
	}
}
