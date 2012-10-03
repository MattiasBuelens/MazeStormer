package mazestormer.model;

import be.kuleuven.cs.som.annotate.*;
import mazestormer.Terminatable;
import mazestormer.board.*;
import mazestormer.board.Position;

/**
 * An abstract super class representing every board model that could be
 * placed on a board, involving a board, a position.
 * 
 * @invar	The board where a board model is situated on must be
 * 			always a proper board for that board model.
 * 			| hasProperBoard()
 * @invar	The position where a board model is situated on must be
 * 			always a proper position for that board model.
 * 			| hasProperPosition()
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public abstract class BoardModel implements Terminatable{
	
	/**
	 * Initializes a new board object with given board and position.
	 * 
	 * @param 	board
	 * 			The board where this new board object is situated on.
	 * @param 	position
	 * 			The position of this new board object on the given board.
	 * 			Registering whether new this board model is movable.
	 * @post	The new board model isn't terminated.
	 * 			| new.isTerminated() == false
	 * @effect	This new board object is added to the the given board, if
	 * 			this board doesn't refer the null reference. Else the board of
	 * 			this new board model is set to the given board.
	 * 			| if(board != null) then board.addBoardModelAt(position, new.this)
	 * 			| else setBoard(null)
	 * @throws	IllegalArgumentException
	 * 			This board model could not be situated on the given board
	 * 			on the given position. Note that there could be no exception
	 * 			if the board refers the null reference.
	 * 			| if(board != null) then
	 * 			| 	!board.canHaveAsBoardModel(position, this)
	 * @throws	IllegalArgumentException
	 * 			The given board isn't a valid board.
	 * 			| !canHaveAsBoard(board)
	 */
	@Raw @Model
	protected BoardModel(Board board, Position position) 
			throws IllegalArgumentException{
		this.isTerminated = false;
		if(board != null)
			board.addBoardModelAt(position, this);
		else
			setBoard(null);
	}
	
	/**
	 * Terminates this board model.
	 * 
	 * @post	If this board model is not terminated before
	 * 			executing this method and becomes terminated
	 * 			after executing this method, its old board
	 * 			doesn't contain this board model anymore
	 * 			(if it doesn't refer the null reference)
	 * 			and its new board and new position refers
	 * 			the null reference.
	 * 			| if(!isTerminated() && new.isTerminated())
	 * 			| 	then 	(if(getBoard() != null)
	 * 			|				then !getBoard().containsBoardModel_allCheck(this))
	 * 			|			&& new.getBoard() == null
	 * 			|			&& new.getPosition() == null
	 */
	@Override
	public void terminate() {
		if(getBoard() != null)
			getBoard().removeBoardModel(this);
		this.isTerminated = true;
	}
	
	/**
	 * Checks whether this model is terminated.
	 */
	@Basic @Raw @Override
	public boolean isTerminated(){
		return this.isTerminated;
	}
	
	/**
	 * Variable registering whether this model is terminated.
	 */
	private boolean isTerminated;
	
	/**
	 * Checks if this board model has a proper board.
	 * 
	 * @return	If the board of this board model is not valid,
	 * 			the result is false.
	 * 			| if(!isValidBoard(getBoard()))
	 * 			| 	then result == false
	 * @return	If the board of this board model does not refer
	 * 			the null reference and this board model is terminated,
	 * 			the result is false.
	 * 			| if(getBoard() != null && isTerminated())
	 * 			|	then result == false
	 * @return	If the board of this board model does not refer
	 * 			the null reference and this board model is not
	 * 			situated anywhere on that board, the result is false.
	 * 			| if(getBoard() != null && !getBoard().containsBoardModel_allCheck(this))
	 * 			|	then result == false
	 * @return	In all other cases, the result is true.
	 * 			| result == true
	 */
	@Raw
	public boolean hasProperBoard(){
		if(!isValidBoard(getBoard()))
			return false;
		if(getBoard() != null && isTerminated())
			return false;
		if(getBoard() != null && !getBoard().containsBoardModel_allCheck(this))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given board is a valid board for any board model.
	 *
	 * @param 	board
	 * 			The board that has to be checked.
	 * @return	True if the board is the null reference.
	 * 			If not the board is valid if it's not terminated.
	 * 			| if (board == null)
	 * 			| 	result == true
	 * 			| else
	 * 			| 	result == !board.isTerminated()
	 */
	public static boolean isValidBoard(Board board){
		return ((board == null)? true : !board.isTerminated());
	}
	
	/**
	 * 
	 * @param 	board
	 * 			The board that has to be checked.
	 * @return	If the given board is invalid,
	 * 			the result is always false.
	 * 			| if(!isValidBoard(board)) then
	 * 			| 	result == false
	 * @return	If the given board doesn't refer the null
	 * 			reference and if this board model is terminated,
	 * 			the result is always false.
	 * 			| if(board != null && isTerminated) then
	 * 			| 	result == false
	 * @return	If the given board is equal to the board of this board model,
	 * 			the result is always true. This makes invariant check possible.
	 * 			| if(board == getBoard()) then
	 * 			| 	result == true
	 * @return	If the board of this board model doesn't refer the null
	 * 			reference and if this board model is still situated on
	 * 			its board, the result is always false.
	 * 			| if(getBoard() != null && getBoard().containsBoardModel_allCheck(this))
	 * 			| 	result == false
	 * @return	If the given board doesn't refer the null reference and
	 * 			if this board model is not yet situated on the given board,
	 * 			the result is always false.
	 * 			| if(board != null && !board.containsBoardModel_allCheck(this))
	 * 			| 	result == false
	 * @return	In all other cases is the result true.
	 * 			| in all other cases:
	 * 			| result == true
	 */
	@Raw
	public boolean canHaveAsBoard(Board board){
		if(!isValidBoard(board))
			return false;
		// first condition not necessary with current terminate() implementation
		// but much safer
		if(board != null && isTerminated())
			return false;
		// just for invariant check
		if(board == getBoard())
			return true;
		if(getBoard() != null && getBoard().containsBoardModel_allCheck(this))
			return false;
		if(board != null && !board.containsBoardModel_allCheck(this))
			return false;
		else
			return true;
	}
	
	/**
	 * Returns the board where this board model is situated on.
	 */
	@Basic @Raw
	public Board getBoard(){
		return board;
	}
	
	/**
	 * Sets the board where this board model is situated on
	 * to the given board.
	 * 
	 * @param 	board
	 * 			The new value for the board.
	 * @post	The given board is set to the board where
	 * 			this board model is situated on.
	 * 			| new.getBoard() == board
	 * @effect	If the given board refers the null reference
	 * 			the new position of this board is also
	 * 			set to the null reference.
	 * 			| if(board == null)
	 * 			| 	then setPosition(null)
	 * @throws	IllegalArgumentException
	 * 			The board is not valid for this board model.
	 * 			| !canHaveAsBoard(board)
	 */
	@Raw
	public void setBoard(@Raw Board board) throws IllegalArgumentException{
		if(!canHaveAsBoard(board))
			throw new IllegalArgumentException("The given board is not valid for this board model.");
		this.board = board;
		if(board == null){
			setPosition(null);
		}
	}
	
	/**
	 * The board where this board model is situated.
	 */
	private Board board;
	
	/**
	 * Checks if this board model has a proper position.
	 * 
	 * @return	If the board of this board model does not refer the
	 * 			null reference and the position of this board model
	 * 			refers the null reference, the result is false.
	 * 			| if(getBoard() != null && getPosition() == null)
	 * 			|	then result == false
	 * @return	If the board of this board model refers the null reference
	 * 			and the position of this board does not refer the null
	 * 			reference, the result is false.
	 * 			| if(getBoard() == null && getPosition() != null)
	 * 			|	then result == false
	 * @return	If the board of this board model does not refer the
	 * 			null reference and this board model is not situated
	 * 			on its position on its board, the result is false.
	 * 			| if(getBoard() != null && !getBoard().containsBoardModel_positionCheck(this))
	 * 			|	then result == false
	 * @return	In all other cases, the result is true.
	 *			| result == true
	 */
	@Raw
	public boolean hasProperPosition(){
		if(getBoard() != null && getPosition() == null)
			return false;
		if(getBoard() == null && getPosition() != null)
			return false;
		if(getBoard() != null && !getBoard().containsBoardModel_positionCheck(this))
			return false;
		return true;
	}
	
	/**
	 * Checks if this board model can have the given position as its position.
	 * 
	 * @return	If the board of this board model doesn't refer the null reference
	 * 			and the given position refers the null reference, the result
	 * 			is always false.
	 * 			| if(getBoard() != null && (position == null)) then
	 * 			| result == false
	 * @return	If the board refers the null reference and the position
	 * 			doesn't refer the null reference. The result is always false.
	 * 			| if(getBoard() == null && position != null) then
	 * 			| result == false
	 * @return	If the position of this board model is equal
	 * 			to the given position, the result is true; if
	 * 			both positions doesn't refer the null reference.
	 * 			This makes invariant check possible.
	 * 			| if(getPosition() != null && position != null && position.equals(getPosition())) then
	 * 			| result == true
	 * @return	If both positions refer the null reference, the result is true.
	 * 			| if(getPosition() == null && position == null) then
	 * 			| result == true
	 * @return	If the board of this board model doesn't refer the null reference
	 * 			and this board model is situated on its position on its board
	 * 			of this board model is not situated on the given position on its board,
	 * 			the result is false.
	 * 			| if(getBoard() != null && (getBoard().containsBoardModel_positionCheck(this)
	 * 			|						|| !getBoard().containsBoardModel(this, position))) then
	 * 			| result == false
	 * @return	In all other cases, the result is true.
	 * 			| in all other cases:
	 * 			| result == true
	 * @note	Note that the order of the return clauses is crucial. If a condition is true
	 * 			in a lower situated return clause, but false at a higher situated return clause,
	 * 			the return value is false.
	 */
	@Raw
	public boolean canHaveAsPosition(Position position){
		if(getBoard() != null && position == null)
			return false;
		if(getBoard() == null && position != null)
			return false;
		
		// For invariant check
		if(getPosition() != null && position != null && position.equals(getPosition()))
			return true;
		if(getPosition() == null && position == null)
			return true;
		
		if(getBoard() != null && (getBoard().containsBoardModel_positionCheck(this) || !getBoard().containsBoardModel(this, position)))
			return false;
		else
			return true;
	}
	
	/**
	 * Returns the position of this board model on its board.
	 * 
	 * @note	If the position doesn't refer the null reference
	 * 			a clone position is returned.
	 */
	@Basic @Raw
	public Position getPosition(){
		return position==null? null:position.clone();
	}
	
	/**
	 * Sets the position of this board model to the given position.
	 * 
	 * @param 	position
	 * 			The new position for this board model.
	 * @post	The new position of this board model
	 * 			refers the given position, if this position
	 * 			refers the null reference. Otherwise the new
	 * 			position of this board model is equal to the
	 * 			given position.
	 * 			| if(position != null) then
	 * 			| new.getPosition().equals(position)
	 * 			| if(position == null) then
	 * 			| new.getPosition() == null
	 * @throws	IllegalArgumentException
	 * 			The given position is invalid for this board model.
	 * 			| !canHaveAsPosition(position)
	 */
	@Raw
	public void setPosition(Position position) throws IllegalArgumentException{
		if(!canHaveAsPosition(position))
			throw new IllegalArgumentException("The given position is invalid for this board model.");
		this.position = position == null? null:position.clone();
	}
	
	/**
	 * Checks if this board model could share its position with the given board model.
	 * 
	 * @param 	request
	 * 			The board model that has to be checked.
	 * @return	True if and only if this board model could share its position
	 * 		   	with the given board model.
	 * @note	This means this method doesn't include a board, position
	 * 			or termination check.
	 */
	@Raw
	public abstract boolean canSharePositionWith(BoardModel request);
	
	/**
	 * The position of this board model on its board..
	 */
	private Position position;
	
	/**
	 * Moves this board model to the given position on its current board.
	 * 
	 * @param	pos
	 * 			The position that has to be reached by this board model.
	 * @post	The board now contains this board model at the new specified (X,Y) coordinates.
	 * 			| 	new.getPosition().equals(pos) &&
	 * 			| 	new.getBoard().getBoardModelsAt(new.getPosition()).contains(this) == true &&
	 * 			| 	new.getBoard().getBoardModelsAt(getPosition()).contains(this) == false
	 * @throws	IllegalStateException
	 * 			The board model must be positioned on a not null referring board.
	 * 			| getBoard() == null
	 * @throws	IllegaArgumentException
	 * 			The new created position is invalid for the board model's board.
	 * 			| !getBoard().canHaveBoardModelAtNoBindingCheck(new Position(coordX, coordY))
	 */
	public void moveToPosition(Position pos)
			throws IllegalStateException, IllegalArgumentException{
		if(getBoard() == null)
			throw new IllegalStateException();
		
		getBoard().moveBoardModelTo(this, pos);
	}
}