package mazestormer;

import lejos.geom.Point;

public class CollisionTest {
	
	public static final float ROBOT_WIDTH = 2;
	public static final float ROBOT_LENGTH = 2;

	public static void main(String[] args) {
		Point center = new Point(0,0);
		float heading = 90.0f;
		
		float distanceToCorners = (float) Math.sqrt(Math.pow(ROBOT_WIDTH/2,2) +
				Math.pow(ROBOT_LENGTH/2,2));
		// the angle between the axle over the width, and the line to the front right corner.
		float angle = (float) Math.atan2(ROBOT_WIDTH/2, ROBOT_LENGTH/2);
		angle = (float) Math.toDegrees(angle);
		
		Point corner1, corner2, corner3, corner4;
		corner1 = center.pointAt(distanceToCorners, heading-90+angle);
		corner2 = center.pointAt(distanceToCorners, heading-90-angle);
		corner3 = center.pointAt(distanceToCorners, heading+90+angle);
		corner4 = center.pointAt(distanceToCorners, heading+90-angle);
		System.out.println(corner1.toString());
		System.out.println(corner2.toString());
		System.out.println(corner3.toString());
		System.out.println(corner4.toString());
	}
}
