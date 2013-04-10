/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is GraphMaker. The Initial Developer of the Original
 * Software is Nathan L. Fiedler. Portions created by Nathan L. Fiedler
 * are Copyright (C) 1999-2008. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 */

/*
 * Portions Copyrighted 1999-2008 Nathan L. Fiedler
 * Last change by Mattias Buelens on 26 April 2012
 */

package mazestormer.path.util;

import java.util.Stack;

/**
 * This class implements a Fibonacci heap data structure. Much of the code in
 * this class is based on the algorithms in the "Introduction to Algorithms" by
 * Cormen, Leiserson, and Rivest in Chapter 21. The amortized running time of
 * most of these methods is O(1), making it a very fast data structure. Several
 * have an actual running time of O(1). removeMin() and delete() have O(log n)
 * amortized running times because they do the heap consolidation. If you
 * attempt to store nodes in this heap with key values of -Infinity
 * (Double.NEGATIVE_INFINITY) the <code>delete()</code> operation may fail to
 * remove the correct element.
 * 
 * <p>
 * <b>Note that this implementation is not synchronized.</b> If multiple threads
 * access a set concurrently, and at least one of the threads modifies the set,
 * it <i>must</i> be synchronized externally. This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the set.
 * </p>
 * 
 * @author Nathan Fiedler
 * @author Mattias Buelens
 * @author Thomas Goossens
 * @version 3.0
 */
public class FibonacciHeap<E> {
	/** Points to the minimum node in the heap. */
	protected Node<E> min;
	/** Number of nodes in the heap. */
	protected int n;

	/**
	 * Constructs a FibonacciHeap object that contains no elements.
	 */
	public FibonacciHeap() {
	} // FibonacciHeap

	/**
	 * Performs a cascading cut operation. This cuts y from its parent and then
	 * does the same for its parent, and so on up the tree.
	 * <p>
	 * Running time: O(log n); O(1) excluding the recursion
	 * 
	 * @param y
	 *            node to perform cascading cut on
	 */
	protected void cascadingCut(Node<E> y) {
		Node<E> z = y.parent;
		// if there's a parent...
		if (z != null) {
			// if y is unmarked, set it marked
			if (y.mark == false) {
				y.mark = true;
			} else {
				// it's marked, cut it from parent
				cut(y, z);
				// cut its parent as well
				cascadingCut(z);
			}
		}
	} // cascadingCut

	/**
	 * Removes all elements from this heap.
	 */
	public void clear() {
		min = null;
		n = 0;
	} // clear

	/**
	 * Consolidates the trees in the heap by joining trees of equal degree until
	 * there are no more trees of equal degree in the root list.
	 * <p>
	 * Running time: O(log n) amortized
	 */
	@SuppressWarnings("unchecked")
	protected void consolidate() {
		// Asize equals ceil(log2(n))
		// int Asize = (int)Math.ceil(Math.log(n) / Math.log(2));
		int Asize = n + 1;
		Node<?> A[] = new Node<?>[Asize];
		// Initialize degree array
		for (int i = 0; i < Asize; i++) {
			A[i] = null;
		}
		// Find the number of root nodes.
		int numRoots = 0;
		Node<E> x = min;
		if (x != null) {
			numRoots++;
			x = x.right;
			while (x != min) {
				numRoots++;
				x = x.right;
			}
		}
		// For each node in root list do...
		while (numRoots > 0) {
			// Access this node's degree..
			int d = x.degree;
			Node<E> next = x.right;
			// ..and see if there's another of the same degree.
			while (A[d] != null) {
				// There is, make one of the nodes a child of the other.
				Node<E> y = (Node<E>) A[d];
				// Do this based on the key value.
				if (x.key > y.key) {
					Node<E> temp = y;
					y = x;
					x = temp;
				}
				// Node y disappears from root list.
				link(y, x);
				// We've handled this degree, go to next one.
				A[d] = null;
				d++;
			}
			// Save this node for later when we might encounter another
			// of the same degree.
			A[d] = x;
			// Move forward through list.
			x = next;
			numRoots--;
		}
		// Set min to null (effectively losing the root list) and
		// reconstruct the root list from the array entries in A[].
		min = null;
		for (int i = 0; i < Asize; i++) {
			x = (Node<E>) A[i];
			if (A[i] != null) {
				// We've got a live one, add it to root list.
				if (min != null) {
					// First remove node from root list.
					x.left.right = x.right;
					x.right.left = x.left;
					// Now add to root list, again.
					x.left = min;
					x.right = min.right;
					min.right = x;
					x.right.left = x;
					// Check if this is a new min.
					if (A[i].key < min.key) {
						min = x;
					}
				} else {
					min = x;
				}
			}
		}
	} // consolidate

