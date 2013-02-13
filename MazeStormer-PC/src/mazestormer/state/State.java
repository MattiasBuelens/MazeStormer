package mazestormer.state;

public interface State {

	/**
	 * Enter this state.
	 */
	public void enter();
	
	/**
	 * Exit this state.
	 */
	public void exit();
	
	/**
	 * Pause this state, leaving the robot idle.
	 */
	public void pause();
	
	/**
	 * Resume from a paused state.
	 */
	public void resume();

}
