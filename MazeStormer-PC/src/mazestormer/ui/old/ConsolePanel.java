package mazestormer.ui.old;

import javax.swing.JPanel;

import mazestormer.controller.old.ViewController;

/**
 * A class of console panels.
 * 
 * @version
 * @author 	Team Bronze
 *
 */
public abstract class ConsolePanel extends JPanel{
	
	private static final long serialVersionUID = 1856901269027189707L;
	
	private ViewController vc;
	
	protected ConsolePanel(ViewController vc) throws NullPointerException{
		//if(vc == null)
		//	throw new NullPointerException("The given view controller may not refer the null reference.");
		this.vc = vc;
	}
	
	protected ViewController getViewController(){
		return this.vc;
	}
}
