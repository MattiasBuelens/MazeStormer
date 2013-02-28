package mazestormer.barcode;

public enum ModeBarcodeMapping {
	
	TeamTreasureTrek{
		
		public IAction[] getActions() {
			return actions.clone();
		}
		
		protected void setActions() {
			actions[0] = new NoAction();
			actions[1] = new NoAction();
			actions[2] = new NoAction();
			actions[3] = new NoAction();
			actions[4] = new NoAction();
			actions[5] = new NoAction();
			actions[6] = new NoAction();
			actions[7] = new NoAction();
			actions[11] = new NoAction();
			actions[13] = new NoAction();
			actions[15] = new NoAction();
			actions[17] = new NoAction();
			actions[19] = new NoAction();
			actions[21] = new NoAction();
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
