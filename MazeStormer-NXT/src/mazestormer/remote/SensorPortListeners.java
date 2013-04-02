package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;

public final class SensorPortListeners implements SensorPortListener {

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

	private static SensorPortListeners[] portListeners = new SensorPortListeners[SensorPort.NUMBER_OF_PORTS];

	public static SensorPortListeners get(SensorPort port) {
		SensorPortListeners listeners = portListeners[port.getId()];
		if (listeners == null) {
			listeners = new SensorPortListeners(port);
			portListeners[port.getId()] = listeners;
		}
		return listeners;
	}

}
