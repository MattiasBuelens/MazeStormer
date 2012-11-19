package mazestormer.controller;

import mazestormer.barcode.IAction;
import mazestormer.robot.Robot;


public class BarcodeController extends SubController implements IBarcodeController{

	public BarcodeController(MainController mainController){
		super(mainController);
	}

	private ActionRunner runner;

	private Robot getRobot(){
		return getMainController().getRobot();
	}

	@Override
	public void startAction(String action){
		this.runner = new ActionRunner(getAction(action));
		this.runner.start();
	}

	@Override
	public void stopAction(){
		if(this.runner != null){
			this.runner.stop();
			this.runner = null;
		}
	}
	
	private static IAction getAction(String action){
		if(ACTIONS[0].equals(action))
			return new mazestormer.barcode.SoundAction();
		if(ACTIONS[1].equals(action))
			return new mazestormer.barcode.RotateClockwiseAction();
		if(ACTIONS[2].equals(action))
			return new mazestormer.barcode.RotateCounterClockwiseAction();		
		if(ACTIONS[3].equals(action))
			return new mazestormer.barcode.HighSpeedAction();			
		if(ACTIONS[4].equals(action))
			return new mazestormer.barcode.LowSpeedAction();	
		if(ACTIONS[5].equals(action))
			return new mazestormer.barcode.WaitAction();
		return mazestormer.barcode.NoAction.getInstance();
	}

	private void postState(EventType eventType){
		postEvent(new ActionEvent(eventType));
	}

	private class ActionRunner implements Runnable{

		private final Robot robot;
		private boolean isRunning = false;
		private IAction action;

		public ActionRunner(IAction action){
			this.robot = getRobot();
			this.action = action;
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop(){
			if(isRunning()){
				this.isRunning = false;
				this.robot.getPilot().stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		@Override
		public void run(){
			this.action.performAction(this.robot);
			stop();
		}

	}

}
