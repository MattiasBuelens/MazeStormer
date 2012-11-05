package mazestormer;

public class CrossTest {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		double sensorToWheels = 7.2;
		
		double angle1 = 351.00787353515625;
		double angle2 = 302.7168884277344;
		
		double radAngle1 = Math.toRadians(angle1);
		double radAngle2 = Math.toRadians(angle2);
		
		double xp = 0;
		double yp = sensorToWheels;
		
		double x0 = -sensorToWheels*Math.sin(radAngle1);
		double y0 = sensorToWheels*Math.cos(radAngle1);
		
		System.out.println("x0: " + x0);
		System.out.println("y0: " + y0);
		
		double x1 = -sensorToWheels*Math.sin(radAngle1-radAngle2);
		double y1 = sensorToWheels*Math.cos(radAngle1-radAngle2);

		System.out.println("x1: " + x1);
		System.out.println("y1: " + y1);
		
		double lambda=((x1-x0)*(xp-x0)+(y1-y0)*(yp-y0))/
				(Math.pow((x1-x0),2)+Math.pow((y1-y0),2));
		
		double afstand=Math.sqrt(Math.pow(xp-x0-lambda*(x1-x0),2)+(Math.pow(yp-y0-lambda*(y1-y0),2)));
		System.out.println(afstand);

	}

}
