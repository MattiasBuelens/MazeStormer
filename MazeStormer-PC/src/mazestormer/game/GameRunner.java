package mazestormer.game;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;
import mazestormer.explore.ExplorerRunner;
import mazestormer.player.Player;
import mazestormer.robot.Navigator.NavigatorState;
import mazestormer.robot.NavigatorListener;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;

public class GameRunner extends
StateMachine<GameRunner, GameRunner.GameState> implements
StateListener<GameRunner.GameState>, NavigatorListener {
	
	ExplorerRunner explorerRunner;
	
	public GameRunner(Player player){
		this.explorerRunner = new ExplorerRunner(player);
		explorerRunner.setBarcodeMapping(new TeamTreasureTrekBarcodeMapping());
	}
	
	public enum GameState implements State<GameRunner, GameState> {
	}

}
