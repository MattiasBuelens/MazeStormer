package mazestormer.ui;

import javax.swing.JPanel;

/**
 * A class of control panels for the main control.
 * 
 * @version
 * @author 	Team Bronze
 *
 */
public class ConsolePanel extends JPanel{
	
	private static final long serialVersionUID = 1856901269027189707L;
	
	private MainControl mc;
	
	public ConsolePanel(MainControl mainControl) throws NullPointerException{
		//if(mainControl == null)
		//	throw new NullPointerException("The given main control may not refer the null reference.");
		this.mc = mainControl;
	}
	
	protected MainControl getMainControl(){
		return this.mc;
	}
}
