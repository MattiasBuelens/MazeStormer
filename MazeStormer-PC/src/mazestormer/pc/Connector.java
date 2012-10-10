package mazestormer.pc;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

public class Connector {

	private static NXTComm comm;
	private static NXTCommand command;

	public static boolean connectTo(String nxt) {
		// Search for NXT by name and connect over LCP
		NXTConnector conn = new NXTConnector();
		boolean isConnected = conn.connectTo(nxt, null,
				NXTCommFactory.ALL_PROTOCOLS, NXTComm.LCP);
		if (!isConnected)
			return false;

		// Set up command connector
		comm = conn.getNXTComm();
		command = new NXTCommand(comm);
		NXTCommandConnector.setNXTCommand(command);
		return true;
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
