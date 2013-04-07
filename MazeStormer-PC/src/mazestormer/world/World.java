package mazestormer.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.infrared.Model;
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
		addModel(player.getIRRobot());
		
		for (WorldListener listener : listeners) {
			listener.playerAdded(player);
		}
	}

	public void removePlayer(AbsolutePlayer player) {
		players.remove(player.getPlayerID());
		
		//TODO: de oude robot zijn ir robot kan nog steeds in de set aanwezig zijn
		removeModel(player.getIRRobot());
		
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
	
	// TODO: Objects and ir circuits need to be added after parsing
	
	private final Set<Model> models = new HashSet<Model>();
	
	public void addModel(Model model) {
		this.models.add(model);
	}
	
	public void removeModel(Model model) {
		this.models.remove(model);
	}
	
	public Set<Model> getModels() {
		return Collections.unmodifiableSet(this.models);
	}
	
    public <T extends Model> Set<T> getAllStrictModelsClass(Class<T> clazz) {
       Set<T> temp = new HashSet<T>();
       for(Model model : this.models) {
    	   if (model.getClass() == clazz) {
    		   temp.add(clazz.cast(model));
    	   }
        }
        return Collections.unmodifiableSet(temp);
    }
    
    public <T extends Model> Set<T> getAllModelsClass(Class<T> modelType){
    	Set<T> temp = new HashSet<T>();
        for(Model model : this.models) {
     	   if (modelType.isInstance(model)) {
     		   temp.add(modelType.cast(model));
     	   }
         }
         return Collections.unmodifiableSet(temp);
    }
}
