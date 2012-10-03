package mazestormer.board;

import java.math.BigInteger;
import mazestormer.model.Direction;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class representing positions, involving coordinates.
 * 
 * @invar	The coordinates of every position must be valid coordinates.
 * 			| areValidCoordinates(getCoordinates())
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
@Value
public class Position{
	
	/**
	 * Variable containing the standard 'X', horizontal coordinate for a position.
	 */
	public static final long STANDARD_POSITION_X_COORDINATE = 0;
	
	/**
	 * Variable containing the standard 'Y', vertical coordinate for a position.
	 */
	public static final long STANDARD_POSITION_Y_COORDINATE = 0;
	
	/**
	 * Initializes a new position with the standard coordinates for a position.
	 * 
	 * @effect	Sets the given coordinates to the standard position coordinates
	 * 			for this new position.
	 * 			| this(new long[]{STANDARD_POSITION_X_COORDINATE, STANDARD_POSITION_Y_COORDINATE})
	 */
	public Position(){
		this(new long[]{STANDARD_POSITION_X_COORDINATE, STANDARD_POSITION_Y_COORDINATE});
	}
	
	/**
	 * Initializes a new position with the given coordinates.
	 * 
	 * @param 	coordX
	 * 			The 'X', horizontal coordinate for this new position.
	 * @param 	coordY
	 * 			The 'Y', vertical coordinate for this new position.
	 * @effect	Sets the given coordinates to the new coordinates
	 * 			for this new position.
	 * 			| this(new long[]{long1, long2})
	 */
	public Position(long coordX, long coordY){
		this(new long[]{coordX, coordY});
	}
	
	/**
	 * Initializes a new position with the given coordinates.
	 * 
	 * @param 	coordinates
	 * 			The coordinates for this new position.
	 * @post	Sets the given coordinates to the coordinates of this position.
	 * 			| new.getCoordinates() == request
	 * @throws	IllegalArgumentexception
	 * 			The given coordinates are invalid.
	 * 			| !areValidCoordinates(coordinates)
	 */
	public Position(long[] coordinates) throws IllegalArgumentException{
		if(!areValidCoordinates(coordinates))
			throw new IllegalArgumentException("Rejected coordinates length: " + coordinates.length);
		this.coordinates = coordinates;
	}
	
	/**
	 * Checks if the given coordinates are valid for a position.
	 * 
	 * @param 	coordinates
	 * 			the coordinates that have to be checked.
	 * @return	True if and only if the amount of given coordinates
	 * 			is equal to the dimension of the boards.
	 * 			| result == coordinates.length==Board.getNbDimensions()
	 * @note	There is only a check if the coordinates
	 * 			are valid for two dimensional boards.
	 */
	public static boolean areValidCoordinates(long[] coordinates){
		return coordinates.length==Board.getNbDimensions();
	}
	
	/**
	 * Returns the coordinate at the given dimension of this position.
	 * 
	 * @param 	dimension
	 * 			The dimension of the coordinate to get.
	 * @return	Returns the coordinate at the given dimension.
	 * 			| result == coordinates[dimension.getDimensionnr()-1]
	 * @throws 	IllegalDimensionException()
	 * 			The given dimension is invalid.
	 * 			| !Dimension.isValidDimension(dimension)
	 */
	@Raw @Immutable
	public long getCoordinate(Dimension dimension) throws IllegalDimensionException{
		if (!Dimension.isValidDimension(dimension))
			throw new IllegalDimensionException("The given dimension is invalid.");
		return coordinates[dimension.getDimensionnr()-1];
	}
	
	/**
	 * Returns the coordinates of this position.
	 */
	@Basic @Raw @Immutable
	public long[] getCoordinates(){
		return coordinates.clone();
	}
	
	/**
	 * Variable storing the coordinates of this position.
	 */
	private final long[] coordinates;

