package mazestormer.remote;

import lejos.nxt.ADSensorPort;
import lejos.nxt.LightSensor;
import mazestormer.robot.CalibratedLightSensor;

public class PhysicalLightSensor extends LightSensor implements CalibratedLightSensor {

	public PhysicalLightSensor(ADSensorPort port) {
		super(port);
	}

	public PhysicalLightSensor(ADSensorPort port, boolean floodlight) {
		super(port, floodlight);
	}

}
