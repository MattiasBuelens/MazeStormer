package mazestormer;

import lejos.nxt.*;

public class Light extends Thread{
	
	 private LightSensor ls;
	    private int reading;
	    public Light()
	    {
	        ls = new LightSensor( SensorPort.S1 );
	    }
	    public void run()
	    {
	        do {
	            reading = ls.readValue();
	        } while (!isInterrupted());
	    }
	    public int getReading()
	    {
	        return reading;
	    }

}