	/**
	 * The reverse of the link operation: removes x from the child list of y.
	 * This method assumes that min is non-null.
	 * <p>
	 * Running time: O(1)
	 * 
	 * @param x
	 *            child of y to be removed from y's child list
	 * @param y
	 *            parent of x about to lose a child
	 */
	protected void cut(Node<E> x, Node<E> y) {
		// remove x from childlist of y and decrement degree[y]
		x.left.right = x.right;
		x.right.left = x.left;
		y.degree--;
		// reset y.child if necessary
		if (y.child == x) {
			y.child = x.right;
		}
		if (y.degree == 0) {
			y.child = null;
		}
		// add x to root list of heap
		x.left = min;
		x.right = min.right;
		min.right = x;
		x.right.left = x;
		// set parent[x] to nil
		x.parent = null;
		// set mark[x] to false
		x.mark = false;
	} // cut

	/**
	 * Decreases the key value for a heap node, given the new value to take on.
	 * The structure of the heap may be changed and will not be consolidated.
	 * <p>
	 * Running time: O(1) amortized
	 * 
	 * @param x
	 *            node to decrease the key of
	 * @param k
	 *            new key value for node x
	 * @exception IllegalArgumentException
	 *                Thrown if k is larger than x.key value.
	 */
	public void decreaseKey(Node<E> x, double k) {
		if (k > x.key) {
			throw new IllegalArgumentException("decreaseKey() got larger key value");
		}
		x.key = k;
		Node<E> y = x.parent;
		if ((y != null) && (x.key < y.key)) {
			cut(x, y);
			cascadingCut(y);
		}
		if (x.key < min.key) {
			min = x;
		}
	} // decreaseKey

	/**
	 * Deletes a node from the heap given the reference to the node. The trees
	 * in the heap will be consolidated, if necessary. This operation may fail
	 * to remove the correct element if there are nodes with key value
	 * -Infinity.
	 * <p>
	 * Running time: O(log n) amortized
	 * 
	 * @param x
	 *            node to remove from heap
	 */
	public void delete(Node<E> x) {
		// make x as small as possible
		decreaseKey(x, Double.NEGATIVE_INFINITY);
		// remove the smallest, which decreases n also
		removeMin();
	} // delete

	/**
	 * Tests if the Fibonacci heap is empty or not. Returns true if the heap is
	 * empty, false otherwise.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @return true if the heap is empty, false otherwise
	 */
	public boolean isEmpty() {
		return min == null;
	} // isEmpty

	/**
	 * Inserts a new data element into the heap. No heap consolidation is
	 * performed at this time, the new node is simply inserted into the root
	 * list of this heap.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @param x
	 *            data object to insert into heap
	 * @param key
	 *            key value associated with data object
	 * @return newly created heap node
	 */
	public Node<E> insert(E x, double key) {
		Node<E> node = new Node<E>(x, key);
		// concatenate node into min list
		if (min != null) {
			node.left = min;
			node.right = min.right;
			min.right = node;
			node.right.left = node;
			if (key < min.key) {
				min = node;
			}
		} else {
			min = node;
		}
		n++;
		return node;
	} // insert

	/**
	 * Make node y a child of node x.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @param y
	 *            node to become child
	 * @param x
	 *            node to become parent
	 */
	protected void link(Node<E> y, Node<E> x) {
		// remove y from root list of heap
		y.left.right = y.right;
		y.right.left = y.left;
		// make y a child of x
		y.parent = x;
		if (x.child == null) {
			x.child = y;
			y.right = y;
			y.left = y;
		} else {
			y.left = x.child;
			y.right = x.child.right;
			x.child.right = y;
			y.right.left = y;
		}
		// increase degree[x]
		x.degree++;
		// set mark[y] false
		y.mark = false;
	} // link

	/**
	 * Returns the smallest element in the heap. This smallest element is the
	 * one with the minimum key value.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @return heap node with the smallest key
	 */
	public Node<E> min() {
		return min;
	} // min

	/**
	 * Removes the smallest element from the heap. This will cause the trees in
	 * the heap to be consolidated, if necessary.
	 * <p>
	 * Running time: O(log n) amortized
	 * 
	 * @return data object with the smallest key
	 */
	public E removeMin() {
		Node<E> z = min;
		if (z == null)
			return null;

		int numKids = z.degree;
		Node<E> x = z.child;
		Node<E> tempRight;
		// for each child of z do...
		while (numKids > 0) {
			tempRight = x.right;
			// remove x from child list
			x.left.right = x.right;
			x.right.left = x.left;
			// add x to root list of heap
			x.left = min;
			x.right = min.right;
			min.right = x;
			x.right.left = x;
			// set parent[x] to null
			x.parent = null;
			x = tempRight;
			numKids--;
		}
		// remove z from root list of heap
		z.left.right = z.right;
		z.right.left = z.left;
		if (z == z.right) {
			min = null;
		} else {
			min = z.right;
			consolidate();
		}
		// decrement size of heap
		n--;

		return z.data;
	} // removeMin

