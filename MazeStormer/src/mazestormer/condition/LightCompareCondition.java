package mazestormer.condition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightCompareCondition extends Condition {

	private int threshold;

	public LightCompareCondition(ConditionType type) {
		super(type);
	}

	public LightCompareCondition(ConditionType type, int threshold) {
		this(type);
		setThreshold(threshold);
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setThreshold(dis.readInt());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(getThreshold());
	}

}
