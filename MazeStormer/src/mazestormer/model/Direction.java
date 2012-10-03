package mazestormer.model;

import java.util.*;

import be.kuleuven.cs.som.annotate.*;

/**
 * An enumeration containing the different directions of the board.
 * This contains: up, right, down, left.
 * 
 * @version 
 * @author 	Team Bronze
 *
 */
public enum Direction {
        UP(0),RIGHT(1),DOWN(2),LEFT(3);
        
        /**
         * Initialize this new direction with the given direction number.
         * 
         * @param	directionnr 
         * 			The direction number for this new direction.
         * @post 	The direction number for this new direction is 
         * 			equal to the given direction.
         *          | new.getDirectionnr() == directionnr
         */
        private Direction(int directionnr){
                this.directionnr = directionnr;
        }
        
        /**
         * Returns the direction number for this direction.
         */
        @Basic @Immutable
        public int getDirectionnr(){
                return this.directionnr;
        }
        
        /**
         * Variable storing the direction number for this direction.
         */
        private final int directionnr;
        
        /**
         * Checks if the given direction is valid.
         * 
         * @param 	direction
         * 			The direction that has to be checked.
         * @return	True if and only if the given direction
         * 			doesn't refer the null reference.
         * 			| result == direction != null
         */
        public static boolean isValidDirection(Direction direction){
        	return direction != null;
        }

        /**
         * Returns a new direction from the given integer value.
         * 
         * @param 	directionValue
         * 			The direction value for which the corresponding
         * 			direction has to be returned.
         * @return	Returns the direction value corresponding to the given integer value.
         * 			| if ((directionValue%4 +4) %4 == 0) then result == Direction.UP
         * 			| if ((directionValue%4 +4) %4 == 1) then result == Direction.RIGHT
         * 			| if ((directionValue%4 +4) %4 == 2) then result == Direction.DOWN
         * 			| if ((directionValue%4 +4) %4 == 3) then result == Direction.LEFT
         */
        public static Direction directionFromInt(int directionValue){
        	directionValue = directionValue % 4;
        	directionValue = (directionValue < 0)? directionValue + 4 : directionValue;
        	return Direction.values()[directionValue];
        }
        
        /**
         * Returns the direction that is situated one clockwise turn further
         * than the given direction.
         * 
         * @param 	direction
         * 			The direction for which the direction has to be returned
         * 			that's situated one clockwise turn further.
         * @return	If the given direction is an invalid direction
         * 			the return value refers the null reference.
         * 			| if(!isValidDirection(direction)) then
         * 			| result == null
         * @return	Returns the direction that is situated one clockwise turn further
         * 			than the given direction.
         * 			| result == getDirectionAfterAddition(direction, 1)
         */
        public static Direction turnDirectionClockwise(Direction direction)
        		throws IllegalArgumentException{
        	if(!isValidDirection(direction))
        		return null;
        	return getDirectionAfterAddition(direction, 1);
        }
        
        /**
         * Returns the direction that is situated one counterclockwise turn further
         * than the given direction.
         * 
         * @param 	direction
         * 			The direction for which the direction has to be returned
         * 			that's situated one counterclockwise turn further.
         * @return	If the given direction is an invalid direction
         * 			the return value refers the null reference.
         * 			| if(!isValidDirection(direction)) then
         * 			| result == null
         * @return	Returns the direction that is situated one counterclockwise turn further
         * 			than the given direction.
         * 			| result == getDirectionAfterAddition(direction, -1)
         */
        public static Direction turnDirectionCounterClockwise(Direction direction)
        		throws IllegalArgumentException{
        	if(!isValidDirection(direction))
        		return null;
        	return getDirectionAfterAddition(direction, -1);
        }
        
        /**
         * Returns the direction number of the given direction of which the
         * direction number is added with the given amount.
         * 
         * @param 	direction
         * 			The direction to start with.
         * @param 	toAdd
         * 			The amount that has to be added from the number of the given direction.
         * @return	Returns the direction number of the given direction of which the
         * 			direction number is added with the given amount.
         * 			| result == directionFromInt(direction.getDirectionnr()+toAdd)
         */
        @Model
        private static Direction getDirectionAfterAddition(Direction direction, int toAdd){
        	return directionFromInt(direction.getDirectionnr()+toAdd);
        }
        
