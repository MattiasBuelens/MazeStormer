package mazestormer.connect;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

public class Connector {

	private static NXTConnector connector;
	private static NXTComm comm;
	private static NXTCommand command;

	public static boolean connectTo(String nxt) {
		if (connector != null)
			return true;

		// Search for NXT by name and connect over LCP
		NXTConnector conn = new NXTConnector();
		boolean isConnected = conn.connectTo(nxt, null,
				NXTCommFactory.ALL_PROTOCOLS, NXTComm.LCP);
		if (!isConnected)
			return false;

		// Store connector
		connector = conn;

		// Set up command connector
		NXTComm comm = conn.getNXTComm();
		command = new NXTCommand(comm);
		NXTCommandConnector.setNXTCommand(command);
		return true;
	}

	public static NXTConnector getConnector() {
		return connector;
	}

	public static NXTCommand getCommand() {
		return command;
	}

	public static void close() throws IOException {
		if (command != null) {
			command.disconnect();
			command = null;
		}
		if (comm != null) {
			comm.close();
			comm = null;
		}
	}

}
