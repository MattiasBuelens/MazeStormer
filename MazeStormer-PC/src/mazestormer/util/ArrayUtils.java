package mazestormer.util;

import java.lang.reflect.Array;

public class ArrayUtils {

	public static <T> T[] concat(T[] a, T[] b) {
	   int la = a.length;
	   int lb = b.length;
	   @SuppressWarnings("unchecked")
	   T[] result = (T[]) Array.newInstance(a.getClass().getComponentType(), la + lb);
	   System.arraycopy(a, 0, result, 0, la);
	   System.arraycopy(b, 0, result, la, lb);
	   return result;
	}
}
