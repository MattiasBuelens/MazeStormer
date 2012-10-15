package mazestormer.controller.old;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mazestormer.ui.old.PolygonConsolePanel;

/**
 * A class that provides the interaction between a polygon view
 * with physical interpretation and the connect model classes.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class PhysicalPolygonViewController implements PolygonViewController{

	private JTabbedPane plotTabPane;
	private JPanel consolePane;
	
	public PhysicalPolygonViewController(){
		this.plotTabPane = new JTabbedPane();
	    this.plotTabPane.add("Empty", new JPanel());
		this.consolePane = new PolygonConsolePanel(this);
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
