package mazestormer.board;

import mazestormer.Terminatable;
import mazestormer.model.*;
import be.kuleuven.cs.som.annotate.*;

import java.util.*;

/**
 * A class of two dimensional boards involving a horizontal size, a vertical size
 * and collection of board models.
 * 
 * @invar	The size corresponding to the 'X', horizontal dimension
 * 			must be a valid size for any board.
 * 			| isValidSizeAt(1, getSizeAt(1))
 * @invar	The size corresponding to the 'Y', vertical dimension
 * 			must be a valid size for any board.
 * 			| isValidSizeAt(2, getSizeAt(2))
 * @invar	The length of the size array of every board must be equal
 * 			to the dimension for boards.
 * 			| getSize().length == getNbDimensions()
 * @invar	Every board must have proper board models.
 * 			| hasProperBoardModels()
 * 
 * @version	
 * @author 	Team Bronze
 *
 */
public class Board implements Terminatable{
	
	/**
	 * Variable containing the standard horizontal size for boards.
	 */
	public static final long STANDARD_BOARD_HORIZONTAL_SIZE = 10L;
	
	/**
	 * Variable containing the standard vertical size for boards.
	 */
	public static final long STANDARD_BOARD_VERTICAL_SIZE = 10L;
	
	/**
	 * Initializes a new board with standard size values.
	 * 
	 * @post	The new size of the board is set to sizeX by sizeY.
	 * 			| new.getSize() == {STANDARD_BOARD_HORIZONTAL_SIZE, STANDARD_BOARD_VERTICAL_SIZE}
	 * @post	The new board isn't terminated.
	 * 			| new.isTerminated() == false
	 */
	public Board(){
		this(STANDARD_BOARD_HORIZONTAL_SIZE, STANDARD_BOARD_VERTICAL_SIZE);
	}
	
	/**
	 * Creates a new board with the given size for the first (horizontal)
	 * and second (vertical) dimension.
	 * 
	 * @param 	sizeX
	 * 			The size of the board in the first 'X', horizontal dimension.
	 * @param 	sizeY
	 * 			The size of the board in the second 'Y', vertical dimension.
	 * @post	The new size of the board is set to sizeX by sizeY.
	 * 			| new.getSize() == {sizeX, sizeY}
	 * @post	The new board isn't terminated.
	 * 			| new.isTerminated() == false
	 * @throws 	IllegalArgumentException
	 * 			At least one of the given size values is invalid for a board.
	 * 			| !isValidSizeAt(Dimension.HORIZONTAL, sizeX) || !isValidSizeAt(Dimension.VERTICAL,sizeY))
	 */
	public Board(long sizeX, long sizeY) throws IllegalArgumentException {
		if(!isValidSizeAt(Dimension.HORIZONTAL, sizeX) || !isValidSizeAt(Dimension.VERTICAL,sizeY))
			throw new IllegalArgumentException("At least one of the given sizes is invalid.");
		size = new long[]{sizeX, sizeY};
		this.isTerminated = false;
	}
	
	/**
	 * Terminates this board and all board models situated on this board.
	 * 
	 * @effect	All board models on this board are terminated.
	 * 			| for every position in boardModels.keySet() :
	 * 			| 	 for each model in boardModels.get(position) :
	 * 			|		model.terminate()
	 * @post	This board is terminated.
	 * 			| new.isTerminated() == true
	 */
	@Override
	public void terminate() {
		if(!isTerminated){
			for(Object position : boardModels.keySet().toArray()){
				List<BoardModel> temp = boardModels.get(position);
				while(temp.size() != 0){
					temp.get(0).terminate();
				}
			}
			this.isTerminated = true;
		}
	}

	/**
	 * Returns whether this board is terminated.
	 */
	@Basic @Raw @Override
	public boolean isTerminated(){
		return this.isTerminated;
	}
	
	/**
	 * Variable registering whether this board is terminated.
	 */
	private boolean isTerminated;

