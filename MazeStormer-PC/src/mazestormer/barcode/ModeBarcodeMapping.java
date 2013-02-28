package mazestormer.barcode;

public enum ModeBarcodeMapping {
	
	TeamTreasureTrek{
		
		public IAction[] getActions() {
			return actions.clone();
		}
		
		protected void setActions() {
			actions[0] = new ObjectFoundAction(0,0);
			actions[1] = new ObjectFoundAction(1,0);
			actions[2] = new ObjectFoundAction(2,0);
			actions[3] = new ObjectFoundAction(3,0);
			actions[4] = new ObjectFoundAction(0,1);
			actions[5] = new ObjectFoundAction(1,1);
			actions[6] = new ObjectFoundAction(2,1);
			actions[7] = new ObjectFoundAction(3,1);
			actions[11] = new SeesawAction();
			actions[13] = new SeesawAction();
			actions[15] = new SeesawAction();
			actions[17] = new SeesawAction();
			actions[19] = new SeesawAction();
			actions[21] = new SeesawAction();
			actions[55] = new NoAction();
			actions[47] = new NoAction();
			actions[43] = new NoAction();
			actions[39] = new NoAction();
		}
		
		protected IAction[] actions = new IAction[BarcodeDecoder.RANGE];
		
		{
			setActions();
		}
	},
	
	ExploringTheMaze{
		
		public IAction[] getActions() {
			return actions.clone();
		}
		
		protected void setActions() {
			actions[5] = new RotateCounterClockwiseAction();
			actions[9] = new RotateClockwiseAction();
			actions[13] = new CheckPointAction();
			actions[15] = new SoundAction();
			actions[19] = new WaitAction();
			actions[25] = new LowSpeedAction();
			actions[37] = new HighSpeedAction();
			actions[55] = new GoalAction();
		}
		
		private IAction[] actions = new IAction[BarcodeDecoder.RANGE];
		
		{
			setActions();
		}
	};
	
	public abstract IAction[] getActions();
	
	protected abstract void setActions();
}
