package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.navigation.Move;

public class MoveReport extends Report<Move> {

	private Move move;

	public MoveReport(ReportType type) {
		super(type);
	}

	public MoveReport(ReportType type, Move move) {
		this(type);
		setMove(move);
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	@Override
	public Move getValue() {
		return getMove();
	}

	@Override
	public void setValue(Move value) {
		setMove(value);
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		// Load move
		Move move = createMove();
		move.loadObject(dis);
		setMove(move);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		// Dump move
		getMove().dumpObject(dos);
	}

	private static final Move createMove() {
		return new Move(0, 0, false);
	}

}
