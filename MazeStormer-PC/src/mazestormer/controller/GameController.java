package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import mazestormer.controller.PlayerEvent.EventType;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.IPlayer;
import mazestormer.player.Player;

public class GameController extends SubController implements IGameController {

	private Map<IPlayer, IPlayerController> pcs = new LinkedHashMap<IPlayer, IPlayerController>();

	public GameController(MainController mainController) {
		super(mainController);
	}

	@Override
	public IPlayerController getPlayerController(IPlayer player) {
		return pcs.get(player);
	}

	@Override
	public IPlayerController getPersonalPlayerController() {
		return getPlayerController(getMainController().getPlayer());
	}

	@Override
	public void addPlayer(String playerID) {
		checkNotNull(playerID);
		addPlayer(new Player(playerID, new ObservableRobot()));
	}

	@Override
	public void addPlayer(Player p) {
		checkNotNull(p);
		this.pcs.put(p, new PlayerController(this.getMainController(), p));
		postEvent(new PlayerEvent(EventType.PLAYER_ADDED, p));
	}

	@Override
	public void removePlayer(Player p) {
		checkNotNull(p);
		this.pcs.remove(p);
		postEvent(new PlayerEvent(EventType.PLAYER_REMOVED, p));
	}

	@Override
	public IPlayer getPlayer(String playerID) {
		for (IPlayer p : this.pcs.keySet()) {
			if (p.getPlayerID().equals(playerID)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Collection<IPlayerController> getPlayerControllers() {
		return Collections.unmodifiableCollection(pcs.values());
	}

	@Override
	public void logToSpecific(String playerID, String message) {
		for (IPlayer p : this.pcs.keySet()) {
			if (p.getPlayerID().equals(playerID))
				((Player) p).getLogger().info(message);
		}
	}

	@Override
	public void logToAll(String message) {
		for (IPlayer p : this.pcs.keySet()) {
			((Player) p).getLogger().info(message);
		}
	}
}