package mazestormer.maze;

import static org.junit.Assert.*;
import mazestormer.util.LongPoint;

import org.junit.Test;

public class TileTransformTest {

	@Test
	public void identicalTransform() {
		TileTransform identicalTransform = new TileTransform(new LongPoint(0,0), 0);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		
		assertEquals(0, (long) identicalTransform.transform(origin).getX());
		assertEquals(0, (long) identicalTransform.transform(origin).getY());
		
		assertEquals(1, (long) identicalTransform.transform(e_x).getX());
		assertEquals(0, (long) identicalTransform.transform(e_x).getY());
		
		assertEquals(0, (long) identicalTransform.transform(e_y).getX());
		assertEquals(1, (long) identicalTransform.transform(e_y).getY());
	}
	
	@Test
	public void oneRotation() {
		TileTransform transform = new TileTransform(new LongPoint(0,0), 1);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		
		
		assertEquals(0, (long) transform.transform(origin).getX());
		assertEquals(0, (long) transform.transform(origin).getY());
		
		assertEquals(0, (long) transform.transform(e_x).getX());
		assertEquals(1, (long) transform.transform(e_x).getY());
		
		assertEquals(-1, (long) transform.transform(e_y).getX());
		assertEquals(0, (long) transform.transform(e_y).getY());
	}
	
	@Test
	public void twoRotations() {
		TileTransform transform = new TileTransform(new LongPoint(0,0), 2);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		
		
		assertEquals(0, (long) transform.transform(origin).getX());
		assertEquals(0, (long) transform.transform(origin).getY());
		
		assertEquals(-1, (long) transform.transform(e_x).getX());
		assertEquals(0, (long) transform.transform(e_x).getY());
		
		assertEquals(0, (long) transform.transform(e_y).getX());
		assertEquals(-1, (long) transform.transform(e_y).getY());
	}
	
	@Test
	public void translationOver32() {
		TileTransform transform = new TileTransform(new LongPoint(3,2), 0);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		
		
		assertEquals(3, (long) transform.transform(origin).getX());
		assertEquals(2, (long) transform.transform(origin).getY());
		
		assertEquals(4, (long) transform.transform(e_x).getX());
		assertEquals(2, (long) transform.transform(e_x).getY());
		
		assertEquals(3, (long) transform.transform(e_y).getX());
		assertEquals(3, (long) transform.transform(e_y).getY());
	}
	
	@Test
	public void translationOverMinus3Minus2() {
		TileTransform transform = new TileTransform(new LongPoint(-3,-2), 0);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		
		
		assertEquals(-3, (long) transform.transform(origin).getX());
		assertEquals(-2, (long) transform.transform(origin).getY());
		
		assertEquals(-2, (long) transform.transform(e_x).getX());
		assertEquals(-2, (long) transform.transform(e_x).getY());
		
		assertEquals(-3, (long) transform.transform(e_y).getX());
		assertEquals(-1, (long) transform.transform(e_y).getY());
	}
	
	@Test
	public void translationAndRotation() {
		TileTransform transform = new TileTransform(new LongPoint(1,2), 3);
		
		LongPoint origin = new LongPoint(0,0);
		LongPoint e_x = new LongPoint(1,0);
		LongPoint e_y = new LongPoint(0,1);
		LongPoint P = new LongPoint(3,4);
		
		
		assertEquals(1, (long) transform.transform(origin).getX());
		assertEquals(2, (long) transform.transform(origin).getY());
		
		assertEquals(2, (long) transform.transform(e_x).getX());
		assertEquals(0, (long) transform.transform(e_x).getY());
		
		assertEquals(1, (long) transform.transform(e_y).getX());
		assertEquals(-1, (long) transform.transform(e_y).getY());
		
		assertEquals(-2, (long) transform.transform(P).getX());
		assertEquals(2, (long) transform.transform(P).getY());
	}

}
