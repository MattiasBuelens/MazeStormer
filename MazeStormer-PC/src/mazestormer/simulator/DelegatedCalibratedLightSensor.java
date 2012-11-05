package mazestormer.simulator;

import static com.google.common.base.Preconditions.*;

import lejos.robotics.LampLightDetector;
import mazestormer.robot.CalibratedLightSensor;

/**
 * A calibrated light sensor implementation which uses a delegate {@link LampLightDetector}.
 */
public class DelegatedCalibratedLightSensor implements CalibratedLightSensor {

	protected final LampLightDetector delegate;

	private int zero = 0;
	private int hundred = 1023;

	/**
	 * Create a new delegated calibrated light sensor with the given delegate.
	 * 
	 * @param delegate
	 * 			The lamp light detector to use as delegate.
	 */
	public DelegatedCalibratedLightSensor(LampLightDetector delegate) {
		this.delegate = checkNotNull(delegate);
	}

	@Override
	public int getLightValue() {
		if (hundred == zero)
			return 0;
		return 100 * (getNormalizedLightValue() - zero) / (hundred - zero);
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
		zero = getNormalizedLightValue();
	}

	@Override
	public void calibrateHigh() {
		zero = getNormalizedLightValue();
	}

	@Override
	public int getNormalizedLightValue() {
		return delegate.getNormalizedLightValue();
	}

	@Override
	public void setFloodlight(boolean floodlight) {
		delegate.setFloodlight(floodlight);
	}

	@Override
	public boolean isFloodlightOn() {
		return delegate.isFloodlightOn();
	}

	@Override
	public int getFloodlight() {
		return delegate.getFloodlight();
	}

	@Override
	public boolean setFloodlight(int color) {
		return delegate.setFloodlight(color);
	}

}