	/**
	 * Returns the amount of dimensions of every board.
	 * 
	 * @note	For a two dimensional boards the amount of dimensions is two.
	 */
	@Immutable
	public static int getNbDimensions(){
		return BOUNDARIES.length;
	}
	
	/**
	 * Returns the maximum boundary value according to the given dimension for every board.
	 * 
	 * @param 	dimension
	 * 			The dimension of which the boundary has to be returned.
	 * @return	Returns the maximum boundary according to the given dimension for every board.
	 * 			| result == BOUNDARIES[dimension.getDimensionnr()-1]
	 * @throws	IllegalDimensionException
	 * 			The given dimension is invalid.
	 * 			| !Dimension.isValidDimension(dimension)
	 */
	@Immutable
	public static long getBoundaryAt(Dimension dimension)
			throws IllegalDimensionException{
		if(!Dimension.isValidDimension(dimension))
			throw new IllegalDimensionException("The given dimension is invalid.");
		return BOUNDARIES[dimension.getDimensionnr()-1];
	}
	
	/**
	 * Returns the maximum boundary values for every board.
	 */
	@Basic @Immutable
	public static long[] getBoundaries(){
		return BOUNDARIES;
	}

	/**
	 * Array storing the maximum boundaries for every board.
	 */
	private static final long[] BOUNDARIES = new long[]{Long.MAX_VALUE, Long.MAX_VALUE};
	
	/**
	 * Returns whether the given size is valid at the given dimension.
	 * 
	 * @param 	dimension
	 * 			The dimension of which the size has to be checked.
	 * @return	The size must be equal or greater than zero
	 * 			and less to the boundary value corresponding to
	 * 			the given dimension. The given dimension must be a
	 * 			valid dimension.
	 * 			| result == (Dimension.isValidDimension(dimension))
	 * 			|			&& (size >= 0) && (size <= getBoundaryAt(dimension))
	 */
	public static boolean isValidSizeAt(Dimension dimension, long size){
		return (Dimension.isValidDimension(dimension)) && (size >= 0) && (size <= getBoundaryAt(dimension));
	}

	/**
	 * Returns the size according to the given dimension of this board.
	 * 
	 * @param 	dimension
	 * 			The dimension according to which the size is returned.
	 * 			| result == getSize()[dimension.getDimensionnr()-1]
	 * @throws	IllegalDimensionException
	 * 			The given dimension is invalid.
	 * 			| !Dimension.isValidDimension(dimension)
	 */
	@Raw @Immutable
	public long getSizeAt(Dimension dimension)
			throws IllegalDimensionException{
		if(!Dimension.isValidDimension(dimension))
			throw new IllegalDimensionException("The given dimension is invalid.");
		return size[dimension.getDimensionnr()-1];
	}
	
	/**
	 * Returns the size of this board.
	 * 
	 * @note	size[0]	corresponds with the 'X', horizontal size of this board.
	 * 			size[1] corresponds with the 'Y', vertical size of this board.
	 */
	@Basic @Raw @Immutable
	public long[] getSize(){
		return size.clone();
	}
	
	/**
	 * Array storing the size of this board.
	 * 
	 * @note	Position (0,0) is always included in the board.
	 * 			This means that a 20x20 board always have a
	 * 			physical size of 21 by 21.
	 */
	private final long[] size;
	
	/**
	 * Returns a description of this board.
	 * 
	 * @return	Returns a description of this board.
	 * 			| result.equals("Horizontal:" + " " + getSizeAt(Dimension.HORIZONTAL) + "\n" + "Vertical:" + " "+ getSizeAt(Dimension.VERTICAL))
	 */
	public String getDescription(){
		return "Horizontal:" + " " + getSizeAt(Dimension.HORIZONTAL) + "\n" + "Vertical:" + " "+ getSizeAt(Dimension.VERTICAL);
	}

