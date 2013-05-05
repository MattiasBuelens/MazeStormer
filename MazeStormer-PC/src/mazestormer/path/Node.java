package mazestormer.path;

import java.awt.geom.Point2D;
import java.util.Collection;

import mazestormer.path.util.Sortable;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * A node in a path.
 * 
 * @param <V>
 *            The cost value type.
 * 
 * @author Mattias Buelens
 * @author Thomas Goossens
 * @version 3.0
 */
public abstract class Node<V extends Comparable<? super V>> implements Comparable<Node<V>>, Sortable {

	/**
	 * Create a node.
	 * 
	 * @param position
	 *            The position for this new node.
	 * 
	 * @post The new node's position is set to the given position.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given position is not effective.
	 */
	@Raw
	@Model
	protected Node(Point2D position) throws IllegalArgumentException {
		if (position == null)
			throw new IllegalArgumentException("Position must be effective.");

		this.position = position;
	}

	/**
	 * Get the position of this node.
	 */
	@Basic
	@Immutable
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Variable registering the position of this node.
	 * 
	 * @invar The position is effective.
	 */
	private final Point2D position;

	/**
	 * Get the total estimated cost from the start node to the target along this
	 * node.
	 */
	public abstract V getF();

	/**
	 * Get the actual cost from the start node to this node.
	 */
	@Basic
	public V getG() {
		return g;
	}

	/**
	 * Set the actual cost from the start node to this node.
	 * 
	 * @param g
	 *            The new actual cost.
	 * 
	 * @post The new actual cost equals the given cost.
	 */
	protected void setG(V g) {
		this.g = g;
	}

	/**
	 * Reset the actual cost from the start node to this node to zero.
	 */
	public abstract void resetG();

	/**
	 * Variable registering the actual cost from the start node to this node.
	 */
	private V g;

	/**
	 * Get the estimated remaining cost from this node to the target.
	 */
	@Basic
	public V getH() {
		return h;
	}

	/**
	 * Set the estimated remaining cost from this node to the target.
	 * 
	 * @param h
	 *            The estimated remaining cost.
	 * 
	 * @post The new estimated remaining cost equals the given cost.
	 */
	protected void setH(V h) {
		this.h = h;
	}

	/**
	 * Variable registering the estimated remaining cost from this node to the
	 * target.
	 */
	private V h;

	/**
	 * Calculate the estimated remaining cost from this node to the given target
	 * node and store the cost in the node.
	 * 
	 * <p>
	 * If the target is not effective, the remaining cost is set to zero.
	 * </p>
	 * 
	 * @param target
	 *            The target node.
	 */
	public abstract void calculateH(Node<V> target);

	/**
	 * Get the previous node.
	 */
	@Basic
	public Node<V> getPrevious() {
		return previous;
	}

	/**
	 * Set the previous node.
	 * 
	 * @param node
	 *            The new previous node.
	 * 
	 * @post The new previous node equals the given node.
	 */
	public void setPrevious(Node<V> node) {
		this.previous = node;
	}

	/**
	 * Variable registering the previous node of this node.
	 */
	private Node<V> previous;

	/**
	 * Check if the given node appears somewhere in this node's ancestor chain.
	 * 
	 * @param node
	 *            The node to check.
	 * 
	 * @return False if the given node is not effective.
	 * @return False if this node has no previous node.
	 * @return True if the previous node of this node equals the given node.
	 * @return Otherwise, true if and only if the previous node of this node has
	 *         the given node in its ancestor chain.
	 */
	public boolean hasAsPrevious(Node<V> node) {
		if (node == null)
			return false;
		Node<V> previous = getPrevious();
		if (previous == null)
			return false;
		if (previous.equals(node))
			return true;
		return previous.hasAsPrevious(node);
	}

	/**
	 * Check if the given node is a neighbor of this node.
	 * 
	 * @param node
	 *            The node to check.
	 * @return True if and only if the set of neighbor nodes of this node
	 *         contains the given node.
	 */
	public boolean isNeighbor(Node<V> node) {
		return getNeighbors().contains(node);
	}

	/**
	 * Get the neighbor nodes of this node.
	 */
	public abstract Collection<? extends Node<V>> getNeighbors();

	/**
	 * @return The total estimated cost is used as sorting key.
	 */
	@Override
	public abstract double getKey();

	/**
	 * @return The nodes are compared on their total estimated cost.
	 */
	@Override
	public abstract int compareTo(Node<V> node);

	/**
	 * @return True if the given object reference equals this object reference.
	 * @return False if the given object is not a node.
	 * @return Otherwise, true if and only if the positions are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Node other = (Node) obj;
		return getPosition().equals(other.getPosition());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * getPosition().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " at " + getPosition();
	}

}
