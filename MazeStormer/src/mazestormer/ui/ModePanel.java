package mazestormer.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of control panels for the main control.
 * 
 * @version
 * @author 	Team Bronze
 *
 */
public abstract class ModePanel extends JPanel{
	
	private static final long serialVersionUID = 1856901269027189707L;
	
	private MainControl mc;
	
	@Model @Raw
	protected ModePanel(MainControl mainControl) throws NullPointerException{
		if(mainControl == null)
			throw new NullPointerException("The given main control may not refer the null reference.");
		setLayout(null);
		setBounds(10, 465, 832, 134);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.mc = mainControl;
	}
	
	MainControl getMainControl(){
		return this.mc;
	}
}
