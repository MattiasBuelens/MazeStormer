package mazestormer;

import be.kuleuven.cs.som.annotate.*;

/**
 * An interface that has to be implemented by every class
 * for which the objects could be terminated.
 * 
 * @invar	There may not exist an object that has explicit
 * 			or implicit references, which aren't self references,
 * 			to a terminated object, that is an instance of this
 * 			terminatable class.
 * @invar	A terminated object, that is an instance of this
 * 			terminatable class, may not have implicit or explicit
 * 			references, which aren't self references, to an
 * 			object of this terminatable class.
 * 			Such references are only allowed, if it is sure that
 * 			all of the participating objects involved in the
 * 			'reference' graph are out of reach by every other
 * 			non-participating object.
 * 
 * @version	Pandora: Pandora: A New Hope
 * @author 	Matthias Moulin & Ruben Pieters
 *
 */
public interface Terminatable {

	/**
	 * Terminates this terminatable object.
	 */
	public void terminate();
	
	/**
	 * Checks whether this terminatable object is terminated.
	 */
	@Raw
	public boolean isTerminated();
}
