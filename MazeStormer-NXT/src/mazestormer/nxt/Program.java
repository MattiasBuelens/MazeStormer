package mazestormer.nxt;

import lejos.nxt.comm.RConsole;

public class Program {

	public static void main(String[] args) {
		// Initiate remote console
		RConsole.openAny(5000);
		if (!RConsole.isOpen())
			return;

		// Do stuff here
		// You can send log messages such as:
		RConsole.println("Hello from the NXT!");
		
		RConsole.close();

	}

}
