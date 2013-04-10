package mazestormer.path.util;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;

/**
 * A sortable object for use in sorted structures.
 * 
 * @author Mattias Buelens
 * @author Thomas Goossens
 * @version 3.0
 */
public interface Sortable {

	/**
	 * Get the key of this sortable object.
	 */
	@Basic
	@Immutable
	public double getKey();

}
