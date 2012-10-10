package mazestormer.nxt;

import mazestormer.nxt.model.Communicator;

public class Program {

	public static void main(String[] args) {
		// Start communicator
		Communicator communicator = new Communicator();
		communicator.startListening();

		// Close connection
		// connection.close();
	}

}
