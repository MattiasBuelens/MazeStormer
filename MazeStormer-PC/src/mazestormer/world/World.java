package mazestormer.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.infrared.IRRobot;
import mazestormer.infrared.IRSeesaw;
import mazestormer.maze.DefaultMazeListener;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.AbsolutePlayer;
import mazestormer.player.Player;
import mazestormer.player.RelativePlayer;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public class World {

	private final AbsolutePlayer localPlayer;
	private final IMaze maze = new Maze();
	private final Logger logger;
	private final Map<String, AbsolutePlayer> players = new HashMap<String, AbsolutePlayer>();
	private final List<WorldListener> listeners = new ArrayList<WorldListener>();

	public World(AbsolutePlayer localPlayer) {
		this.localPlayer = localPlayer;
		addPlayer(localPlayer);

		getMaze().addListener(new SeesawListener());

		logger = Logger.getLogger(World.class.getSimpleName());
		logger.setLevel(Level.ALL);
	}

	public World(RelativePlayer localPlayer) {
		this(new AbsolutePlayer(localPlayer));
	}

	public IMaze getMaze() {
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

		// Call listeners
		for (WorldListener listener : listeners) {
			listener.playerAdded(player);
		}
	}

	public void removePlayer(AbsolutePlayer player) {
		players.remove(player.getPlayerID());

		// Call listeners
		for (WorldListener listener : listeners) {
			listener.playerRemoved(player);
		}
	}

	/**
	 * Remove all non-local players.
	 */
	public void removeOtherPlayers() {
		Iterator<AbsolutePlayer> it = players.values().iterator();
		while (it.hasNext()) {
			AbsolutePlayer player = it.next();
			if (player != getLocalPlayer()) {
				// Remove
				it.remove();
				// Call listeners
				for (WorldListener listener : listeners) {
					listener.playerRemoved(player);
				}
			}
		}
	}

	public void renamePlayer(String playerID, String newPlayerID) {
		// Remove old name
		AbsolutePlayer player = getPlayer(playerID);
		players.remove(playerID);
		// Set and add with new name
		player.setPlayerID(newPlayerID);
		players.put(newPlayerID, player);
		// Call listeners
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

	private Map<Seesaw, IRSeesaw> seesaws = new HashMap<Seesaw, IRSeesaw>();

	public Iterable<IRSeesaw> getSeesaws() {
		return Collections.unmodifiableCollection(seesaws.values());
	}

	private void addSeesaw(Tile tile) {
		if (tile.isSeesaw() && !seesaws.containsKey(tile.getSeesaw())) {
			Seesaw seesaw = tile.getSeesaw();
			seesaws.put(seesaw, new IRSeesaw(getMaze(), seesaw));
		}
	}

	private void removeSeesaws() {
		seesaws.clear();
	}

	public Iterable<IRRobot> getRobots() {
		return Collections2.transform(getPlayers(), robotFilter);
	}

	public Iterable<? extends Model> getAllModels() {
		return Iterables.concat(getSeesaws(), getRobots());
	}

	public <T extends Model> Iterable<T> getAllModels(Class<T> clazz) {
		return Iterables.filter(getAllModels(), clazz);
	}

	private static Function<Player, IRRobot> robotFilter = new Function<Player, IRRobot>() {
		@Override
		public IRRobot apply(Player player) {
			return player.getRobot();
		}
	};

	private class SeesawListener extends DefaultMazeListener {

		@Override
		public void tileAdded(Tile tile) {
			addSeesaw(tile);
		}

		@Override
		public void tileChanged(Tile tile) {
			addSeesaw(tile);
		}

		@Override
		public void mazeCleared() {
			removeSeesaws();
		}

	}

}