	/**
	 * Returns the size of the heap which is measured in the number of elements
	 * contained in the heap.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @return number of elements in the heap
	 */
	public int size() {
		return n;
	} // size

	/**
	 * Joins two Fibonacci heaps into a new one. No heap consolidation is
	 * performed at this time. The two root lists are simply joined together.
	 * <p>
	 * Running time: O(1) actual
	 * 
	 * @param H1
	 *            first heap
	 * @param H2
	 *            second heap
	 * @return new heap containing H1 and H2
	 */
	public static <E> FibonacciHeap<E> union(FibonacciHeap<E> H1, FibonacciHeap<E> H2) {
		FibonacciHeap<E> H = new FibonacciHeap<E>();
		if ((H1 != null) && (H2 != null)) {
			H.min = H1.min;
			if (H.min != null) {
				if (H2.min != null) {
					H.min.right.left = H2.min.left;
					H2.min.left.right = H.min.right;
					H.min.right = H2.min;
					H2.min.left = H.min;
					if (H2.min.key < H1.min.key) {
						H.min = H2.min;
					}
				}
			} else {
				H.min = H2.min;
			}
			H.n = H1.n + H2.n;
		}
		return H;
	} // union

	/**
	 * Creates a String representation of this Fibonacci heap.
	 * 
	 * @return String of this.
	 */
	public String toString() {
		if (min == null) {
			return "[]";
		}
		// create a new stack and put root on it
		Stack<Node<E>> stack = new Stack<Node<E>>();
		stack.push(min);

		StringBuffer buf = new StringBuffer(512);
		buf.append("[");
		// do a simple breadth-first traversal on the tree
		while (stack.empty() == false) {
			Node<E> curr = (Node<E>) stack.pop();
			buf.append(curr);
			buf.append(", ");
			if (curr.child != null) {
				stack.push(curr.child);
			}
			Node<E> start = curr;
			curr = curr.right;
			while (curr != start) {
				buf.append(curr);
				buf.append(", ");
				if (curr.child != null) {
					stack.push(curr.child);
				}
				curr = curr.right;
			}
		}
		buf.append(']');
		return buf.toString();
	} // toString

	/**
	 * Implements a node of the Fibonacci heap. It holds the information
	 * necessary for maintaining the structure of the heap. It also holds the
	 * reference to the data element and key value (which is used to determine
	 * the heap structure).
	 * 
	 * @author Nathan Fiedler
	 */
	public static class Node<E> {
		/** the data object for this node, holds the key value */
		private E data;
		/** key value for this node */
		private double key;
		/** parent node */
		private Node<E> parent;
		/** first child node */
		private Node<E> child;
		/** right sibling node */
		private Node<E> right;
		/** left sibling node */
		private Node<E> left;
		/** number of children of this node (does not count grandchildren) */
		private int degree;
		/**
		 * true if this node has had a child removed since this node was added
		 * to its parent
		 */
		private boolean mark;

		/**
		 * Two-arg constructor which sets the data and key fields to the passed
		 * arguments. It also initializes the right and left pointers, making
		 * this a circular doubly-linked list.
		 * 
		 * @param data
		 *            data object to associate with this node
		 * @param key
		 *            key value for this data object
		 */
		public Node(E data, double key) {
			this.data = data;
			this.key = key;
			right = this;
			left = this;
		} // Node

		public E getData() {
			return data;
		} // getData

		public double getKey() {
			return key;
		} // getKey

		/**
		 * Return the string representation of this object. It simply returns
		 * the string representation of the data object. It is up to the data
		 * object to implement toString() properly.
		 * 
		 * @return string representing this object
		 */
		public String toString() {
			// if (true) {
			// return Double.toString(key);
			// } else {
			StringBuffer buf = new StringBuffer();
			buf.append("Node=[parent = ");
			if (parent != null) {
				buf.append(Double.toString(parent.key));
			} else {
				buf.append("---");
			}
			buf.append(", key = ");
			buf.append(Double.toString(key));
			buf.append(", degree = ");
			buf.append(Integer.toString(degree));
			buf.append(", right = ");
			if (right != null) {
				buf.append(Double.toString(right.key));
			} else {
				buf.append("---");
			}
			buf.append(", left = ");
			if (left != null) {
				buf.append(Double.toString(left.key));
			} else {
				buf.append("---");
			}
			buf.append(", child = ");
			if (child != null) {
				buf.append(Double.toString(child.key));
			} else {
				buf.append("---");
			}
			buf.append(']');
			return buf.toString();
			// }
		} // toString
	} // Node<E>
} // FibonacciHeap
