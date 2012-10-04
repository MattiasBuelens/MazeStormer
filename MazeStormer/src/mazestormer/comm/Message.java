package mazestormer.comm;

import java.util.HashMap;
import java.util.Map;

public enum Message {

	/*
	 * Left motor
	 */

	/**
	 * (out) Turn left motor off.
	 */
	LEFT_MOTOR_OFF((byte) 0x10),
	/**
	 * (out) Turn left motor on.
	 */
	LEFT_MOTOR_ON((byte) 0x11),
	/**
	 * (out) Set speed of left motor.
	 */
	LEFT_MOTOR_SET_SPEED((byte) 0x12),

	/*
	 * Right motor
	 */

	/**
	 * (out) Turn right motor off.
	 */
	RIGHT_MOTOR_OFF((byte) 0x20),
	/**
	 * (out) Turn right motor on.
	 */
	RIGHT_MOTOR_ON((byte) 0x21),
	/**
	 * (out) Set speed of right motor.
	 */
	RIGHT_MOTOR_SET_SPEED((byte) 0x22);

	// /*
	// * Ultrasonic sensor
	// */
	//
	// /**
	// * (out) Rotate ultrasonic sensor.
	// */
	// SENSOR_ROTATE((byte) 0x30),
	// /**
	// * (out) Get the distance to the nearest object from the ultrasonic
	// sensor.
	// */
	// SENSOR_GET_DISTANCE((byte) 0x31),
	// /**
	// * (in) Report the distance to the nearest object from the ultrasonic
	// * sensor.
	// */
	// SENSOR_REPORT_DISTANCE((byte) 0x32),
	//
	// /*
	// * Barcode reader
	// */
	//
	// /**
	// * (in) Report the read barcode.
	// */
	// BARCODE_REPORT((byte) 0x40);

	private byte code;

	private static Map<Byte, Message> byCode = new HashMap<Byte, Message>();

	private Message(byte code) {
		this.code = code;
		storeByCode(this);
	}

	public byte getCode() {
		return code;
	}

	public static Message byCode(byte code) {
		return byCode.get(code);
	}

	private static void storeByCode(Message message) {
		byCode.put(message.getCode(), message);
	}
}
