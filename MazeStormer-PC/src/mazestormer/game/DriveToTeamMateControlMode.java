package mazestormer.game;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.maze.IMaze;
import mazestormer.maze.Tile;
import mazestormer.maze.path.MazeAStar;
import mazestormer.player.AbsolutePlayer;
import mazestormer.player.Player;
import mazestormer.util.LongPoint;

public class DriveToTeamMateControlMode extends ControlMode {
	
	AbsolutePlayer teamMate;
	MazeAStar mazeAStar;

	protected DriveToTeamMateControlMode(Player player,
			BarcodeMapping barcodeMapping) {
		super(player, barcodeMapping);
	}
	
	public DriveToTeamMateControlMode(Player player, BarcodeMapping barcodeMapping, AbsolutePlayer teamMate){
		this(player, barcodeMapping);
		this.teamMate = teamMate;
	}

	@Override
	public void takeControl(Driver driver) {
		log("Driving to team mate");		
	}

	@Override
	public void releaseControl(Driver driver) {
	}

	@Override
	public Tile nextTile(Tile currentTile) {
		// Het korste pad wordt tile per tile opnieuw berekend, terwijl dat 'waarschijnlijk' hetzelfde blijft, 
		// maar ik denk ni dat ge er van uit kunt gaan dat uw teammate ook het A* kortste pad gaat volgen
		IMaze maze = getMaze();
		//TODO: nog in relatief assemstelsel van team mate
		Pose teamMatePose = teamMate.getRobot().getPoseProvider().getPose();

		//TODO: nachecken; kloppen de positions? (tile vs in cm)
		Point teamMateLocation = maze.toTile(teamMatePose.getLocation());
		//TODO:
		//maze.getTileCenter(maze.getTileAt(tilePosition).getPosition());
		LongPoint teamMateLocation2 = new LongPoint((long)teamMateLocation.getX(), (long)teamMateLocation.getY());

		//Calculate shortest path to team mate
		mazeAStar = new MazeAStar(maze, currentTile.getPosition(), teamMateLocation2);

		//Go to the first tile of the shortest path
		return mazeAStar.findPath().get(0);
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		//No barcode action necessary in this part of the game
		return false;
	}

}
