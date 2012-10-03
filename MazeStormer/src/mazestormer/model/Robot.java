package mazestormer.model;

import mazestormer.board.Board;
import mazestormer.board.Position;

import be.kuleuven.cs.som.annotate.*;

/**
 * A class of robots.
 * 
 * @invar	This robot has a valid direction
 * 			| Direction.isValidDirection(getDirection())
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class Robot extends BoardModel{
	
	/**
	 * Variable storing the standard direction for robots.
	 */
	public final static Direction STANDARD_ROBOT_DIRECTION = Direction.DOWN;
	
	/**
	 * Initializes this new robot with standard values.
	 * 
	 * @effect	This new robot is situated on no board
	 * 			with no position and the direction refers
	 * 			the standard direction for robots.
	 * 			| this(null, null, STANDARD_ROBOT_DIRECTION)
	 */
	@Raw
	public Robot()
			throws IllegalArgumentException{
		this(null, null, STANDARD_ROBOT_DIRECTION);
	}
	
	/**
	 * Initializes this new robot with board and position.
	 * 
	 * @effect	This new robot is situated on the given position
	 * 			on the given board and the direction refers the
	 * 			standard direction for robots.
	 * 			| this(board, position, STANDARD_ROBOT_DIRECTION)
	 */
	@Raw
	public Robot(Board board, Position position)
			throws IllegalArgumentException{
		this(board, position, STANDARD_ROBOT_DIRECTION);
	}
	
	/**
	 * Initializes this new robot with given board, position and orientation.
	 * 
	 * @param 	board
	 * 			The board where this new robot is situated on.
	 * @param 	position
	 * 			The position of the position of this new robot on the given board.
	 * @param	direction
	 * 			The initial direction of this new robot that has to be set.
	 * @effect	This new robot is situated on the given board
	 * 			on the given position.
	 * 			| super(board, position)
	 * @effect	Sets the new direction of this robot to the given direction.
	 * 			| setDirection(direction)
	 */
	@Raw
	public Robot(Board board, Position position, Direction direction) 
				throws IllegalArgumentException{
		super(board, position);
		setDirection(direction);
	}
	
	/**
	 * Checks if this robot could share its position with the given board model.
	 * 
	 * @param 	request
	 * 			The board model that has to be checked.
	 * @return	True if and only if this robot could share its position
	 * 		   	with the given board model.
	 * 			A robot could share a position with an inventory model,
	 * 			an action trigger and a lock model.
	 * 			| if(InventoryModel.class.isInstance(request)
	 * 			|	|| ActionTrigger.class.isInstance(request)
	 * 			|	|| Lock.class.isInstance(request))
	 * 			|	then result == true
	 * @note	This means this method doesn't include a board, position
	 * 			or termination check.
	 */
	@Override @Raw
	public boolean canSharePositionWith(BoardModel request){
		if(Robot.class.isInstance(request))
			return true;
		return false;
	}
	
	/**
	 * Returns the direction of this robot.
	 */
	@Basic @Raw
	public Direction getDirection(){
		return direction;
	}
	
	/**
	 * Sets the direction of this robot to the given direction.
	 * 
	 * @param 	direction
	 * 			The direction that has to be set.
	 * @post	The new direction of this robot is equal
	 * 			to the given direction. If the given direction
	 * 			is invalid, it is set to the standard direction for robots.
	 * 			| if(Direction.isValidDirection(direction))
	 * 			|	then direction = STANDARD_ROBOT_DIRECTION
	 * 			| new.getDirection() == direction
	 */
	@Raw @Model
	private void setDirection(Direction direction){
		if(!Direction.isValidDirection(direction))
			direction = STANDARD_ROBOT_DIRECTION;
		this.direction = direction;
	}
	
	/**
	 * Turns this robot 90 degrees in a clockwise direction.
	 * 
	 * @effect	The robot's direction is turned one clockwise turn.
	 * 			| setDirection(Direction.turnDirectionClockwise(getDirection()))
	 */
	public void turnClockwise(){
		setDirection(Direction.turnDirectionClockwise(getDirection()));
	}
	
	/**
	 * Turns this robot 90 degrees in a counterclockwise direction.
	 * 
	 * @effect	The robot's direction is turned one counterclockwise turn.
	 * 			| setDirection(Direction.turnDirectionCounterClockwise(getDirection()))
	 */
	public void turnCounterClockwise(){
		setDirection(Direction.turnDirectionCounterClockwise(getDirection()));
	}
	
	/**
	 * Turns this robot efficiently to the given direction.
	 * This means turning by the least possible number of turns required.
	 * 
	 * @param	direction
	 * 			The direction that has to be reached by this robot.
	 * @effect	According to the least amount of turns required for
	 * 			this robot to reach the given direction, this robot
	 * 			turns clockwise or counterclockwise to the given direction.
	 * 			| if(Direction.amountOfTurnsToDirection(getDirection(), direction) == 1)
	 * 			| 	then turnClockwise()
	 * 			| if(Direction.amountOfTurnsToDirection(getDirection(), direction) == 2)
	 * 			| 	then turnClockwise() && turnClockwise()
	 * 			|	[SEQUENTIAL]
	 * 			| if(Direction.amountOfTurnsToDirection(getDirection(), direction) == 3)
	 * 			| 	then turnCounterClockwise()
	 */
	public void turnToDirection(Direction direction){
		switch (Direction.amountOfTurnsToDirection(getDirection(), direction)){
			case 1:
				turnClockwise();
				break;
			case 2:
				turnClockwise();
				turnClockwise();
				break;
			case 3:
				turnCounterClockwise();
			break;
		}
	}
	
	/**
	 * The direction of this robot.
	 */
	private Direction direction;
	
	/**
	 * Returns a string representation of this robot.
	 * 
	 * @return	Returns a string representation of this robot.
	 * 			| result.equals("Robot")
	 */
	@Override
	public String toString(){
		return "Robot";
	}
	
	/**
	 * Moves this robot one unit forward in the current direction of this robot.
	 * 
	 * @post	The direction of this robot has not changed.
	 * 			| (new this).getDirection() == this.getDirection()
	 * @post	If this robots direction is up or down the x coordinate has not changed.
	 * 			| if (this.getDirection() == Direction.UP || this.getDirection() == Direction.DOWN)
	 * 			| 	then (new this).getCoordinate(Dimension.HORIZONTAL) == (this).getCoordinate(Dimension.HORIZONTAL)
	 * @post	If this robots direction is left or right the y coordinate has not changed.
	 * 			| if (this.getDirection() == Direction.LEFT || this.getDirection() == Direction.RIGHT)
	 * 			| 	then (new this).getCoordinate(Dimension.VERTICAL) == (this).getCoordinate(Dimension.VERTICAL)
	 * @post	If this robots direction is left the x coordinate has been subtracted by one.
	 * 			| if (this.getDirection() == Direction.LEFT)
	 * 			| 	then (new this).getCoordinate(Dimension.HORIZONTAL) = (this).getCoordinate(Dimension.HORIZONTAL) - 1;
	 * @post	If this robots direction is right the x coordinate has been added with one.
	 * 			| if (this.getDirection() == Direction.RIGHT)
	 * 			| 	then (new this).getCoordinate(Dimension.HORIZONTAL) = (this).getCoordinate(Dimension.HORIZONTAL) + 1;
	 * @post	If this robots direction is up the y coordinate has been subtracted by one.
	 * 			| if (this.getDirection() == Direction.UP)
	 * 			| 	then (new this).getCoordinate(Dimension.VERTICAL) = (this).getCoordinate(Dimension.VERTICAL) - 1;
	 * @post	If this robots direction is down the y coordinate has been added with one.
	 * 			| if (this.getDirection() == Direction.DOWN)
	 * 			| 	then (new this).getCoordinate(Dimension.VERTICAL) = (this).getCoordinate(Dimension.VERTICAL) + 1;
	 * @throws	IllegalStateException
	 * 			This robot is not effective.
	 * 			| isTerminated()
	 * @throws	IllegalArgumentException
	 * 			The robot's new coordinates are invalid for its board.
	 * 			| !getBoard().canHaveBoardModelAtNoBindingCheck((new this).getPosition(), this)
	 */
	public void move() throws IllegalStateException, IllegalArgumentException{
		if(isTerminated())
			throw new IllegalStateException("This robot is not effective.");
		moveDirection(getDirection());
	}
	
	/**
	 * Turns this robot efficiently to the given direction
	 * and then moves one unit forward in that direction.
	 * 
	 * @param	direction
	 * 			The direction to turn to and move one unit forward in.
	 * @post	The direction of this robot has changed to the given direction.
	 * 			| (new this).getDirection() == direction
	 * @post	If the given direction is up or down the x coordinate has not changed.
	 * 			| if (direction == Direction.UP || direction == Direction.DOWN)
	 * 			| 	then (new this).getCoordinate(1) == (this).getCoordinate(1)
	 * @post	If the given direction is left or right the y coordinate has not changed.
	 * 			| if (direction == Direction.LEFT || direction == Direction.RIGHT)
	 * 			| 	then (new this).getCoordinate(2) == (this).getCoordinate(2)
	 * @post	If the given direction is left the x coordinate has been subtracted by one.
	 * 			| if (direction == Direction.LEFT)
	 * 			| 	then (new this).getCoordinate(1) = (this).getCoordinate(1) - 1;
	 * @post	If the given direction is right the x coordinate has been added with one.
	 * 			| if (direction == Direction.RIGHT)
	 * 			| 	then (new this).getCoordinate(1) = (this).getCoordinate(1) + 1;
	 * @post	If the given direction is up the y coordinate has been subtracted by one.
	 * 			| if (direction == Direction.UP)
	 * 			| 	then (new this).getCoordinate(2) = (this).getCoordinate(2) - 1;
	 * @post	If the given direction is down the y coordinate has been added with one.
	 * 			| if (direction == Direction.DOWN)
	 * 			| 	then (new this).getCoordinate(2) = (this).getCoordinate(2) + 1;
	 * @throws	IllegalStateException
	 * 			This robot is not effective.
	 * 			| isTerminated()
	 * @throws	IllegalArgumentException
	 * 			The given direction is invalid.
	 * 			| !Direction.isValidDirection(direction)
	 * @throws	IllegalArgumentException
	 * 			The robot's new coordinates are invalid for its board.
	 * 			| !getBoard().canHaveBoardModelAtNoBindingCheck((new this).getPosition(), this)
	 */
	@Model
	private void moveDirection(Direction direction)
			throws IllegalStateException, IllegalArgumentException{
		if(isTerminated())
			throw new IllegalStateException("This robot is not effective.");
		if(!Direction.isValidDirection(direction))
			throw new IllegalArgumentException("The given direction is invalid.");
		turnToDirection(direction);
		super.moveToPosition(getPosition().getNextPositionInDirection(getDirection()));
	}
}
