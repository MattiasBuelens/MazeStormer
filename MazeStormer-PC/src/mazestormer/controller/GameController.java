package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mazestormer.player.Player;

public class GameController extends SubController implements IGameController{
	
	private static final int PERSONAL_ROBOT_INDEX = 0;

	public GameController(MainController mainController) {
		super(mainController);
	}
	
	@Override
	public int getAmountOfPlayerControllers() {
		return this.pcs.size();
	}
	
	@Override
	public boolean hasAsPlayerController(IPlayerController pc) {
		return this.pcs.contains(pc);
	}
	
	@Override
	public IPlayerController getPersonalPlayerController() {
		return this.pcs.get(PERSONAL_ROBOT_INDEX);
	}
	
	@Override
	public IPlayerController getPlayerControllerAt(int index) 
			throws IndexOutOfBoundsException {
		return this.pcs.get(index);
	}
	
	@Override
	public void addPlayer(Player p) {
		checkNotNull(p);
		addPlayerController(new PlayerController(this.getMainController(), p));
	}
	
	@Override
	public void addPlayerController(IPlayerController pc) {
		checkNotNull(pc);
		this.pcs.add(pc);
	}
	
	@Override
	public void removePlayerController(IPlayerController pc) {
		this.pcs.remove(pc);
	}
	
	@Override
	public List<IPlayerController> getPlayerControllers() {
		return Collections.unmodifiableList(pcs);
	}
	
	private List<IPlayerController> pcs = new ArrayList<IPlayerController>();

}
