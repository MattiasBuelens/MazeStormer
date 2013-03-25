package mazestormer.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import mazestormer.controller.PlayerEvent.EventType;
import mazestormer.player.PlayerIdentifier;
import mazestormer.player.Player;
import mazestormer.world.WorldListener;

public class GameController extends SubController implements IGameController {

	private Map<PlayerIdentifier, IPlayerController> pcs = new LinkedHashMap<PlayerIdentifier, IPlayerController>();

	public GameController(MainController mainController) {
		super(mainController);
		getMainController().getWorld().addListener(new Listener());
	}

	@Override
	public IPlayerController getPlayerController(PlayerIdentifier player) {
		return pcs.get(player);
	}

	@Override
	public IPlayerController getPersonalPlayerController() {
		return getPlayerController(getMainController().getPlayer());
	}

	@Override
	public Collection<IPlayerController> getPlayerControllers() {
		return Collections.unmodifiableCollection(pcs.values());
	}

	private void onPlayerAdded(Player p) {
		this.pcs.put(p, new PlayerController(this.getMainController(), p));
		postEvent(new PlayerEvent(EventType.PLAYER_ADDED, p));
	}

	private void onPlayerRemoved(Player p) {
		this.pcs.remove(p);
		postEvent(new PlayerEvent(EventType.PLAYER_REMOVED, p));
	}

	private void onPlayerRenamed(Player p) {
		postEvent(new PlayerEvent(EventType.PLAYER_RENAMED, p));
	}

	private class Listener implements WorldListener {

		@Override
		public void playerAdded(Player player) {
			onPlayerAdded(player);
		}

		@Override
		public void playerRemoved(Player player) {
			onPlayerRemoved(player);
		}

		@Override
		public void playerRenamed(Player player) {
			onPlayerRenamed(player);
		}

	}

}