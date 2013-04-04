package mazestormer.maze;

import static org.junit.Assert.*;
import mazestormer.util.LongPoint;

import org.junit.Test;

public class TileTransformTest {

	@Test
	public void identicalTransform() {
		TileTransform identicalTransform = new TileTransform(new LongPoint(0, 0), 0);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);

		// In relative coordinates
		assertEquals(origin, identicalTransform.inverseTransform(origin));
		assertEquals(e_x, identicalTransform.inverseTransform(e_x));
		assertEquals(e_y, identicalTransform.inverseTransform(e_y));
	}

	@Test
	public void oneRotation() {
		TileTransform transform = new TileTransform(new LongPoint(0, 0), 1);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);

		// In relative coordinates
		assertEquals(new LongPoint(0, 0), transform.inverseTransform(origin));
		assertEquals(new LongPoint(0, -1), transform.inverseTransform(e_x));
		assertEquals(new LongPoint(1, 0), transform.inverseTransform(e_y));
	}

	@Test
	public void twoRotations() {
		TileTransform transform = new TileTransform(new LongPoint(0, 0), 2);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);

		// In relative coordinates
		assertEquals(new LongPoint(0, 0), transform.inverseTransform(origin));
		assertEquals(new LongPoint(-1, 0), transform.inverseTransform(e_x));
		assertEquals(new LongPoint(0, -1), transform.inverseTransform(e_y));
	}

	@Test
	public void translationOver32() {
		TileTransform transform = new TileTransform(new LongPoint(3, 2), 0);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);

		// In relative coordinates
		assertEquals(new LongPoint(-3, -2), transform.inverseTransform(origin));
		assertEquals(new LongPoint(-2, -2), transform.inverseTransform(e_x));
		assertEquals(new LongPoint(-3, -1), transform.inverseTransform(e_y));
	}

	@Test
	public void translationOverMinus3Minus2() {
		TileTransform transform = new TileTransform(new LongPoint(-3, -2), 0);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);

		// In relative coordinates
		assertEquals(new LongPoint(3, 2), transform.inverseTransform(origin));
		assertEquals(new LongPoint(4, 2), transform.inverseTransform(e_x));
		assertEquals(new LongPoint(3, 3), transform.inverseTransform(e_y));
	}

	@Test
	public void translationAndRotation() {
		TileTransform transform = new TileTransform(new LongPoint(1, 2), 3);

		// In world coordinates
		LongPoint origin = new LongPoint(0, 0);
		LongPoint e_x = new LongPoint(1, 0);
		LongPoint e_y = new LongPoint(0, 1);
		LongPoint P = new LongPoint(3, 4);

		// In relative coordinates
		assertEquals(new LongPoint(2, -1), transform.inverseTransform(origin));
		assertEquals(new LongPoint(2, 0), transform.inverseTransform(e_x));
		assertEquals(new LongPoint(1, -1), transform.inverseTransform(e_y));
		assertEquals(new LongPoint(-2, 2), transform.inverseTransform(P));
	}

}
