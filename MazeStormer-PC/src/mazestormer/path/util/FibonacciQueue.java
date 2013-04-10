package mazestormer.path.util;

import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A priority queue backed by a Fibonacci heap.
 * 
 * @param <E>
 *            The element type.
 * 
 * @author Mattias Buelens
 * @author Thomas Goossens
 * @version 3.0
 */
public class FibonacciQueue<E extends Sortable> extends AbstractQueue<E> {

	private FibonacciHeap<E> heap;
	private Map<E, FibonacciHeap.Node<E>> nodes;

	public FibonacciQueue() {
		heap = new FibonacciHeap<E>();
		nodes = new HashMap<E, FibonacciHeap.Node<E>>();
	}

	@Override
	public boolean offer(E e) {
		if (e == null)
			return false;

		FibonacciHeap.Node<E> node = heap.insert(e, e.getKey());
		nodes.put(node.getData(), node);
		return true;
	}

	@Override
	public E peek() {
		return (heap.isEmpty()) ? null : heap.min().getData();
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;

		nodes.remove(heap.min().getData());
		return heap.removeMin();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return heap.size();
	}

	@Override
	public boolean contains(Object o) {
		return nodes.containsKey(o);
	}

	@Override
	public boolean remove(Object o) {
		FibonacciHeap.Node<E> node = nodes.get(o);
		if (node == null)
			return false;
		heap.delete(node);
		nodes.remove(node);
		return true;
	}

	@Override
	public void clear() {
		heap.clear();
		nodes.clear();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getSimpleName());
		buf.append(heap);
		return buf.toString();
	}

}
