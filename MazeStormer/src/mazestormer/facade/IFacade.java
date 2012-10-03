package mazestormer.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mazestormer.board.Board;
import mazestormer.board.Position;
import mazestormer.model.BoardModel;
import mazestormer.model.Robot;
import mazestormer.model.Wall;

/**
 * An interface that should be implemented by all classes
 * which must provide a UI Interaction.
 * 
 * @author 	Team Bronze
 * @version	
 *
 */
public interface IFacade{

	public String getFeedback();

	// BOARD
	public Board createBoard(long width, long height);
	public long getBoundaryX(Board board) throws NullPointerException;
	public long getBoundaryY(Board board) throws NullPointerException;
	public void terminateBoard(Board board) throws NullPointerException;
	public Map<Position, ArrayList<BoardModel>> getAllBoardModels(Board board) throws NullPointerException;
	public <T extends BoardModel> List<T> getBoardModelsClassAt(Board board, Position position, Class<T> clazz) throws NullPointerException;
	public <T extends BoardModel> Set<T> getAllBoardModelsClass(Board board, Class<T> boardModelType) throws NullPointerException;
	public <T extends BoardModel> Set<T> getAllStrictBoardModelsClass(Board board, Class<T> boardModelType) throws NullPointerException;
	public int getNbBoardModels(Board board) throws NullPointerException;
	public String getBoardDescription(Board board) throws NullPointerException;
	
	// BOARDMODEL
	public void putBoardModel(Board board, long x, long y,BoardModel model) throws NullPointerException;
	public long getBoardModelX(BoardModel model) throws NullPointerException, IllegalStateException;
	public long getBoardModelY(BoardModel model) throws NullPointerException, IllegalStateException;
	
	// ROBOT
	public Robot createRobot(int orientation);
	public int getOrientation(Robot robot) throws NullPointerException;
	public void move(Robot robot) throws NullPointerException;
	public void turnClockwise(Robot robot) throws NullPointerException;
	public void turnCounterClockwise(Robot robot) throws NullPointerException;
		
	// WALL
	public Wall createWall();
}
