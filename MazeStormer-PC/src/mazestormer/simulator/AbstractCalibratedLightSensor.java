package mazestormer.simulator;

import mazestormer.robot.CalibratedLightSensor;

/**
 * A abstract implementation of a calibrated light sensor which relies only on
 * {@link #getNormalizedLightValue()}.
 */
public abstract class AbstractCalibratedLightSensor implements
		CalibratedLightSensor {

	private int zero = 0;
	private int hundred = 1023;

	@Override
	public int getLightValue() {
		return getLightValue(getNormalizedLightValue());
	}

	@Override
	public int getNormalizedLightValue(int lightValue) {
		if (hundred == zero)
			return zero;
		return (int) ((lightValue / 100f) * (hundred - zero) + zero);
	}

	@Override
	public int getLightValue(int normalizedLightValue) {
		if (hundred == zero)
			return 0;
		return 100 * (normalizedLightValue - zero) / (hundred - zero);
	}

	@Override
	public int getLow() {
		return zero;
	}

	@Override
	public int getHigh() {
		return hundred;
	}

	@Override
	public void setLow(int low) {
		zero = low;
	}

	@Override
	public void setHigh(int high) {
		hundred = high;
	}

	@Override
	public void calibrateLow() {
		setLow(getNormalizedLightValue());
	}

	@Override
	public void calibrateHigh() {
		setHigh(getNormalizedLightValue());
	}

}