	/**
	 * Checks if this board contains the given coordinates.
	 * 
	 * @param 	coordinates
	 * 			The coordinates that have to be checked.
	 * @return	True if and only if the given coordinates
	 * 			are valid for this board.
	 * 			| result == (coordinates.length == getNbDimensions()) &&
	 * 			| (for i from 1 by 1 till getNbDimensions()) : 
	 * 			|	canHaveAsCoordinate(coordinates[i-1],Dimension.dimensionFromInt(i))
	 */
	@Raw
	public boolean canHaveAsCoordinates(long[] coordinates){
		if(coordinates.length != getNbDimensions())
			return false;
		for(int i=1; i<=getNbDimensions(); i++){
			if(!canHaveAsCoordinate(coordinates[i-1],Dimension.dimensionFromInt(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if this board contains the given coordinate for the given dimension.
	 * 
	 * @param 	coordinate
	 * 			The coordinate that has to be checked.
	 * @param 	dimension
	 * 			The dimension that corresponds to the given coordinate.
	 * @return	true if and only if the given coordinate
	 * 			corresponding to the given dimension is valid for this board.
	 * 			| result == Dimension.isValidDimension(dimension) &&
	 * 			| 	(0<=coordinate && coordinate<=getSizeAt(dimension))
	 */
	@Raw
	public boolean canHaveAsCoordinate(long coordinate, Dimension dimension){
		return Dimension.isValidDimension(dimension) && (0<=coordinate && coordinate<=getSizeAt(dimension));
	}
	
	/**
	 * Checks if the given position is already used as key value
	 * for the board model collection of this board.
	 * 
	 * @param 	position
	 * 			The position that has to be checked.
	 * @return	True if and only if the given position is
	 * 			already used as key value.
	 * 			| result == getAllBoardModels().containsKey(position)
	 */
	@Raw
	public boolean containsPositionKey(Position position){
		return boardModels.containsKey(position);
	}
	
	/**
	 * Checks if the given board model is situated on a position on this board.
	 * 
	 * @param 	boardModel
	 * 			The board model that has to be checked.
	 * @return	True if and only if the board model is situated on a position on this board.
	 * 			| for each key in boardModels.keySet():
	 * 			| 	if(getBoardModelsAt(key).contains(boardModel)) then
	 * 			| 	result == true
	 * 			| end for
	 * 			| result == false
	 */
	@Raw
	public boolean containsBoardModel_allCheck(BoardModel boardModel){
		for(Position key : boardModels.keySet()){
			if(getBoardModelsAt(key).contains(boardModel))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given board model is situated on its position on this board.
	 * 
	 * @param 	boardModel
	 * 			The board model that has to be checked.
	 * @return	True if and only if the board model is situated on its position on this board,
	 * 			if this position doesn't refer the null reference.
	 * 			| result == boardModel.getPosition() != null && containsPositionKey(boardModel.getPosition())
	 * 			|			&& getBoardModelsAt(boardModel.getPosition()).contains(boardModel)
	 */
	@Raw
	public boolean containsBoardModel_positionCheck(BoardModel boardModel){
		return boardModel.getPosition() != null && containsPositionKey(boardModel.getPosition()) 
				&& getBoardModelsAt(boardModel.getPosition()).contains(boardModel);
	}
	
	/**
	 * Checks if the given board model is situated on the given position on this board.
	 * 
	 * @param 	boardModel
	 * 			The board model that has to be checked.
	 * @param	position
	 * 			The position that has to be checked.
	 * @return	True if and only if the board model is situated on the given position on this board.
	 * 			| result == position != null && containsPositionKey(position)
	 * 			|			&& getBoardModelsAt(position).contains(boardModel)
	 */
	@Raw
	public boolean containsBoardModel(BoardModel boardModel, Position position){
		return position != null && containsPositionKey(position) && getBoardModelsAt(position).contains(boardModel);
	}
	
	/**
	 * Checks if the given board model could be added to this board onto
	 * the given position.
	 * 
	 * @param 	position
	 * 			The position that has to be checked.
	 * @param 	boardModel
	 * 			The board model that has to be added to this board.
	 * @return	The board of the given board model may refer
	 * 			the null reference only and the given board model
	 * 			could be positioned on the given board at the given position.
	 * 			| result == canHaveBoardModelAtNoBindingCheck(position, boardModel) && (boardModel.getBoard() == null)
	 */
	@Raw
	public boolean canHaveBoardModelAt(Position position, @Raw BoardModel boardModel){
		return canHaveBoardModelAtNoBindingCheck(position, boardModel) && (boardModel.getBoard() == null);
	 }
	
	/**
	 * Checks if the given board model could be added to this board onto
	 * the given position. No board binding check included.
	 * 
	 * @param 	position
	 * 			The position that has to be checked.
	 * @param 	boardModel
	 * 			The board model that has to be added to this board.
	 * @return	This board may not be terminated.
	 * 			| if(this.isTerminated())
	 * 			| 	then result == false
	 * @return	The given board model may not be terminated.
	 * 			The given board model may not refer the null reference.
	 * 			The given position may not refer the null reference.
	 * 			| if(boardModel == null || boardModel.isTerminated() || position == null)
	 * 			| 	then result == false
	 * @return	The coordinates of the given position must be valid for this board.
	 * 			| if(!canHaveAsCoordinates(position.getCoordinates())
	 * 			| 	then result == false
	 * @return	If there are already positioned models situated on the given position
	 * 			there is checked for each situated board model that the given board model
	 * 			could be situated together with the positioned model. 
	 * 			If this is not possible for at least one positioned model, the result is false.
	 * 			| if(containsPositionKey(position)) then
	 * 			| for each BoardModel model in getAllBoardModels().get(position) do :
	 * 			| 	if(!model.canSharePositionWith(boardModel))
	 * 			| 		then result == false
	 * @return	In all other cases:
	 * 			| else then result == true
	 */
	@Raw
	public boolean canHaveBoardModelAtNoBindingCheck(Position position, @Raw BoardModel boardModel){
		if(this.isTerminated())
			return false;		
		if(boardModel == null || boardModel.isTerminated() || position == null)
			return false;
		if(!canHaveAsCoordinates(position.getCoordinates()))
			return false;
		if(containsPositionKey(position)){
			for(BoardModel model : boardModels.get(position)){				
				if(!model.canSharePositionWith(boardModel))
					return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the board model collection of this board is valid.
	 * 
	 * @return	True if and only if the board model collection of this board is valid.
	 * 			This means that there could not exist board models situated on this board
	 * 			that are terminated or the model, its board or its position is referring
	 * 			the null reference or its board is not referring this board or its position
	 * 			is not referring the key under which that board model is stocked in this board's
	 * 			board model collection or the position has invalid coordinates for this board.
	 * 			All board models located on the same position, must have the ability to share a position.
	 * 			If this board is terminated then there may not exist board models situated on this board.
	 * 			Beside that it's not allowed that there exists a key referring the null reference
	 * 			or exist keys that refer a value that refers the null reference or refers a collection
	 * 			with no entries.
	 * 			| result ==
	 * 			| if(isTerminated())
	 * 			|	then boardModels.keySet().size() == 0
	 * 			| else
	 * 			| 	for each Position key in boardModels.keySet() :
	 * 			| 		getBoardModelsAt(key) != null &&
	 * 			| 		getBoardModelsAt(key).size() != 0 &&
	 * 			|		for each BoardModel model1 in getBoardModelsAt(key) :
	 * 			|			model1 != null &&
	 * 			|			!model1.isTerminated() &&
	 * 			|			model1.getPosition() != null &&
	 * 			|			canHaveAsCoordinates(model1.getPosition().getCoordinates()) &&
	 * 			|			model1.getBoard() == this &&
	 * 			|			model1.getPosition().equals(key) &&
	 * 			|			(for each BoardModel model2 in getBoardModelsAt(key) && model1!=model2 :
	 * 			|				model1.canSharePositionWith(model2))
	 */
	@Raw
	public boolean hasProperBoardModels(){
		for(Position key : boardModels.keySet()){
			if(this.isTerminated)
				return false;
			List<BoardModel> temp = getBoardModelsAt(key);
			if(temp == null || temp.size() == 0)
				return false;
			
			for(int i=0; i<temp.size(); i++){
				BoardModel model = temp.get(i);
				if(model == null || model.isTerminated())
					return false;
				if(model.getPosition() == null || !canHaveAsCoordinates(model.getPosition().getCoordinates()))
					return false;
				if(model.getBoard() != this || !model.getPosition().equals(key))
					return false;
				for(int j=i+1; j<temp.size(); j++){
					BoardModel other = temp.get(j);
					if(!model.canSharePositionWith(other)){
						return false;
					}
				}
			}	
		}
		return true;
	}
		
	/**
	 * Adds the given board model to this board.
	 * 
	 * @param 	position
	 * 			The position of which the coordinates corresponds
	 * 			to the location of the board model on this board.
	 * @param 	boardModel
	 * 			The board model that has to be added to this board.
	 * @post	Adds the given board model to the collection of board models
	 * 			of this board.
	 * 			| new.getAllBoardModels().get(position).contains(boardModel)
	 * @effect	Updates the position and board of the given board model.
	 * 			| boardModel.setBoard(new.this) &&
	 * 			| boardModel.setPosition(position)
	 * 			| [SEQUENTIAL]
	 * @throws	IllegalArgumentException
	 * 			The given board model could not be positioned
	 * 			onto the given position on this board.
	 * 			| !canHaveBoardModelAt(position, boardModel)
	 */
	public void addBoardModelAt(Position position, @Raw BoardModel boardModel)
		throws IllegalArgumentException{
		if(!canHaveBoardModelAt(position, boardModel))
			throw new IllegalArgumentException("The given board model could not be added to the given position.");
		if(!containsPositionKey(position))
			boardModels.put(position, new ArrayList<BoardModel>());
		boardModels.get(position).add(boardModel);
		boardModel.setBoard(this);
		boardModel.setPosition(position);
	}
	
	/**
	 * Returns all the board models located at the given position
	 * on this board.
	 * 
	 * @param 	position
	 * 			The position of which all the corresponding board models
	 * 			have to be returned.
	 * @return	If and only if there is already an existing entry, matching
	 * 			the given position, this entry is returned.
	 * 			(Returns an unmodifiable list)
	 * 			| if(boardModels.containsKey(position)) then
	 * 			| 	result == getAllBoardModels().get(position)
	 * @return	If no existing entry matches the given position,
	 * 			the null reference is returned.
	 * 			| if(!containsPositionKey(position)) then
	 * 			| 	result == null
	 */
	@Raw
	public List<BoardModel> getBoardModelsAt(Position position){
		if(!containsPositionKey(position))
			return null;
		return Collections.unmodifiableList(boardModels.get(position));
	}
	
	/**
	 * Returns all the board models of the given class located at the given position
	 * on this board.
	 * 
	 * @param 	position
	 * 			The position of which all the corresponding board models
	 * 			have to be returned.
	 * @param	clazz
	 * 			The specific class (in the board model hierarchy) for the
	 * 			returned board models.
	 * @return	The result contains every board model that is of the given
	 * 			subclass at the given position of this board
	 * 			| for each T extends BoardModel model in getBoardModelsAt(position) :
	 * 			| 	result.contains(model)
	 * @return	The result only contains board models of the given subclass
	 * 			| for each T extends BoardModel model in result :
	 * 			| 	clazz.isInstance(model)
	 * @return	If no existing entry matches the given position,
	 * 			the null reference is returned.
	 * 			| if(!containsPositionKey(position))
	 * 			| 	then result == null
	 */
	@Raw
	public <T extends BoardModel> List<T> getBoardModelsClassAt(Position position, Class<T> clazz){
		if(!containsPositionKey(position))
			return null;
		List<T> temp = new ArrayList<T>();
		for(BoardModel model : boardModels.get(position)){
			if(clazz.isInstance(model))
				temp.add(clazz.cast(model));	
		}
		return Collections.unmodifiableList(temp);
	}
	
	/**
	 * Returns all the strict board models of the given class located at the given position
	 * on this board.
	 * 
	 * @param 	position
	 * 			The position of which all the corresponding board models
	 * 			have to be returned.
	 * @param	clazz
	 * 			The specific class (in the board model hierarchy) for the
	 * 			returned strict board models.
	 * @return	The result contains every board model that is of the given
	 * 			subclass at the given position of this board
	 * 			| for each T extends BoardModel model in getBoardModelsAt(position) :
	 * 			| 	result.contains(model)
	 * @return	The result only contains strict board models of the given subclass
	 * 			| for each T extends BoardModel model in result :
	 * 			| 	model.getClass() == clazz
	 * @return	If no existing entry matches the given position,
	 * 			the null reference is returned.
	 * 			| if(!containsPositionKey(position))
	 * 			| 	then result == null
	 */
	@Raw
	public <T extends BoardModel> List<T> getStrictBoardModelsClassAt(Position position, Class<T> clazz){
		if(!containsPositionKey(position))
			return null;
		List<T> temp = new ArrayList<T>();
		for(BoardModel model : boardModels.get(position)){
			if(model.getClass() == clazz)
				temp.add(clazz.cast(model));	
		}
		return Collections.unmodifiableList(temp);
	}
	
	/**
	 * Returns an unmodifiable view of all the board models located on this board.
	 */
	@Basic @Raw
	public Map<Position, ArrayList<BoardModel>> getAllBoardModels(){
		return Collections.unmodifiableMap(boardModels);
	}
	
	/**
	 * Returns all board models of given type that are located on this board.
	 * 
	 * @param 	BoardModelType
	 * 			The board model types to look for.
	 * @return	The result contains every board model that is of the given
	 * 			subclass that is located on this board
	 * 			| for each T extends BoardModel model in getAllBoardModels() :
	 * 			| 	result.contains(model)
	 * @return	The result only contains board models of the given subclass
	 * 			| for each T extends BoardModel model in result :
	 * 			| 	BoardModelType.isInstance(model)
	 */
	@Raw
	public <T extends BoardModel> Set<T> getAllBoardModelsClass(Class<T> BoardModelType){
		Set<T> temp = new HashSet<T>();
		for (Position pos : boardModels.keySet()){
			temp.addAll(((List<T>)getBoardModelsClassAt(pos, BoardModelType)));
		}
		return Collections.unmodifiableSet(temp);
	}
	
	/**
	 * Returns all the strict board models of given type that are located on this board.
	 * 
	 * @param 	BoardModelType
	 * 			The board model types to look for.
	 * @return	The result contains every board model that is of the given
	 * 			subclass that is located on this board
	 * 			| for each T extends BoardModel model in getAllBoardModels() :
	 * 			| 	result.contains(model)
	 * @return	The result only contains strict board models of the given subclass
	 * 			| for each T extends BoardModel model in result :
	 * 			| 	BoardModelType.getClass() == model.getClass()
	 */
	@Raw
	public <T extends BoardModel> Set<T> getAllStrictBoardModelsClass(Class<T> BoardModelType){
		Set<T> temp = new HashSet<T>();
		for(Position pos : boardModels.keySet()){
			temp.addAll(((List<T>)getStrictBoardModelsClassAt(pos, BoardModelType)));
		}
		return Collections.unmodifiableSet(temp);
	}
	
	/**
	 * Returns the amount of board models located on this board.
	 * 
	 * @return	Returns the amount of board models located on this board.
	 * 			| count = 0
	 * 			| for each Position key in getAllBoardModels().keySet() :
	 * 			| 	count = count + getNbBoardModelsAt(key)
	 * 			| end for
	 * 			| result == count
	 */
	@Raw
	public int getNbBoardModels(){
		int count = 0;
		for(Position key : boardModels.keySet())
			count = count + getNbBoardModelsAt(key);
		return count;
	}
	
	/**
	 * Returns the amount of board models located on this board
	 * on the given position.
	 * 
	 * @return	Returns the amount of board models located on this board
	 * 			on the given position.
	 * 			| if(containsPositionKey(position))
	 * 			| 	then result == getAllBoardModels().get(position).size()
	 * @throws	If the given position isn't occupied the result
	 * 			equals zero.
	 * 			| if(!containsPositionKey(position))
	 * 			| 	then result == 0
	 */
	@Raw
	public int getNbBoardModelsAt(Position position)
		throws IllegalArgumentException{
		if(!containsPositionKey(position))
			return 0;
		return boardModels.get(position).size();
	}
	
	/**
	 * Moves the given board model that is located on this board to the given position.
	 * 
	 * @param 	boardModel
	 * 			The board model that has to be moved.
	 * @param 	position
	 * 			The position to move the board model to.
	 * @effect 	The board model will be moved to the new position by first
	 * 			removing it from its current position and adding it to the new position
	 *			| removeBoardModel(boardModel) &&
	 *			| addBoardModelAt(position, boardModel)
	 *			| [SEQUENTIAL]
	 * @throws	NullPointerException
	 * 			The board model or the position refers the null reference.
	 * 			| boardModel == null || position == null
	 * @throws	IllegalArgumentException
	 * 			The given board model is not located on its position on this board.
	 * 			| !containsBoardModel_positionCheck(boardModel)
	 * @throws	IllegalArgumentException
	 * 			The given position is an invalid position for the given board model.
	 * 			| !canHaveBoardModelAtNoBindingCheck(position, boardModel)
	 */
	public void moveBoardModelTo(BoardModel boardModel, Position position)
			throws NullPointerException, IllegalArgumentException{
		if (boardModel == null || position == null)
			throw new NullPointerException("At least one of the given arguments refers the null reference.");
		if (!containsBoardModel_positionCheck(boardModel))
			throw new IllegalArgumentException("The given boardmodel is not located on its position on this board");
		if (!canHaveBoardModelAtNoBindingCheck(position, boardModel))
			throw new IllegalArgumentException("The given boardmodel can not move to the given position: " + boardModel + "pos: " + position);
		removeBoardModel(boardModel);
		addBoardModelAt(position, boardModel);
	}
	
	/**
	 * A map collection with list collections of all board models
	 * situated at the same position and with the corresponding position as key.
	 */
	private	Map<Position,ArrayList<BoardModel>> boardModels = new HashMap<Position,ArrayList<BoardModel>>();
	
	/**
	 * Removes the given board model from this board.
	 * 
	 * @param	boardModel
	 * 			The board model that has to be removed form this board.
	 * @effect	If and only if the given board model is situated on this board,
	 * 			the board model is removed from this board and the board model
	 * 			its board is terminated.
	 * 			| if (getBoardModelsAt(boardModel.getPosition()).contains(boardModel)
	 * 			| && boardModel.getBoard() == this)
	 * 			| 	then boardModel.setBoard(null)
	 * @effect	If the given board model is the only board model
	 * 			located on its position on this board. The whole entry
	 * 			corresponding to the position of the given robot is removed
	 * 			form the board model collection of this board.
	 * 			| if(getBoardModelsAt(boardModel.getPosition())
	 * 			| 	then getAllBoardModels().remove(boardModel.getPosition())
	 */
	public void removeBoardModel(BoardModel boardModel){
		if(boardModel.getBoard() == this){
			List<BoardModel> temp = boardModels.get(boardModel.getPosition());
			// May never return index -1
			temp.remove(temp.indexOf(boardModel));
			if(temp.size() == 0)
				boardModels.remove(boardModel.getPosition());
			boardModel.setBoard(null);
		}
	}
}
