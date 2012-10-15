package mazestormer.controller.old;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mazestormer.ui.old.RobotType;
import be.kuleuven.cs.som.annotate.*;

/**
 * This class provides indirectly the interaction between the main user interface
 * and the model classes, by other sub controllers depending on the requested functionality
 * by the user.
 * 
 * @version
 * @author 	Team Bronze
 *
 */
public class MainController implements IMainController{
	
	private static MainController instance = new MainController();
	
	private MainController(){
		
	}
	
	public static MainController getInstance(){
		return instance;
	}
	
	/**
	 * Sets the feedback of this front to the given request.
	 * 
	 * @param 	request
	 * 			The new feedback for this front.
	 * @post	The feedback of this front is set to the given request.
	 * 			| new.getFeedback().equals(request)
	 */
	public void setFeedback(String request){
		this.feedback = request;
	}
	
	/**
	 * Returns the feedback of this front.
	 */
	@Basic @Override
	public String getFeedback(){
		return this.feedback;
	}
	
	/**
	 * Variable storing the feedback of this front.
	 */
	private String feedback;

	@Override
	public void setFeedback() {
		
	}

	@Override
	public Box getConnectView(RobotType rt){
		if(RobotType.PHYSICAL == rt)
			return (new PhysicalConnectViewController()).getView();
		if(RobotType.VIRTUAL == rt)
			return (new VirtualConnectViewController()).getView();
		return getDefault();
	}
	
	@Override
	public Box getControlView(RobotType rt){
		if(RobotType.PHYSICAL == rt)
			return (new PhysicalControlViewController()).getView();
		if(RobotType.VIRTUAL == rt)
			return (new VirtualControlViewController()).getView();
		return getDefault();
	}
	
	@Override
	public Box getPolygonView(RobotType rt){
		if(RobotType.PHYSICAL == rt)
			return (new PhysicalPolygonViewController()).getView();
		if(RobotType.VIRTUAL == rt)
			return (new VirtualPolygonViewController()).getView();
		return getDefault();
	}
	
	@Override
	public Box getDefault(){
		Box rightPanel = new Box(BoxLayout.Y_AXIS);
		JTabbedPane plotTabPane = new JTabbedPane();
	    plotTabPane.add("Empty", new JPanel());
	    rightPanel.add(plotTabPane);
        JPanel consolePane = new JPanel();
        consolePane.setBorder(mazestormer.ui.old.MainView.getTitleBorder("Console"));
	    rightPanel.add(consolePane);
        return rightPanel;
	}
}
