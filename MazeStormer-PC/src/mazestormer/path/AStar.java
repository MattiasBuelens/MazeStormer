package mazestormer.path;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * An implementation of the A* path finding algorithm.
 * 
 * @invar The start node is valid.
 * @invar The target node is valid.
 * 
 * @param <N>
 *            The node type.
 * @param <V>
 *            The cost value type.
 * 
 * @author Mattias Buelens
 * @author Thomas Goossens
 * @version 3.0
 */
public abstract class AStar<N extends Node<V>, V extends Comparable<? super V>> {

	/**
	 * Run the path finding algorithm and return the last evaluated node.
	 * 
	 * <p>
	 * If there exists a path to the target, the last node in the optimal path
	 * is returned. The complete path can be retrieved with
	 * {@link #reconstructPath(Node)}.
	 * </p>
	 * 
	 * <p>
	 * If there are no paths to the target, all reachable positions are
	 * evaluated and stored with their minimal cost in the node map.
	 * </p>
	 * 
	 * @return For all paths starting at the start node and ending at the target
	 *         node, the result is the last node in the path with the minimal
	 *         cost to reach the target by traveling along the path.
	 * 
	 * @return If no paths exist to the target, then every position which can be
	 *         reached from the starting position is contained in the node map
	 *         and is mapped to the last node in the optimal path to that
	 *         position.
	 */
	protected N run() {
		reset();
		N current = getStart();

		while (!openSet.isEmpty()) {
			// Remove from open set and add to closed set
			current = openSet.poll();
			closedSet.add(current.getPosition());
			nodeMap.put(current.getPosition(), current);

			// If this is the target, we're finished
			if (isTarget(current)) {
				openSet.clear();
				break;
			}

			// Inspect all neighbors of current node
			Iterable<? extends Node<V>> neighbors = current.getNeighbors();
			for (Node<V> neighborNode : neighbors) {
				@SuppressWarnings("unchecked")
				N neighbour = (N) neighborNode;
				Point2D neighborPosition = neighbour.getPosition();
				// Ignore invalid nodes
				if (!isValidNode(neighbour))
					continue;
				// If neighbor position is in closed set, don't check it again
				if (closedSet.contains(neighborPosition)) {
					continue;
				}

				// Get old neighbor as it is stored in the node map
				Node<V> oldNeighbor = getNodeByPosition(neighborPosition);
				boolean isBetterNeighbor = false;

				if (oldNeighbor == null) {
					// If no node at this position yet, add it
					isBetterNeighbor = true;
				} else {
					// If there is already a node at this position,
					// replace it if it has a higher G-score
					V oldG = oldNeighbor.getG();
					V newG = neighbour.getG();
					isBetterNeighbor = (newG.compareTo(oldG) < 0);
				}

				// If the new neighbor is better
				if (isBetterNeighbor) {
					// Calculate its H-score
					neighbour.calculateH(getTarget());
					// Remove the old one and add the new one
					openSet.remove(oldNeighbor);
					openSet.offer(neighbour);
					// Store in node map
					nodeMap.put(neighbour.getPosition(), neighbour);
				}
			}
		}

		return current;
	}

	/**
	 * Reset the algorithm to its initial state.
	 * 
	 * @effect The cost to reach the start node is reset to zero.
	 * @post The open set only contains the start node.
	 * @post The closed set is cleared.
	 * @post The node map is cleared.
	 * 
	 * @throws NullPointerException
	 *             If the open set is not effective.
	 */
	protected void reset() throws NullPointerException {
		// Clear sets
		nodeMap.clear();
		closedSet.clear();
		openSet.clear();

		// Reset start node and add to open set
		getStart().resetG();
		openSet.offer(getStart());
	}

	/**
	 * Reconstruct the path to reach the given node from its chain of previous
	 * nodes.
	 * 
	 * @param node
	 *            The node.
	 * 
	 * @pre The given node must originate from this algorithm's start node.
	 * 
	 * @return The first node in the resulting sequence is the start node and
	 *         the last node is the given node.
	 * @return Every node in the resulting sequence is preceded by its previous
	 *         node.
	 * @throws IllegalArgumentException
	 *             If the given node is not effective.
	 */
	@SuppressWarnings("unchecked")
	public List<N> reconstructPath(N node) {
		if (node == null)
			throw new IllegalArgumentException("Node must be effective.");
		assert node.hasAsPrevious(getStart());

		LinkedList<N> path = new LinkedList<N>();
		N current = node;
		do {
			path.offerFirst(current);
			current = (N) current.getPrevious();
		} while (current != null);
		return path;
	}

