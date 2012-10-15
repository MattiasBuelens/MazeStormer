package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import mazestormer.ui.event.RobotParameterChangeRequest;

public interface IParametersController {

	@Subscribe
	public void onParameterChangeRequest(RobotParameterChangeRequest e);

}
