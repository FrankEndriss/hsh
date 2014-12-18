package com.happypeople.hsh.ls;

import java.util.Iterator;

/** Concatenates the iterators of two Iterable<T>
 * @param <T> the type of the Iterables/Iterators.
 */
public class ConcatIterable<T> implements Iterable<T> {
	private final Iterable<T> it1;
	private final Iterable<T> it2;

	public ConcatIterable(final Iterable<T> it1, final Iterable<T> it2) {
		this.it1=it1;
		this.it2=it2;
	}

	public Iterator<T> iterator() {
		return new ConcatIterator<T>(it1.iterator(), it2.iterator());
	}

}
