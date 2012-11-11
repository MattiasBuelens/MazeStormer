package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.navigation.Move;

public class MoveReport extends Report {

	private Move move;

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	private Move createMove() {
		return new Move(0, 0, false);
	}

	@Override
	public void dumpObject(DataOutputStream dos) throws IOException {
		super.dumpObject(dos);
		// Dump move
		getMove().dumpObject(dos);
	}

	@Override
	public void loadObject(DataInputStream dis) throws IOException {
		super.loadObject(dis);
		// Load move
		Move move = createMove();
		move.loadObject(dis);
		setMove(move);
	}

}
