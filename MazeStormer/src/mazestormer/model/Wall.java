package mazestormer.model;

import mazestormer.board.Board;
import mazestormer.board.Position;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class of walls.
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class Wall extends BoardModel{
	
	/**
	 * Initializes a new wall.
	 * 
	 * @effect	This new wall is situated on no board
	 * 			with no position.
	 * 			| this(null, null)
	 */
	@Raw
	public Wall(){
		this(null, null);
	}
	
	/**
	 * Initializes this new wall with given board and position.
	 * 
	 * @param 	board
	 * 			The board where this new wall is situated on.
	 * @param 	position
	 * 			The position of this new wall on the given board.
	 * @effect	This new wall is situated on the given board
	 * 			on the given position.
	 * 			| super(board, position)
	 */
	@Raw
	public Wall(Board board, Position position) 
			throws IllegalArgumentException{
		super(board, position);
	}
	
	/**
	 * Checks if this wall could share its position with the given board model.
	 * 
	 * @param 	request
	 * 			The board model that has to be checked.
	 * @return	True if and only if this wall could share its position
	 * 		   	with the given board model.
	 * 			A wall could not share its position with another model.
	 * 			| result == false
	 * @note	This means this method doesn't include a board, position
	 * 			or termination check.
	 */
	@Override @Raw
	public boolean canSharePositionWith(BoardModel request){
		return false;
	}
	
	/**
	 * Returns a string representation of this wall.
	 * 
	 * @return	Returns a string representation of this wall.
	 * 			| result.equals("Wall")
	 */
	@Override
	public String toString(){
		return "Wall";
	}
}
