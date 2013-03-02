package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;

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

	private void addPlayer(Player p) {
		checkNotNull(p);
		this.pcs.put(p, new PlayerController(this.getMainController(), p));
	}

	private void removePlayer(Player p) {
		checkNotNull(p);
		this.pcs.remove(p);
	}

	@Override
	public Collection<IPlayerController> getPlayerControllers() {
		return Collections.unmodifiableCollection(pcs.values());
	}

	@Subscribe
	public void onPlayerEvent(PlayerEvent e) {
		switch (e.getEventType()) {
		case PLAYER_ADDED:
			addPlayer((Player) e.getPlayer());
			break;
		case PLAYER_REMOVED:
			removePlayer((Player) e.getPlayer());
			break;
		default:
			break;
		}
	}

}