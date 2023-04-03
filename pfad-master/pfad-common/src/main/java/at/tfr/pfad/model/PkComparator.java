package at.tfr.pfad.model;

import java.util.Comparator;

public class PkComparator<T extends PrimaryKeyHolder> implements Comparator<T> {
	@Override
	public int compare(T o1, T o2) {
		if (o1.getId() == o2.getId())
			return 0;
		if (o1.getId() == null)
			return -1;
		if (o2.getId() == null)
			return +1;
		return o2.getId().compareTo(o1.getId());
	}
}
