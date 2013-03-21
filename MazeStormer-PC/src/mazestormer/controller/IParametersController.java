package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IParametersController extends EventSource {

	public double getTravelSpeed();

	public double getMaxTravelSpeed();

	public void setTravelSpeed(double travelSpeed);

	public double getRotateSpeed();

	public double getMaxRotateSpeed();

	public void setRotateSpeed(double rotateSpeed);

}
