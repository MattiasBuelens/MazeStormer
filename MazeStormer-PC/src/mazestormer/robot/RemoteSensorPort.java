package mazestormer.robot;

import java.io.IOException;

import lejos.nxt.I2CPort;
import lejos.nxt.LegacySensorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.remote.InputValues;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.NXTProtocol;
import lejos.pc.comm.NXTCommandConnector;

public class RemoteSensorPort implements NXTProtocol, LegacySensorPort, I2CPort {

	private final NXTCommand nxtCommand;
	private final int id;

	public RemoteSensorPort(NXTCommand nxtCommand, int id) {
		this.nxtCommand = nxtCommand;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static RemoteSensorPort get(int port) {
		return new RemoteSensorPort(NXTCommandConnector.getSingletonOpen(),
				port);
	}

	public void setTypeAndMode(int type, int mode) {
		try {
			nxtCommand.setInputMode(id, type, mode);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public void setType(int type) {
		int mode = getMode();
		try {
			nxtCommand.setInputMode(id, type, mode);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public void setMode(int mode) {
		int type = getType();
		try {
			nxtCommand.setInputMode(id, type, mode);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public int getType() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}
		return vals.sensorType;
	}

	public int getMode() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}
		return vals.sensorMode;
	}

	/**
	 * Reads the boolean value of the sensor.
	 * 
	 * @return Boolean value of sensor.
	 */
	public boolean readBooleanValue() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return false;
		}
		return (vals.rawADValue < 600);
	}

	/**
	 * Reads the raw value of the sensor.
	 * 
	 * @return Raw sensor value. Range is device dependent.
	 */
	public int readRawValue() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 1023;
		}
		return vals.rawADValue;
	}

	/**
	 * Reads the normalized value of the sensor.
	 * 
	 * @return Normalized value. 0 to 1023
	 */
	public int readNormalizedValue() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}
		return vals.normalizedADValue;
	}

	/**
	 * Returns scaled value, depending on mode of sensor. e.g. BOOLEANMODE
	 * returns 0 or 1. e.g. PCTFULLSCALE returns 0 to 100.
	 * 
	 * @return the value
	 * @see SensorPort#setTypeAndMode(int, int)
	 */
	public int readValue() {
		InputValues vals;
		try {
			vals = nxtCommand.getInputValues(id);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return 0;
		}
		return vals.scaledValue;
	}

	/**
	 * Activate an RCX Light Sensor
	 */
	public void activate() {
		setType(REFLECTION);
	}

	/**
	 * Passivate an RCX Light Sensor
	 */
	public void passivate() {
		setType(NO_SENSOR);
	}

	/**
	 * Return a variable number of sensor values. NOTE: Currently there is no
	 * way to return multiple results from a remote sensor, so we return an
	 * error.
	 * 
	 * @param values
	 *            An array in which to return the sensor values.
	 * @return The number of values returned.
	 */
	public int readValues(int[] values) {
		return -1;
	}

	/**
	 * Return a variable number of raw sensor values NOTE: Currently there is no
	 * way to return multiple results from a remote sensor, so we return an
	 * error.
	 * 
	 * @param values
	 *            An array in which to return the sensor values.
	 * @return The number of values returned.
	 */
	public int readRawValues(int[] values) {
		return -1;
	}

	public void enableColorSensor() {

	}

}
