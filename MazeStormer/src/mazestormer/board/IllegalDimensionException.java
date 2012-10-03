package mazestormer.board;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of exceptions signaling illegal dimensions.
 * 
 * @version	
 * @author 	Team Bronze
 * 
 */
public class IllegalDimensionException extends RuntimeException{
	
	/**
	 * The Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes this new illegal dimension with default value.
	 * 
	 * @param	value
	 *         	The value for this new illegal dimension exception.
	 * @post   	The value of this new illegal dimension exception is equal
	 *         	to the empty String.
	 *       	| new.getValue().equals("")
	 */
	public IllegalDimensionException(){
		this.value = "";
	}

	/**
	 * Initializes this new illegal dimension exception with given value.
	 * 
	 * @param	value
	 *         	The value for this new illegal dimension exception.
	 * @post   	The value of this new illegal dimension exception is equal
	 *         	to the given value.
	 *       	| new.getValue().equals(value)
	 */
	public IllegalDimensionException(String value){
		this.value = value;
	}

	/**
	 * Returns the value registered for this illegal dimension exception.
	 */
	@Basic @Immutable
	public String getValue() {
		return this.value;
	}

	/**
	 * Variable registering the value involved in this illegal dimension
	 * exception.
	 */
	private final String value;
}
