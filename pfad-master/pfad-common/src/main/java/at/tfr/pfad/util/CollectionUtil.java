package at.tfr.pfad.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtil {

	/**
	 * synchronize two collections (e.g. UI selectMulti passes new Set of options to persistent collection)
	 * @param target e.g. the persistent ManyToOne or ManyToMany reference 
	 * @param newSet the new reference set of elements to establish
	 */
	public static <T> Collection<T> synchronize(Collection<T> target, Collection<T> newSet) {
		Iterator<T> iter = target.iterator();
		while (iter.hasNext()) {
			T next = iter.next();
			if (!newSet.contains(next))
				iter.remove();
		}
		newSet.iterator().forEachRemaining(f -> { if (!target.contains(f)) target.add(f); });
		return target;
	}
}
