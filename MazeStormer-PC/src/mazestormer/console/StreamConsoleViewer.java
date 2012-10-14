package mazestormer.console;

import java.io.PrintStream;

import lejos.pc.tools.ConsoleViewerUI;

public class StreamConsoleViewer implements ConsoleViewerUI {

	private PrintStream stream;

	public StreamConsoleViewer(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public void append(String value) {
		stream.print(value);
	}

	@Override
	public void updateLCD(byte[] buffer) {
		// Do nothing
	}

	@Override
	public void setStatus(String msg) {
		// Do nothing
	}

	@Override
	public void logMessage(String msg) {
		stream.println(msg);
	}

	@Override
	public void connectedTo(String name, String address) {
		// Do nothing
	}

}
