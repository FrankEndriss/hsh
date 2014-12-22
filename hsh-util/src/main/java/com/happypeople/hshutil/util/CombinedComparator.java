package com.happypeople.hshutil.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Combines a list of Comparator<T> into one Comparator<T>
 * @param <T> the compared type
 */
public class CombinedComparator<T> implements Comparator<T> {
	private final List<Comparator<? super T>> comparators=new ArrayList<Comparator<? super T>>();

	/**
	 * @param comparators the first entry in comparator has highest priority
	 */
	public CombinedComparator(final Iterable<Comparator<? super T>> comparators) {
		for(final Comparator<? super T> c : comparators)
			this.comparators.add(c);
	}

	@Override
	public int compare(final T o1, final T o2) {
		for(final Comparator<? super T> c : comparators) {
			final int res=c.compare(o1, o2);
			if(res!=0)
				return res;
		}
		return 0;
	}


}
