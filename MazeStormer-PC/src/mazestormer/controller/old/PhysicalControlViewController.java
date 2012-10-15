package mazestormer.controller.old;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mazestormer.ui.old.ControlConsolePanel;

/**
 * A class that provides the interaction between a control view
 * with physical interpretation and the corresponding model classes.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class PhysicalControlViewController implements ControlViewController{
	
	private JTabbedPane plotTabPane;
	private JPanel consolePane;
	
	public PhysicalControlViewController(){
		this.plotTabPane = new JTabbedPane();
	    this.plotTabPane.add("Empty", new JPanel());
		this.consolePane = new ControlConsolePanel(this);
		this.consolePane.setBorder(mazestormer.ui.old.MainView.getTitleBorder("Console"));
	}

	@Override
	public Box getView(){
		Box rightPanel = new Box(BoxLayout.Y_AXIS);
	    rightPanel.add(this.plotTabPane);
        rightPanel.add(this.consolePane);
        return rightPanel;
	}
}
