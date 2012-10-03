package mazestormer.board;

import java.util.*;
import be.kuleuven.cs.som.annotate.*;

/**
 * An enumeration containing the different dimensions for a board.
 * This contains: HORIZONTAL and VERTICAL.
 * 
 * @version 
 * @author 	Team Bronze
 *
 */
public enum Dimension{
	HORIZONTAL(1), VERTICAL(2);
	
	/**
     * Initialize this new dimension with the given dimension number.
     * 
     * @param	dimensionnr 
     * 			The dimension number for this new dimension.
     * @post 	The dimension number for this new dimension is 
     * 			equal to the given dimension.
     *          | new.getDimensionnr() == dimensionnr
     */
    private Dimension(int dimensionnr){
    	this.dimensionnr = dimensionnr;
    }
    
    /**
     * Returns the dimension number for this dimension.
     */
    @Basic @Immutable
    public int getDimensionnr(){
    	return this.dimensionnr;
    }
    
    /**
     * Variable storing the dimension number for this dimension.
     */
    private final int dimensionnr;
    
    /**
     * Checks if the given dimension is valid.
     * 
     * @param 	dimension
     * 			The dimension that has to be checked.
     * @return	True if and only if the given dimension
     * 			doesn't refer the null reference.
     * 			| result == (dimension != null)
     */
    public static boolean isValidDimension(Dimension dimension){
    	return dimension != null;
    }
    
    /**
     * Returns a new dimension from the given integer value.
     * 
     * @param 	dimensionValue
     * 			The dimension value for which the corresponding
     * 			dimension has to be returned.
     * @return	Returns the dimension value corresponding to the given integer value.
     * 			| if(correspondsToExistingDimension(dimensionValue)) then
     * 			| result == Dimension.values()[dimensionValue-1]
     * @return	Returns the horizontal dimension if the given dimension value
     * 			doesn't refer an existing valid dimension.
     * 			| if(!correspondsToExistingDimension(dimensionValue)) then
     * 			| result == Dimension.HORIZONTAL
     */
    public static Dimension dimensionFromInt(int dimensionValue){
    	if(!correspondsToExistingDimension(dimensionValue))
    		return Dimension.HORIZONTAL;
    	return Dimension.values()[dimensionValue-1];
    }
    
    /**
     * Checks if the given value corresponds to an existing dimension.
     * 
     * @param 	value
     * 			The value that has to be checked.
     * @return	True if and only if there exist
     * 			a not null referring dimension with
     * 			the given value as its value.
     * 			| result == there exists a dim in getAllDimensions()
     * 			|			for which dim.getDimensionnr() == value
     */
    public static boolean correspondsToExistingDimension(int value){
    	for(Dimension dim : getAllDimensions()){
    		if(dim.getDimensionnr() == value)
    			return true;
    	}
    	return false;
	}
    
    /**
     * Returns a collection with all the valid dimensions.
     * 
     * @return	Returns a collection which contains all the valid dimensions once.
     * 			| result.size() == Dimension.values().length &&
     * 			| for each dim in result :
     * 			| 	Dimension.isValidDimension(dim) == true
     */
    @Immutable
    public static Set<Dimension> getAllDimensions(){
    	Set<Dimension> temp = new HashSet<Dimension>();
    	for(int i=0; i <getNbOfDimensions(); i++)
    		temp.add(Dimension.values()[i]);
    	return temp;
    }
    
    /**
     * Returns the number of different valid dimensions.
     * 
     * @return	Returns the number of different valid dimensions.
     * 			| result == Dimension.values().length
     */
    @Immutable
    public static int getNbOfDimensions(){
    	return Dimension.values().length;
    }
}