	/**
	 * Get the start node of this algorithm.
	 */
	@Basic
	public N getStart() {
		return start;
	}

	/**
	 * Set the start node for this algorithm.
	 * 
	 * @param start
	 *            The new start node.
	 * 
	 * @post The start node is set to the given node.
	 * @throws IllegalArgumentException
	 *             If the given node is not a valid start node.
	 */
	protected void setStart(N start) throws IllegalArgumentException {
		if (!canHaveAsStart(start))
			throw new IllegalArgumentException("Invalid start node.");
		this.start = start;
	}

	/**
	 * Check whether the given node is a valid start node.
	 * 
	 * @param start
	 *            The node to check.
	 * @return True if and only if the given node is effective and valid.
	 */
	protected boolean canHaveAsStart(N start) {
		return start != null && isValidNode(start);
	}

	/**
	 * Variable registering the start node of this algorithm.
	 */
	private N start;

	/**
	 * Get the target node of this algorithm.
	 */
	@Basic
	public N getTarget() {
		return target;
	}

	/**
	 * Set the target node for this algorithm.
	 * 
	 * @param target
	 *            The new target node.
	 * 
	 * @post The target node is set to the given node.
	 * @throws IllegalArgumentException
	 *             If the given node is not a valid target node.
	 */
	protected void setTarget(N target) {
		if (!canHaveAsTarget(target))
			throw new IllegalArgumentException("Invalid target node.");
		this.target = target;
	}

	/**
	 * Check whether the given node is a valid target node.
	 * 
	 * @param target
	 *            The node to check.
	 * @return True if and only if the given node is effective and valid.
	 */
	protected boolean canHaveAsTarget(N target) {
		return target != null && isValidNode(target);
	}

	/**
	 * Variable registering the target node of this algorithm.
	 */
	private N target;

	/**
	 * Check if the given node is the target.
	 * 
	 * @param node
	 *            The node to check.
	 * @return False if the given node is not effective.
	 */
	public abstract boolean isTarget(N node);

	/**
	 * Get the closed set of this algorithm.
	 */
	@Basic
	protected Set<Point2D> getClosedSet() {
		return closedSet;
	}

	/**
	 * Set of closed nodes.
	 * 
	 * <p>
	 * These nodes have a minimal actual cost to reach them from the start node.
	 * </p>
	 */
	private final Set<Point2D> closedSet = new HashSet<Point2D>();

	/**
	 * Get the open set of this algorithm.
	 */
	@Basic
	protected Queue<N> getOpenSet() {
		return openSet;
	}

	/**
	 * Set the open set of this algorithm.
	 * 
	 * @param openSet
	 *            The new open set.
	 * 
	 * @post The open set is set to the given set.
	 */
	protected void setOpenSet(Queue<N> openSet) {
		this.openSet = openSet;
	}

	/**
	 * Set of open nodes.
	 */
	private Queue<N> openSet;

	/**
	 * Get the map of nodes which have been visited during the execution of the
	 * algorithm.
	 */
	@Basic
	protected Map<Point2D, N> getNodeMap() {
		return Collections.unmodifiableMap(nodeMap);
	}

	/**
	 * Get a node by its position.
	 * 
	 * @param position
	 *            The position.
	 * 
	 * @return The node from the node map at the given position, or null if the
	 *         given position has never been visited during the execution of the
	 *         algorithm.
	 */
	protected N getNodeByPosition(Point2D position) {
		return getNodeMap().get(position);
	}

	/**
	 * Map mapping positions to nodes with the current minimal cost to reach
	 * that position.
	 */
	private Map<Point2D, N> nodeMap = new HashMap<Point2D, N>();

	/**
	 * Check if the given node is a valid node.
	 * 
	 * @param node
	 *            The node to check.
	 */
	public abstract boolean isValidNode(N node);

}
