package mazestormer.ui;

import javax.swing.JTabbedPane;

import mazestormer.controller.ViewController;

public abstract class PlotPanel extends JTabbedPane{
	
	private static final long serialVersionUID = 205654497504294365L;
	
	private ViewController vc;
	
	protected PlotPanel(ViewController vc) throws NullPointerException{
		if(vc == null)
			throw new NullPointerException("The given view controller may not refer the null reference.");
		this.vc = vc;
	}
	
	protected ViewController getViewController(){
		return this.vc;
	}

}
