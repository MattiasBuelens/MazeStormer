package mazestormer.condition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightCompareCondition extends Condition {

	private int minThreshold;
	private int maxThreshold;

	public LightCompareCondition(ConditionType type) {
		super(type);
	}

	public LightCompareCondition(ConditionType type, int threshold) {
		this(type);
		switch (type) {
		case LIGHT_SMALLER_THAN:
			setMinThreshold(0);
			setMaxThreshold(threshold);
			break;
		case LIGHT_GREATER_THAN:
			setMinThreshold(threshold);
			setMaxThreshold(Integer.MAX_VALUE);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public LightCompareCondition(ConditionType type, int minThreshold, int maxThreshold) {
		this(type);
		setMinThreshold(minThreshold);
		setMaxThreshold(maxThreshold);
	}

	public int getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(int minThreshold) {
		this.minThreshold = minThreshold;
	}

	public int getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(int maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setMinThreshold(dis.readInt());
		setMaxThreshold(dis.readInt());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(getMinThreshold());
		dos.writeInt(getMaxThreshold());
	}

}
