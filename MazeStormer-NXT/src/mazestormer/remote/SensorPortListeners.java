package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;

public class SensorPortListeners implements SensorPortListener {

	private final SensorPort port;
	private List<SensorPortListener> listeners = new ArrayList<SensorPortListener>();

	private SensorPortListeners(SensorPort port) {
		this.port = port;
		port.addSensorPortListener(this);
	}

	public SensorPort getPort() {
		return port;
	}

	public synchronized void addSensorPortListener(SensorPortListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeSensorPortListener(
			SensorPortListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
		for (SensorPortListener listener : listeners) {
			listener.stateChanged(aSource, aOldValue, aNewValue);
		}
	}

	public static SensorPortListeners S1 = new SensorPortListeners(
			SensorPort.S1);
	public static SensorPortListeners S2 = new SensorPortListeners(
			SensorPort.S2);
	public static SensorPortListeners S3 = new SensorPortListeners(
			SensorPort.S3);
	public static SensorPortListeners S4 = new SensorPortListeners(
			SensorPort.S4);

	public static SensorPortListeners get(SensorPort port) {
		switch (port.getId()) {
		case 0:
			return S1;
		case 1:
			return S2;
		case 2:
			return S3;
		case 3:
			return S4;
		default:
			return null;
		}
	}

}
