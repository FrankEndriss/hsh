package com.happypeople.hshutil.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Conncats two Iterators<E> to act as one Iterator<E>
 * @param <E>
 */
public class ConcatIterator<E> implements Iterator<E> {
	private Iterator<E> itCurrent;
	private Iterator<E> it2;
	public ConcatIterator(Iterator<E> it1, Iterator<E> it2) {
		this.itCurrent=it1;
		this.it2=it2;
	}

	public boolean hasNext() {
		boolean ret=itCurrent.hasNext();
		if(!ret && itCurrent!=it2) {
			itCurrent=it2;
			return itCurrent.hasNext();
		}
		return ret;
	}

	public E next() {
		if(itCurrent!=it2)
			try {
				return itCurrent.next();
			}catch(NoSuchElementException e) {
				itCurrent=it2;
				return itCurrent.next();
			}
		else
			return itCurrent.next();
	}

	public void remove() {
		itCurrent.remove();
	}
}