        /**
    	 * Returns the amount of clockwise turns required to reach
    	 * the given target direction, starting from the given initial direction.
    	 * 
    	 * @param 	initial
    	 * 			The initial direction.
    	 * @param	target
    	 * 			The target direction.
    	 * @return	If one of the given directions is invalid, the return value is
    	 * 			equal to minus one.
    	 * 			| if(!isValidDirection(initial) || !isValidDirection(target)) then
    	 * 			| result == -1
    	 * @return 	Declaration of variables used in all following return clauses.
    	 * 			| let
    	 * 			|	amountOfTurns = target.getDirectionnr() - initial.getDirectionnr()
    	 * 			| in:
    	 * @return 	When already facing the direction, returns zero.
    	 * 			| if(amountOfTurns % 4 == 0 || amountOfTurns % 4 == -4)
    	 * 			| then result == 0
    	 * @return 	When one clockwise turn is required to face the direction, returns one.
    	 * 			| if(amountOfTurns % 4 == 1 || amountOfTurns % 4 == -3)
    	 * 			| then result == 1
    	 * @return 	When two clockwise turns are required to face the direction, returns two.
    	 * 			| if(amountOfTurns % 4 == 2 || amountOfTurns % 4 == -2)
    	 * 			| then result == 2
    	 * @return 	When one counterclockwise turn is required to face the direction, returns three.
    	 * 			(one counterclockwise turn is equal to three clockwise turns)
    	 * 			| if(amountOfTurns % 4 == 3 || amountOfTurns % 4 == -1)
    	 * 			| then result == 3
    	 * @note	The range for the (meaningful) return value is from 0 till 3.
    	 * 			The amount of turns is based on clockwise rotation.
    	 * @note	A minus one return value has no meaning.
    	 */
    	public static int amountOfTurnsToDirection(Direction initial, Direction target){
    		if(isValidDirection(initial) && isValidDirection(target)){
    			int amountOfTurns = target.getDirectionnr() - initial.getDirectionnr();
    			amountOfTurns = amountOfTurns % 4;
    			amountOfTurns = (amountOfTurns < 0)? amountOfTurns + 4 : amountOfTurns;
    			return amountOfTurns;
    		}
    		return -1;
    	}
    	
    	/**
    	 * Returns the amount of efficient turns required to reach
    	 * the given target direction, starting from the given initial direction.
    	 * 
    	 * @param 	initial
    	 * 			The initial direction.
    	 * @param	target
    	 * 			The target direction.
    	 * @return	If the amount of clockwise turns to reach the target direction is not 3
    	 * 			then it will return the amount of clockwise turns to reach the target
    	 * 			direction.
    	 * 			| if (amountOfTurnsToDirection(initial, target) != 3) then
    	 * 			| result == amountOfTurnsToDirection(initial, target)
    	 * @return	If the amount of clockwise turns to reach the target direction is 3
    	 * 			then it will return 1; as in 1 counterclockwise turn is needed to reach
    	 * 			the target direction.
    	 * 			| if (amountOfTurnsToDirection(initial, target) == 3)
    	 * 			| result == 1
    	 * @note	The range for the (meaningful) return value is from 0 till 2.
    	 * 			The amount of turns is based on clockwise rotation.
    	 * @note	A minus one return value has no meaning.
    	 */
    	public static int amountOfEfficientTurnsToDirection(Direction initial, Direction target){
    		int amt = amountOfTurnsToDirection(initial, target);
    		amt = (amt == 3)? 1 : amt;
    		return amt;
    	}
        
        /**
    	 * Returns the dimension in which the orientation of the given direction is situated.
    	 * 
    	 * @param 	direction 
    	 * 			The direction of which the dimension has to be found.
    	 * @return	If and only if the given direction is not valid
    	 * 			the return value equals minus one.
    	 * 			| if(!isValidDirection(direction)) then 
    	 * 			| result == -1
    	 * @return 	The direction up(0) and down(2) are in dimension 2 (vertical) 
    	 * 			and left(3) and right(1) in dimension 1 (horizontal)
    	 * 			| if(direction != null) then
    	 * 			| result == 2-(direction.getDirectionnr()%2)
    	 * @note	A minus one return value has no meaning.
    	 */
    	public static int getDirectionDimension(Direction direction){
    		if(!isValidDirection(direction))
    			return -1;
    		return 2-(direction.getDirectionnr()%2);
    	}
    	
    	/**
    	 * Checks if a coordinate corresponding to the dimension,
    	 * which corresponds to the given direction becomes smaller (-1)
    	 * or larger (1) when travelling into the given direction.
    	 * 
    	 * @param 	direction
    	 * 			The direction of travelling.
    	 * @return	When the direction is DOWN or RIGHT the return value is 1.
    	 * 			When the direction is UP or LEFT the return value is -1.
    	 * 			| if(direction == Direction.DOWN || direction == Direction.RIGHT)
    	 * 			| then result == 1
    	 * 			| if(direction == Direction.UP || direction == Direction.LEFT)
    	 * 			| then result == -1
    	 * 			| else result == 0
    	 * @note	A zero return value has no meaning.
    	 * @note	The hypothetical board used in the calculation has
    	 * 			got a horizontal axis to the right and a vertical axis,
    	 * 			going downstairs.
    	 * 			
    	 */
    	public static int getDirectionOrder(Direction direction){
    		if(direction == Direction.DOWN || direction == Direction.RIGHT)
    			return 1;
    		if(direction == Direction.UP || direction == Direction.LEFT)
    			return -1;
    		return 0;
    	}
    	
    	/**
         * Returns a collection with all the valid directions.
         * 
         * @return	Returns a collection which contains all the valid directions once.
         * 			| result.size() == getNbOfDirections() &&
         * 			| for each dir in result :
         * 			| 	Direction.isValidDirection(dir) == true
         */
    	@Immutable
        public static Set<Direction> getAllDirections(){
        	Set<Direction> temp = new HashSet<Direction>();
        	for(int i=0; i <getNbOfDirections(); i++)
        		temp.add(Direction.values()[i]);
        	return temp;
        }
        
        /**
         * Returns the number of different valid directions.
         * 
         * @return	Returns the number of different valid directions.
         * 			| result == Direction.values().length
         */
        @Immutable
        public static int getNbOfDirections(){
        	return Direction.values().length;
        }      
}