	/**
	 * Checks if this position and the given object are equals.
	 * 
	 * @param 	object
	 * 			The object to compare with.
	 * @return	If the given object refers the null reference,
	 * 			the result is always false.
	 * 			| if (object == null) then
	 * 			| 	result == false
	 * @return	If the class of this position is not equal
	 * 			to the class of the given object, the result is false.
	 *			| if(this.getClass() != object.getClass()) then
	 *			|	result == false
	 * @return	If the coordinates of this position and the given position
	 * 			differs in amount, the result is false.
	 * 			| if(position.getCoordinates().length != coordinates.length) then
	 * 			| 	result == false
	 * @return	If one coordinate value of the given position differs
	 * 			from the corresponding coordinate value of this position,
	 * 			the result is false.
	 * 			| for i from 1 by 1 to coordinates.length-1 :
	 * 			| 	if(position.getCoordinates()[i] != coordinates[i]) then
	 * 			| 		result == false
	 * @return	In all other cases:
	 * 			| result == true;
	 */
	@Override
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(this.getClass() != object.getClass())
			return false;
		Position position = (Position) object;
		if(position.getCoordinates().length != coordinates.length)
			return false;
		for(int i=0; i<coordinates.length; i++){
			if(position.getCoordinates()[i] != coordinates[i])
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the hashcode of this position.
	 * 
	 * @return	Returns the hashcode of this position.
	 * 			| result == 31*(getCoordinates()[0]^(getCoordinates()[0]>>>32))
	 * 			| 			 + (getCoordinates()[1]^(getCoordinates()[1]>>>32))
	 */
	@Override
	public int hashCode(){
        int result = (int) (getCoordinates()[0] ^ (getCoordinates()[0] >>> 32));
        result = 31 * result + (int) (getCoordinates()[1] ^ (getCoordinates()[1] >>> 32));
        return result;
	}
	
	/**
	 * Returns a clone of this position.
	 * 
	 * @return	Returns a clone of this position.
	 * 			| result.equals(this)
	 */
	@Override
	public Position clone(){
		return new Position(getCoordinates());
	}
	
	/**
	 * Returns the Manhattan distance separation of this position
	 * and the other given position.
	 * 
	 * @param 	other
	 * 			The other position.
	 * @return	The result value is equal to the sum of the absolute value
	 * 			of the difference in horizontal coordinate and the absolute value
	 * 			of the difference in vertical coordinate.
	 * 			| result == BigInteger.valueOf(Math.abs(this.getCoordinates()[0]-other.getCoordinates()[0])).add(
	 *			| 			BigInteger.valueOf(Math.abs(this.getCoordinates()[1]-other.getCoordinates()[1])))
	 * @throws	NullPointerException
	 * 			The given position may not refer the null reference.
	 * 			| other == null
	 */
	public BigInteger getManhattanDistanceSeparation(Position other) throws NullPointerException{
		if(other == null)
			throw new NullPointerException("The given position may not refer the null reference.");
		return 	BigInteger.valueOf(Math.abs(this.getCoordinates()[0]-other.getCoordinates()[0])).add(
				BigInteger.valueOf(Math.abs(this.getCoordinates()[1]-other.getCoordinates()[1])));
	}
	
	/**
	 * Returns a string representation of this position.
	 * 
	 * @return	The result for a position with coordinates (a,b)
	 * 			is equal to "X:aY:b"
	 * 			| result.equals("X: "+ getCoordinate(Dimension.HORIZONTAL) + " Y: "+getCoordinate(Dimension.VERTICAL))
	 */
	@Override
	public String toString(){
		return "X: "+ getCoordinate(Dimension.HORIZONTAL) + " Y: "+getCoordinate(Dimension.VERTICAL);
	}
	
	/**
	 * Returns the position facing this position in the given direction.
	 * 
	 * @param 	direction
	 * 			The facing direction.
	 * @return	Returns the position facing this position in the given direction.
	 * 			| let
	 * 			|	temp = getCoordinates()
	 * 			|	temp[Direction.getDirectionDimension(direction)-1] = temp[Direction.getDirectionDimension(direction)-1]+Direction.getDirectionOrder(direction)
	 * 			| in:
	 * 			| result.equals(new Position(temp))
	 * @throws	IllegalArgumentException
	 * 			The given direction must be valid.
	 * 			| !Direction.isValidDirection(direction)
	 */
	public Position getNextPositionInDirection(Direction direction)
			throws IllegalArgumentException{
		if(!Direction.isValidDirection(direction))
			throw new IllegalArgumentException("The given direction is invalid.");
		
		long[] temp = getCoordinates();
		temp[Direction.getDirectionDimension(direction)-1] = temp[Direction.getDirectionDimension(direction)-1]+Direction.getDirectionOrder(direction);
		return new Position(temp);
	}
}

