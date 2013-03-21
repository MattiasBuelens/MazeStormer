package mazestormer.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mazestormer.maze.Maze;
import mazestormer.player.Player;

public class World {

	private final Maze maze = new Maze();
	private final Set<Player> players = new HashSet<Player>();
	private final Set<WorldListener> listeners = new HashSet<WorldListener>();

	public Maze getMaze() {
		return maze;
	}

	public Collection<Player> getPlayers() {
		return Collections.unmodifiableSet(players);
	}

	public Player getPlayer(String playerID) {
		for (Player player : players) {
			if (player.getPlayerID().equals(playerID)) {
				return player;
			}
		}
		return null;
	}

	public void addPlayer(Player player) {
		players.add(player);
		for (WorldListener listener : listeners) {
			listener.playerAdded(player);
		}
	}

	public void removePlayer(Player player) {
		players.remove(player);
		for (WorldListener listener : listeners) {
			listener.playerRemoved(player);
		}
	}

	public void addListener(WorldListener listener) {
		listeners.add(listener);
	}

	public void removeListener(WorldListener listener) {
		listeners.remove(listener);
	}

}
