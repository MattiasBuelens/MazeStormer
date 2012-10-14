package mazestormer.console;

import java.util.HashSet;
import java.util.Set;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.tools.ConsoleDebug;
import lejos.pc.tools.ConsoleViewComms;
import lejos.pc.tools.ConsoleViewerUI;

public class ConsoleConnector {

	private ConsoleViewComms communicator;
	private Set<ConsoleViewerUI> viewers = new HashSet<ConsoleViewerUI>();
	private Set<ConsoleDebug> debuggers = new HashSet<ConsoleDebug>();

	public ConsoleConnector() {
		communicator = new ConsoleViewComms(new Viewer(), new Debugger(), false);
	}

	public boolean open(String nxt, boolean lcd) {
		return communicator.connectTo(nxt, null, NXTCommFactory.ALL_PROTOCOLS,
				lcd);
	}

	public void close() {
		communicator.close();
	}

	public boolean addViewer(ConsoleViewerUI viewer) {
		return viewers.add(viewer);
	}

	public boolean removeViewer(ConsoleViewerUI viewer) {
		return viewers.remove(viewer);
	}

	public boolean addDebugger(ConsoleDebug debugger) {
		return debuggers.add(debugger);
	}

	public boolean removeDebugger(ConsoleDebug debugger) {
		return debuggers.remove(debugger);
	}

	private class Viewer implements ConsoleViewerUI {

		@Override
		public void append(String value) {
			for (ConsoleViewerUI viewer : viewers) {
				viewer.append(value);
			}
		}

		@Override
		public void updateLCD(byte[] buffer) {
			for (ConsoleViewerUI viewer : viewers) {
				viewer.updateLCD(buffer);
			}
		}

		@Override
		public void setStatus(String msg) {
			for (ConsoleViewerUI viewer : viewers) {
				viewer.setStatus(msg);
			}
		}

		@Override
		public void logMessage(String msg) {
			for (ConsoleViewerUI viewer : viewers) {
				viewer.logMessage(msg);
			}
		}

		@Override
		public void connectedTo(String name, String address) {
			for (ConsoleViewerUI viewer : viewers) {
				viewer.connectedTo(name, address);
			}
		}

	}

	private class Debugger implements ConsoleDebug {

		@Override
		public void exception(int classNo, String msg, int[] stackTrace) {
			for (ConsoleDebug debugger : debuggers) {
				debugger.exception(classNo, msg, stackTrace);
			}
		}

	}

}
