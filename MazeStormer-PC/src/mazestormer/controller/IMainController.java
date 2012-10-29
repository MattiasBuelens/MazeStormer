package mazestormer.controller;

import lejos.robotics.navigation.Pose;
import mazestormer.util.EventSource;

public interface IMainController {

	public IConfigurationController configuration();

	public IParametersController parameters();

	public IManualControlController manualControl();

	public IPolygonControlController polygonControl();
	
	public IBarcodeController barcodeControl();

	public IMapController map();

	public ILogController log();

	public IStateController state();

	public void register(EventSource eventSource);

	public Pose getPose();

}
