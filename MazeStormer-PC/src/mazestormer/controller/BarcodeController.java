package mazestormer.controller;

import mazestormer.barcode.IAction;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;


public class BarcodeController extends SubController implements IBarcodeController{

	public BarcodeController(MainController mainController){
		super(mainController);
	}

	private ActionRunner runner;

	private Pilot getPilot(){
		return getMainController().getPilot();
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
		if("Play sound".equals(action))
			return new mazestormer.barcode.SoundAction();
		if("Rotate 360 degrees clockwise".equals(action))
			return new mazestormer.barcode.RotateClockwiseAction();
		if("Rotate 360 degrees counter-clockwise".equals(action))
			return new mazestormer.barcode.RotateCounterClockwiseAction();		
		if("Travel at high speed".equals(action))
			return new mazestormer.barcode.HighSpeedAction();			
		if("Travel at low speed".equals(action))
			return new mazestormer.barcode.LowSpeedAction();	
		if("Wait for 5 seconds".equals(action))
			return new mazestormer.barcode.WaitAction();
		return mazestormer.barcode.NoAction.getInstance();
	}

	private void postState(EventType eventType){
		postEvent(new ActionEvent(eventType));
	}

	private class ActionRunner implements Runnable{

		private final Pilot pilot;
		private boolean isRunning = false;
		private IAction action;

		public ActionRunner(IAction action){
			this.pilot = getPilot();
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
				this.pilot.stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		@Override
		public void run(){
			this.action.performAction((Robot) this.pilot);
			stop();
		}

	}

}
