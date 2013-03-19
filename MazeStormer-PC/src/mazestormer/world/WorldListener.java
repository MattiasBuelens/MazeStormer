package mazestormer.world;

import mazestormer.player.Player;

public interface WorldListener {

	public void playerAdded(Player player);

	public void playerRemoved(Player player);
}
