package com.happypeople.hsh.ls;

import java.util.Iterator;

/** Converts the type of an Iterator
 * @param <I> the input type
 * @param <O> the converted type
 */
public class ConvertedIterator<I, O> implements Iterator<O> {
	private final Iterator<I> delegate;
	private final OneToOneConverter<I, O> converter;

	public ConvertedIterator(final Iterator<I> delegate, final OneToOneConverter<I, O> converter) {
		this.delegate=delegate;
		this.converter=converter;
	}

	public boolean hasNext() {
		return delegate.hasNext();
	}

	public O next() {
		return converter.convert(delegate.next());
	}

	public void remove() {
		delegate.remove();
	}

}
