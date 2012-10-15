package mazestormer.controller.old;

import javax.swing.Box;

import mazestormer.ui.old.RobotType;

/**
 * An interface that should be implemented by all classes
 * that must provide the interaction between the main user
 * interface and the model classes.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public interface IMainController{
	
	public String getFeedback();
	
	public void setFeedback();
	
	public Box getConnectView(RobotType rt);
	
	public Box getControlView(RobotType rt);
	
	public Box getPolygonView(RobotType rt);
	
	public Box getDefault();

}
