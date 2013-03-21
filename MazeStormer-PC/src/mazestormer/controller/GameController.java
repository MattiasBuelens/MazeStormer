package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import peno.htttp.DisconnectReason;

import mazestormer.controller.PlayerEvent.EventType;
import mazestormer.observable.ObservableRobot;
import mazestormer.player.IPlayer;
import mazestormer.player.Player;
import mazestormer.world.WorldListener;

public class GameController extends SubController implements IGameController {

	private Map<IPlayer, IPlayerController> pcs = new LinkedHashMap<IPlayer, IPlayerController>();

	public GameController(MainController mainController) {
		super(mainController);
		getMainController().getWorld().addListener(new Listener());
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
	public boolean isPersonalPlayer(String playerID) {
		return getMainController().getPlayer().getPlayerID().equals(playerID);
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
	public void removeOtherPlayers() {
		Iterator<Map.Entry<IPlayer, IPlayerController>> it = this.pcs
				.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<IPlayer, IPlayerController> entry = it.next();
			Player player = (Player) entry.getKey();
			if (!isPersonalPlayer(player.getPlayerID())) {
				postEvent(new PlayerEvent(EventType.PLAYER_REMOVED, player));
				it.remove();
			}
		}
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
	public void logTo(String playerID, String message) {
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
	
	private class Listener implements WorldListener {

		@Override
		public void playerAdded(Player player) {
			addPlayer(player);
		}

		@Override
		public void playerRemoved(Player player) {
			removePlayer(player);
		}
	}
}