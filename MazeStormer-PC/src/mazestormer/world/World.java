package mazestormer.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mazestormer.maze.Maze;
import mazestormer.player.Player;

public class World {

	private final Maze maze = new Maze();
	private final Map<String, Player> players = new HashMap<String, Player>();
	private final List<WorldListener> listeners = new ArrayList<WorldListener>();

	public Maze getMaze() {
		return maze;
	}

	public Collection<Player> getPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}

	public Player getPlayer(String playerID) {
		return players.get(playerID);
	}

	public void addPlayer(Player player) {
		players.put(player.getPlayerID(), player);
		for (WorldListener listener : listeners) {
			listener.playerAdded(player);
		}
	}

	public void removePlayer(Player player) {
		players.remove(player.getPlayerID());
		for (WorldListener listener : listeners) {
			listener.playerRemoved(player);
		}
	}

	public void renamePlayer(Player player, String newPlayerID) {
		// Remove old name
		players.remove(player.getPlayerID());
		// Set and add with new name
		player.setPlayerID(newPlayerID);
		players.put(newPlayerID, player);

		for (WorldListener listener : listeners) {
			listener.playerRenamed(player);
		}
	}

	public void addListener(WorldListener listener) {
		listeners.add(listener);
	}

	public void removeListener(WorldListener listener) {
		listeners.remove(listener);
	}

}
