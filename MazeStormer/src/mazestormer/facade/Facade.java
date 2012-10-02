package mazestormer.facade;

import be.kuleuven.cs.som.annotate.*;

/**
 * This class contains all the functionality of the control and model layer
 * required for UI interaction.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class Facade implements IFacade{
	
	/**
	 * Sets the feedback of this front to the given request.
	 * 
	 * @param 	request
	 * 			The new feedback for this front.
	 * @post	The feedback of this front is set to the given request.
	 * 			| new.getFeedback().equals(request)
	 */
	@Model
	private void setFeedback(String request){
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
}
