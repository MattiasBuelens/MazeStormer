package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import mazestormer.game.Game;
import mazestormer.maze.DefaultMazeListener;
import mazestormer.maze.Tile;

public class TileReporter extends DefaultMazeListener implements Runnable {

	private final Game game;
	private final ScheduledExecutorService executor;

	private final BlockingQueue<Tile> queue = new LinkedBlockingQueue<Tile>();
	private ScheduledFuture<?> task;

	/**
	 * Delay in milliseconds.
	 */
	private static final long delay = 100;

	public TileReporter(Game game, ScheduledExecutorService executor) {
		this.game = game;
		this.executor = executor;
	}

	public void start() {
		stop();
		task = executor.scheduleWithFixedDelay(this, 0, delay, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (task == null)
			return;

		task.cancel(false);
		task = null;
		queue.clear();
	}

	@Override
	public void run() {
		List<Tile> tiles = new ArrayList<Tile>();
		queue.drainTo(tiles);
		game.sendTiles(tiles);
	}

	public void offer(Tile tile) {
		queue.add(tile);
	}

	public void offer(Collection<? extends Tile> tiles) {
		queue.addAll(tiles);
	}

	@Override
	public void tileExplored(Tile tile) {
		offer(tile);
	}

}