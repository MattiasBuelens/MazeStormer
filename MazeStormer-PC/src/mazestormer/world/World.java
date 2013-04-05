package mazestormer.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.maze.Maze;
import mazestormer.player.AbsolutePlayer;
import mazestormer.player.RelativePlayer;

public class World {

	private final AbsolutePlayer localPlayer;
	private final Maze maze = new Maze();
	private final Logger logger;
	private final Map<String, AbsolutePlayer> players = new HashMap<String, AbsolutePlayer>();
	private final List<WorldListener> listeners = new ArrayList<WorldListener>();

	public World(AbsolutePlayer localPlayer) {
		this.localPlayer = localPlayer;
		addPlayer(localPlayer);

		logger = Logger.getLogger(World.class.getSimpleName());
		logger.setLevel(Level.ALL);
	}

	public World(RelativePlayer localPlayer) {
		this(new AbsolutePlayer(localPlayer));
	}

	public Maze getMaze() {
		return maze;
	}

	public Collection<? extends AbsolutePlayer> getPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}

	public AbsolutePlayer getLocalPlayer() {
		return this.localPlayer;
	}

	public AbsolutePlayer getPlayer(String playerID) {
		return players.get(playerID);
	}

	public void addPlayer(RelativePlayer player) {
		addPlayer(new AbsolutePlayer(player));
	}

	public void addPlayer(AbsolutePlayer player) {
		players.put(player.getPlayerID(), player);
		for (WorldListener listener : listeners) {
			listener.playerAdded(player);
		}
	}

	public void removePlayer(AbsolutePlayer player) {
		players.remove(player.getPlayerID());
		for (WorldListener listener : listeners) {
			listener.playerRemoved(player);
		}
	}

	public void renamePlayer(String playerID, String newPlayerID) {
		// Remove old name
		AbsolutePlayer player = getPlayer(playerID);
		players.remove(playerID);
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

	public Logger getLogger() {
		return logger;
	}
}
