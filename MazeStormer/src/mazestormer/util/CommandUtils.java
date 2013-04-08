package mazestormer.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CommandUtils {

	public static float[] readArray(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = dis.readFloat();
		}
		return array;
	}

	public static void writeArray(DataOutputStream dos, float[] array)
			throws IOException {
		dos.writeInt(array.length);
		for (float value : array) {
			dos.writeFloat(value);
		}
	}

}
