package mazestormer.controller;

import mazestormer.robot.Robot;

public class PathFindingController extends SubController implements IPathFindingController{

	public PathFindingController(MainController mainController){
		super(mainController);
	}

	private Robot getRobot(){
		return getMainController().getRobot();
	}

	@Override
	public void startAction(String action){
		
	}

	@Override
	public void stopAction(){
		
	}

}
