package mazestormer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mazestormer.board.Board;
import mazestormer.board.Dimension;
import mazestormer.board.Position;
import mazestormer.model.BoardModel;
import mazestormer.model.Direction;
import mazestormer.model.Robot;
import mazestormer.model.Wall;
import mazestormer.ui.IModelViewController;
import be.kuleuven.cs.som.annotate.*;

/**
 * This class contains all the functionality of the control and model layer
 * required for UI interaction.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public class ModelViewController implements IModelViewController{
	
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
	
	@Override
	public Board createBoard(long width, long height){
		try{
			return new Board(width, height);
		}
		catch(IllegalArgumentException e){
			setFeedback("Board creation failed: invalid sizes.");
			return null;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public long getBoundaryX(Board board) throws NullPointerException{
		return board.getBoundaryAt(Dimension.HORIZONTAL);
	}

	@SuppressWarnings("static-access")
	@Override
	public long getBoundaryY(Board board) throws NullPointerException{
		return board.getBoundaryAt(Dimension.VERTICAL);
	}

	@Override
	public void terminateBoard(Board board) throws NullPointerException{
		board.terminate();
	}

	@Override
	public Map<Position, ArrayList<BoardModel>> getAllBoardModels(Board board) throws NullPointerException{return board.getAllBoardModels();
	}
	
	@Override
	public <T extends BoardModel> List<T> getBoardModelsClassAt(Board board, Position position, Class<T> clazz) throws NullPointerException{
		return board.getBoardModelsClassAt(position, clazz);
	}

	@Override
	public <T extends BoardModel> Set<T> getAllBoardModelsClass(Board board,Class<T> boardModelType) throws NullPointerException{
		return board.getAllBoardModelsClass(boardModelType);
	}
	
	@Override
	public <T extends BoardModel> Set<T> getAllStrictBoardModelsClass(Board board,Class<T> boardModelType) throws NullPointerException{
		return board.getAllStrictBoardModelsClass(boardModelType);
	}

	@Override
	public int getNbBoardModels(Board board) throws NullPointerException{
		return board.getNbBoardModels();
	}

	@Override
	public String getBoardDescription(Board board) throws NullPointerException{
		return board.getDescription();
	}

	@Override
	public void putBoardModel(Board board, long x, long y, BoardModel model) throws NullPointerException{
		try{
			board.addBoardModelAt(new Position(x, y), model);
		}
		catch(IllegalArgumentException e){
			setFeedback("Unable to put board model.");
		}
	}

	@Override
	public long getBoardModelX(BoardModel model) throws NullPointerException, IllegalStateException{
		if(model.getBoard() == null)
			throw new IllegalStateException("The given model is not placed on a board.");
		return model.getPosition().getCoordinate(Dimension.HORIZONTAL);
	}

	@Override
	public long getBoardModelY(BoardModel model) throws NullPointerException, IllegalStateException{
		if(model.getBoard() == null)
			throw new IllegalStateException("The given model is not placed on a board.");
		return model.getPosition().getCoordinate(Dimension.VERTICAL);
	}

	@Override
	public Robot createRobot(int orientation){
		return new Robot(null, null, Direction.directionFromInt(orientation));
	}
	@Override
	public int getOrientation(Robot robot) throws NullPointerException{
		return robot.getDirection().getDirectionnr();
	}

	@Override
	public void move(Robot robot) throws NullPointerException{
		if(!robot.isTerminated()){
			try{
				robot.move();
			}
			catch(IllegalArgumentException e2){
				setFeedback("Not able to move");
			}
		}
		else
			setFeedback("Terminated robot: no move operation possible.");
	}

	@Override
	public void turnClockwise(Robot robot) throws NullPointerException{
		if(!robot.isTerminated())
			robot.turnClockwise();
		else
			setFeedback("Terminated robot: no turn operation possible.");
	}

	@Override
	public void turnCounterClockwise(Robot robot) throws NullPointerException{
		if(!robot.isTerminated())
			robot.turnCounterClockwise();
		else
			setFeedback("Terminated robot: no turn operation possible.");
	}

	@Override
	public Wall createWall(){
		return new Wall();
	}
}
