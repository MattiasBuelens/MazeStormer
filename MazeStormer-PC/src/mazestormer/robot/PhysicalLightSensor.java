package mazestormer.robot;

import lejos.nxt.ADSensorPort;
import lejos.nxt.LightSensor;

public class PhysicalLightSensor extends LightSensor implements
		CalibratedLightSensor {

	public PhysicalLightSensor(ADSensorPort port) {
		super(port);
	}

	public PhysicalLightSensor(ADSensorPort port, boolean floodlight) {
		super(port, floodlight);
	}

	@Override
	public int getNormalizedLightValue(int lightValue) {
		if (getHigh() == getLow())
			return getLow();
		return (int) ((lightValue / 100f) * (getHigh() - getLow()) + getLow());
	}

	@Override
	public int getLightValue(int normalizedLightValue) {
		if (getHigh() == getLow())
			return 0;
		return 100 * (normalizedLightValue - getLow()) / (getHigh() - getLow());
	}

	@Override
	public float getSensorRadius() {
		return Robot.sensorRadius;
	}

}
