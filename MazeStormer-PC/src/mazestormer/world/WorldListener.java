package mazestormer.world;

import mazestormer.game.player.Player;

public interface WorldListener {

	public void playerAdded(Player player);

	public void playerRemoved(Player player);

	public void playerRenamed(Player player);

}
